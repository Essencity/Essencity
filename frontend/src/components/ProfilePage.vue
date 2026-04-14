<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  currentUser: {
    type: Object,
    required: true
  },
  userId: {
    type: String,
    default: null
  },
  isOwnProfile: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update-user', 'back', 'open-user-profile', 'update-stats', 'open-detail'])

const showEditModal = ref(false)
const editNickname = ref('')
const editGender = ref('')
const editBio = ref('')
const editAvatar = ref('')
const avatarInputRef = ref(null)
const isSaving = ref(false)

const genderOptions = ['男', '女', '保密']
const activeTab = ref('notes')

// Helper function to get full image URL
const getImageUrl = (url) => {
  if (!url) return ''
  // If URL is already a full URL or contains default_avatar, return as is
  if (url.startsWith('http') || url.includes('default_avatar')) return url
  // Otherwise, use relative path for API requests
  return `/api${url}`
}

// Helper function to fix URL
const fixUrl = (url) => {
  if (!url) return null
  // If URL already starts with /api, strip it to avoid duplication
  if (url.startsWith('/api')) {
    return url.replace('/api', '')
  }
  // Handle legacy URLs from old backend
  if (url.startsWith('http://localhost:3000')) {
    return url.replace('http://localhost:3000', '')
  }
  // Handle old localhost URLs
  if (url.startsWith('http://localhost:8080/api')) {
    return url.replace('http://localhost:8080/api', '')
  }
  // For relative paths, return as is
  if (url.startsWith('/uploads')) {
    return url
  }
  return url
}

// Helper function to get full image/video URL
const getMediaUrl = (url) => {
  if (!url) return null
  // If URL is already a full URL, return as is
  if (url.startsWith('http')) return url
  // If URL already starts with /api, return as is to avoid duplication
  if (url.startsWith('/api')) return url
  // Otherwise, use relative path for API requests
  return `/api${url}`
}

const tabs = [
  { id: 'notes', label: '笔记' },
  { id: 'favorites', label: '收藏' },
  { id: 'likes', label: '点赞' }
]

const userPosts = ref([])
const favoritePosts = ref([])
const likePosts = ref([])
const isLoading = ref(false)

// Follow functionality
const isFollowing = ref(false)
const isFollowLoading = ref(false)
const showFollowersModal = ref(false)
const showFollowingModal = ref(false)
const followersList = ref([])
const followingList = ref([])
const isLoadingFollowers = ref(false)
const isLoadingFollowing = ref(false)

// Search functionality
const searchQueryFollowers = ref('')
const searchQueryFollowing = ref('')

// Follow status for each user in lists
const followingStatus = ref({})
const followLoading = ref({})

// Filtered lists
const filteredFollowers = computed(() => {
  if (!searchQueryFollowers.value) return followersList.value
  const query = searchQueryFollowers.value.toLowerCase()
  return followersList.value.filter(user => 
    user.nickname.toLowerCase().includes(query)
  )
})

const filteredFollowing = computed(() => {
  if (!searchQueryFollowing.value) return followingList.value
  const query = searchQueryFollowing.value.toLowerCase()
  return followingList.value.filter(user => 
    user.nickname.toLowerCase().includes(query)
  )
})

// Check follow status for a specific user
const checkUserFollowStatus = async (userId) => {
  if (!props.currentUser || props.currentUser.id === userId) return false
  
  try {
    const response = await fetch(`/api/auth/following-status?followerId=${props.currentUser.id}&followingId=${userId}`)
    const data = await response.json()
    followingStatus.value[userId] = data.isFollowing || false
    return data.isFollowing || false
  } catch (error) {
    console.error('Failed to check follow status:', error)
    return false
  }
}

// Follow/unfollow user in list
const handleListFollow = async (userId) => {
  if (!props.currentUser || props.currentUser.id === userId) return
  
  followLoading.value[userId] = true
  
  try {
    const isFollowing = followingStatus.value[userId] || false
    const endpoint = isFollowing ? 'unfollow' : 'follow'
    const response = await fetch(`/api/auth/${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        followerId: props.currentUser.id,
        followingId: userId
      })
    })
    
    const data = await response.json()
    if (data.success) {
      followingStatus.value[userId] = !isFollowing
      fetchUserStats() // 更新统计数据
    } else {
      alert(data.message || '操作失败，请重试')
    }
  } catch (error) {
    console.error('Failed to follow/unfollow:', error)
    alert('网络错误，请重试')
  } finally {
    followLoading.value[userId] = false
  }
}

// Load follow status for all users in the list
const loadFollowStatus = async (users) => {
  if (!props.currentUser) return
  
  for (const user of users) {
    if (user.id !== props.currentUser.id) {
      await checkUserFollowStatus(user.id)
    }
  }
}

const stats = ref({
  following: 0,
  followers: 0,
  likes: 0
})

const fetchUserStats = async () => {
  if (!props.currentUser?.id) return
  
  try {
    // 获取关注数
    const followingResponse = await fetch(`/api/auth/following-count/${props.currentUser.id}`)
    const followingData = await followingResponse.json()
    
    // 获取粉丝数
    const followersResponse = await fetch(`/api/auth/followers-count/${props.currentUser.id}`)
    const followersData = await followersResponse.json()
    
    // 获取获赞与收藏数
    const statsResponse = await fetch(`/api/posts/user/${props.currentUser.id}/stats`)
    const statsData = await statsResponse.json()
    
    // 更新统计数据
    stats.value = {
      following: followingData.count || 0,
      followers: followersData.count || 0,
      likes: (statsData.totalLikes || 0) + (statsData.totalCollections || 0)
    }
  } catch (error) {
    console.error('Failed to fetch user stats:', error)
  }
}

// 在组件挂载或currentUser变化时获取统计数据


// 当关注状态变化时重新获取统计数据
watch(isFollowing, fetchUserStats)

const fetchUserPosts = async () => {
  if (!props.currentUser?.id) return
  try {
    const response = await fetch('/api/posts')
    const posts = await response.json()
    userPosts.value = posts.filter(p => {
      // Filter by ID is safer than name
      return p.author?.id === props.currentUser?.id
    }).map(p => ({
      id: p.id,
      title: p.title,
      // API返回的是驼峰格式的字段
      image: p.type === 'video' 
        ? (getMediaUrl(fixUrl(p.coverUrl)) || getMediaUrl(fixUrl(p.imageUrl)) || '/api/uploads/video_cover_placeholder.svg') 
        : (getMediaUrl(fixUrl(p.imageUrl)) || getMediaUrl(fixUrl(p.url))),
      type: p.type,
      likes: p.likeCount || p.likes || 0
    }))
  } catch (error) {
    console.error('Failed to fetch user posts:', error)
  }
}

const fetchFavoritePosts = async () => {
  if (!props.currentUser?.id) return
  isLoading.value = true
  try {
    const response = await fetch(`/api/posts/user/${props.currentUser.id}/collections`)
    const data = await response.json()
    favoritePosts.value = data.map(c => ({
      id: c.post.id,
      title: c.post.title,
      image: c.post.type === 'video' 
        ? (getMediaUrl(fixUrl(c.post.coverUrl)) || getMediaUrl(fixUrl(c.post.imageUrl)) || '/api/uploads/video_cover_placeholder.svg') 
        : (getMediaUrl(fixUrl(c.post.imageUrl)) || getMediaUrl(fixUrl(c.post.url))),
      type: c.post.type,
      likes: 0
    }))
  } catch (error) {
    console.error('Failed to fetch favorites:', error)
  } finally {
    isLoading.value = false
  }
}

const fetchLikePosts = async () => {
  if (!props.currentUser?.id) return
  isLoading.value = true
  try {
    const response = await fetch(`/api/posts/user/${props.currentUser.id}/likes`)
    const data = await response.json()
    
    likePosts.value = data.map(l => ({
        id: l.post.id,
        title: l.post.title,
        image: l.post.type === 'video' 
          ? (getMediaUrl(fixUrl(l.post.coverUrl)) || getMediaUrl(fixUrl(l.post.imageUrl)) || '/api/uploads/video_cover_placeholder.svg') 
          : (getMediaUrl(fixUrl(l.post.imageUrl)) || getMediaUrl(fixUrl(l.post.url))),
        type: l.post.type,
        likes: 0
      }))
  } catch (error) {
    console.error('Failed to fetch likes:', error)
  } finally {
    isLoading.value = false
  }
}

const formattedUserId = computed(() => {
  const id = props.currentUser?.id || 0
  return String(id).padStart(8, '0')
})

// Check follow status when component mounts or props change
const checkFollowStatus = async () => {
  if (!props.currentUser || props.isOwnProfile) {
    isFollowing.value = false
    return
  }
  
  try {
    const targetUserId = props.userId ? Number(props.userId) : props.currentUser.id
    const response = await fetch(`/api/auth/following-status?followerId=${props.currentUser.id}&followingId=${targetUserId}`)
    const data = await response.json()
    isFollowing.value = data.isFollowing || false
  } catch (error) {
    console.error('Failed to check follow status:', error)
  }
}

// Follow/unfollow user
const handleFollow = async () => {
  if (!props.currentUser || props.isOwnProfile) return
  
  const targetUserId = props.userId ? Number(props.userId) : props.currentUser.id
  // 确保不会关注自己
  if (targetUserId === props.currentUser.id) return
  
  isFollowLoading.value = true
  
  try {
    const endpoint = isFollowing.value ? 'unfollow' : 'follow'
    const response = await fetch(`/api/auth/${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        followerId: props.currentUser.id,
        followingId: targetUserId
      })
    })
    
    const data = await response.json()
    if (data.success) {
      isFollowing.value = !isFollowing.value
      emit('update-stats', { isFollowing: isFollowing.value })
    } else {
      alert(data.message || '操作失败，请重试')
    }
  } catch (error) {
    console.error('Failed to follow/unfollow:', error)
    alert('网络错误，请重试')
  } finally {
    isFollowLoading.value = false
  }
}

// Load followers list
const loadFollowers = async () => {
  const targetUserId = props.userId ? Number(props.userId) : props.currentUser.id
  isLoadingFollowers.value = true
  
  try {
    const response = await fetch(`/api/auth/followers/${targetUserId}`)
    const data = await response.json()
    followersList.value = data || []
    
    // Load follow status for all followers
    if (props.currentUser) {
      await loadFollowStatus(followersList.value)
    }
  } catch (error) {
    console.error('Failed to load followers:', error)
  } finally {
    isLoadingFollowers.value = false
  }
}

// Load following list
const loadFollowing = async () => {
  const targetUserId = props.userId ? Number(props.userId) : props.currentUser.id
  isLoadingFollowing.value = true
  
  try {
    const response = await fetch(`/api/auth/following/${targetUserId}`)
    const data = await response.json()
    followingList.value = data || []
    
    // Load follow status for all following users
    if (props.currentUser) {
      await loadFollowStatus(followingList.value)
    }
  } catch (error) {
    console.error('Failed to load following:', error)
  } finally {
    isLoadingFollowing.value = false
  }
}

// Open followers modal
const openFollowersModal = () => {
  loadFollowers()
  showFollowersModal.value = true
}

// Open following modal
const openFollowingModal = () => {
  loadFollowing()
  showFollowingModal.value = true
}

// Close modals
const closeFollowersModal = () => {
  showFollowersModal.value = false
}

const closeFollowingModal = () => {
  showFollowingModal.value = false
}

// Navigate to user profile
const navigateToUserProfile = (userId) => {
  emit('open-user-profile', userId)
  closeFollowersModal()
  closeFollowingModal()
}

watch(activeTab, (newTab) => {
  // 每次切换标签都重新获取数据，确保URL处理逻辑生效
  if (newTab === 'favorites') {
    fetchFavoritePosts()
  } else if (newTab === 'likes') {
    fetchLikePosts()
  }
})

// Check follow status when component mounts or props change
watch(() => [props.currentUser, props.userId, props.isOwnProfile], checkFollowStatus, { immediate: true, deep: true })

// 在组件挂载或currentUser变化时获取统计数据
watch(() => props.currentUser, () => {
  fetchUserStats()
  fetchUserPosts()
}, { immediate: true, deep: true })

const getDisplayPosts = computed(() => {
  if (activeTab.value === 'notes') return userPosts.value
  if (activeTab.value === 'favorites') return favoritePosts.value
  if (activeTab.value === 'likes') return likePosts.value
  return []
})

const openEditModal = () => {
  editNickname.value = props.currentUser?.nickname || ''
  editGender.value = props.currentUser?.gender || '保密'
  editBio.value = props.currentUser?.bio || ''
  editAvatar.value = props.currentUser?.avatar || ''
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
}

const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (file) {
    try {
      // 显示加载状态
      isSaving.value = true
      
      // 创建FormData对象
      const formData = new FormData()
      formData.append('file', file)
      
      // 上传文件到后端
      const response = await fetch('/api/uploads', {
        method: 'POST',
        body: formData
      })
      
      if (response.ok) {
        const data = await response.json()
        // 保存返回的文件URL
        editAvatar.value = data.url
      } else {
        const errorData = await response.json().catch(() => ({ message: '上传失败' }))
        alert(errorData.message || '头像上传失败，请重试')
      }
    } catch (error) {
      console.error('Failed to upload avatar:', error)
      alert('头像上传失败，请重试')
    } finally {
      isSaving.value = false
    }
  }
}

const saveProfile = async () => {
  try {
    isSaving.value = true
    const response = await fetch('/api/auth/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: props.currentUser.id,
        nickname: editNickname.value,
        gender: editGender.value,
        avatar: editAvatar.value,
        bio: editBio.value
      })
    })

    if (response.ok) {
      const updatedUser = await response.json()
      emit('update-user', updatedUser)
      showEditModal.value = false
    } else {
      const errorData = await response.json().catch(() => ({ message: '保存失败，请重试' }))
      alert(errorData.message || '保存失败，请重试')
    }
  } catch (error) {
    console.error('Failed to update profile:', error)
    alert('网络错误，请重试')
  } finally {
    isSaving.value = false
  }
}

const handlePostClick = (post) => {
  emit('open-detail', post)
}

</script>

<template>
  <div class="profile-page">
    <!-- Profile Header Section -->
    <div class="profile-header">
      <div class="avatar-section">
        <img :src="getImageUrl(currentUser.avatar)" alt="头像" class="profile-avatar" />
      </div>
      
      <div class="info-section">
        <div class="name-row">
          <h1 class="nickname">{{ currentUser.nickname }}</h1>
          <template v-if="isOwnProfile">
            <button class="edit-btn" @click="openEditModal">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <circle cx="5" cy="12" r="2"/>
                <circle cx="12" cy="12" r="2"/>
                <circle cx="19" cy="12" r="2"/>
              </svg>
            </button>
          </template>
          <template v-else-if="currentUser && userId && currentUser.id !== userId">
            <button
              class="follow-btn"
              :class="{ 'following': isFollowing }"
              @click="handleFollow"
              :disabled="isFollowLoading"
            >
              <span v-if="isFollowLoading" class="follow-btn-loading">
                <svg class="loading-spinner" viewBox="0 0 24 24" fill="currentColor">
                  <circle class="spinner-path" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" stroke-linecap="round"/>
                </svg>
                处理中...
              </span>
              <span v-else-if="isFollowing">已关注</span>
              <span v-else>关注</span>
            </button>
          </template>
        </div>
        
        <p class="user-id">趣生活号：{{ formattedUserId }}</p>
        
        <p class="bio">{{ currentUser.bio || '还没有简介' }}</p>
        
        <div 
          class="gender-tag" 
          v-if="currentUser.gender && currentUser.gender !== '保密'"
          :class="{ 
            'female': currentUser.gender === '女',
            'male': currentUser.gender === '男' 
          }"
        >
          <svg v-if="currentUser.gender === '女'" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C9.24 2 7 4.24 7 7c0 2.52 1.86 4.59 4.28 4.94V14H9v2h2.28v3h1.43v-3H15v-2h-2.28v-2.06C15.14 11.59 17 9.52 17 7c0-2.76-2.24-5-5-5zm0 8c-1.65 0-3-1.35-3-3s1.35-3 3-3 3 1.35 3 3-1.35 3-3 3z"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C9.24 2 7 4.24 7 7c0 2.76 2.24 5 5 5s5-2.24 5-5c0-2.76-2.24-5-5-5zm0 8c-1.65 0-3-1.35-3-3s1.35-3 3-3 3 1.35 3 3-1.35 3-3 3zm4 4h-2.06c-.48 1.72-1.25 3.3-2.25 4.57-.55.7-.88 1.16-.88 1.16L12 21l1.19-1.27s0 0-.02-.02c-.2-.25-.37-.5-.55-.76.99-1.26 1.76-2.84 2.25-4.55H17v-1.4h-3V13h-2v.4H9v1.4z"/>
          </svg>
        </div>
        
        <div class="stats-row">
          <div class="stat-item" @click="openFollowingModal">
            <span class="stat-number">{{ stats.following }}</span>
            <span class="stat-label">关注</span>
          </div>
          <div class="stat-item" @click="openFollowersModal">
            <span class="stat-number">{{ stats.followers }}</span>
            <span class="stat-label">粉丝</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.likes }}</span>
            <span class="stat-label">获赞与收藏</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Tab Navigation -->
    <div class="tabs-container">
      <div class="tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          class="tab-btn"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <!-- Content Area -->
    <div class="content-area">
      <template v-if="activeTab === 'notes'">
        <div v-if="userPosts.length > 0" class="posts-grid">
          <div v-for="post in userPosts" :key="post.id" class="post-item" @click="handlePostClick(post)">
            <img :src="post.image" :alt="post.title" class="post-image" />
            <p class="post-title">{{ post.title }}</p>
          </div>
        </div>
        <div v-else class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 80 80" fill="none">
              <circle cx="40" cy="35" r="20" stroke="#ddd" stroke-width="2"/>
              <path d="M25 60 Q40 75 55 60" stroke="#ddd" stroke-width="2" fill="none"/>
            </svg>
          </div>
          <p class="empty-text">TA 还没有发布任何内容哦</p>
        </div>
      </template>
      
      <template v-else-if="activeTab === 'favorites'">
        <div v-if="isLoading" class="loading-state">
          <p class="loading-text">加载中...</p>
        </div>
        <div v-else-if="favoritePosts.length > 0" class="posts-grid">
          <div v-for="post in favoritePosts" :key="post.id" class="post-item" @click="handlePostClick(post)">
            <img :src="post.image" :alt="post.title" class="post-image" />
            <p class="post-title">{{ post.title }}</p>
          </div>
        </div>
        <div v-else class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 80 80" fill="none">
              <circle cx="40" cy="35" r="20" stroke="#ddd" stroke-width="2"/>
              <path d="M25 60 Q40 75 55 60" stroke="#ddd" stroke-width="2" fill="none"/>
            </svg>
          </div>
          <p class="empty-text">TA 还没有收藏任何内容</p>
        </div>
      </template>
      
      <template v-else-if="activeTab === 'likes'">
        <div v-if="isLoading" class="loading-state">
          <p class="loading-text">加载中...</p>
        </div>
        <div v-else-if="likePosts.length > 0" class="posts-grid">
          <div v-for="post in likePosts" :key="post.id" class="post-item" @click="handlePostClick(post)">
            <img :src="post.image" :alt="post.title" class="post-image" />
            <p class="post-title">{{ post.title }}</p>
          </div>
        </div>
        <div v-else class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 80 80" fill="none">
              <circle cx="40" cy="35" r="20" stroke="#ddd" stroke-width="2"/>
              <path d="M25 60 Q40 75 55 60" stroke="#ddd" stroke-width="2" fill="none"/>
            </svg>
          </div>
          <p class="empty-text">TA 还没有点赞任何内容</p>
        </div>
      </template>
    </div>

    <!-- Edit Modal -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
      <div class="edit-modal">
        <div class="modal-header">
          <button class="modal-close" @click="closeEditModal">取消</button>
          <h2>编辑资料</h2>
          <button class="modal-save" type="button" @click="saveProfile" :disabled="isSaving">
            <span v-if="isSaving" class="btn-loading">
              <svg class="loading-spinner" viewBox="0 0 24 24" fill="currentColor">
                <circle class="spinner-path" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" stroke-linecap="round"/>
              </svg>
              保存中...
            </span>
            <span v-else>保存</span>
          </button>
        </div>
        
        <div class="modal-content">
          <!-- Avatar Edit -->
          <div class="avatar-edit-section">
            <div class="avatar-preview" @click="triggerAvatarUpload">
              <img :src="getImageUrl(editAvatar)" alt="头像" />
              <div class="avatar-overlay">
                <svg viewBox="0 0 24 24" fill="white">
                  <path d="M21 6h-4l-2-2H9L7 6H3v14h18V6zm-9 11c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5z"/>
                </svg>
                <span>更换头像</span>
              </div>
            </div>
            <input 
              ref="avatarInputRef"
              type="file" 
              accept="image/*" 
              @change="handleAvatarChange" 
              hidden 
            />
          </div>

          <div class="form-group">
            <label>昵称</label>
            <input v-model="editNickname" type="text" placeholder="请输入昵称" maxlength="20" />
          </div>
          
          <div class="form-group">
            <label>性别</label>
            <div class="gender-select">
              <button 
                v-for="opt in genderOptions" 
                :key="opt"
                class="gender-option"
                :class="{ selected: editGender === opt }"
                @click="editGender = opt"
              >
                {{ opt }}
              </button>
            </div>
          </div>
          
          <div class="form-group">
            <label>简介</label>
            <textarea v-model="editBio" placeholder="介绍一下自己吧" maxlength="100"></textarea>
          </div>
        </div>
      </div>
    </div>

    <!-- Followers Modal -->
    <div v-if="showFollowersModal" class="modal-overlay" @click.self="closeFollowersModal">
      <div class="follow-modal">
        <div class="modal-header">
          <button class="modal-close" @click="closeFollowersModal">取消</button>
          <h2>粉丝</h2>
        </div>
        
        <div class="modal-content">
          <!-- Search Bar -->
          <div class="search-bar">
            <svg class="search-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
            <input 
              type="text" 
              v-model="searchQueryFollowers" 
              placeholder="搜索粉丝"
              class="search-input"
            />
          </div>
          
          <div v-if="isLoadingFollowers" class="loading-state">
            <p class="loading-text">加载中...</p>
          </div>
          <div v-else-if="filteredFollowers.length === 0" class="empty-state">
            <p class="empty-text">
              {{ searchQueryFollowers ? '未找到匹配的粉丝' : '暂无粉丝' }}
            </p>
          </div>
          <div v-else class="followers-list">
            <transition-group name="list" tag="div">
              <div 
                v-for="(user, index) in filteredFollowers" 
                :key="user.id" 
                class="follow-item"
                :class="{ 'animate-enter': true }"
                :style="{ '--index': index }"
              >
                <div class="follow-item-content" @click="navigateToUserProfile(user.id)">
                  <img :src="getImageUrl(user.avatar)" :alt="user.nickname" class="follow-avatar" />
                  <div class="follow-info">
                    <div class="follow-name-row">
                      <span class="follow-nickname">{{ user.nickname }}</span>
                      <span v-if="user.gender && user.gender !== '保密'" class="gender-badge">
                        <svg v-if="user.gender === '女'" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2C9.24 2 7 4.24 7 7c0 2.52 1.86 4.59 4.28 4.94V14H9v2h2.28v3h1.43v-3H15v-2h-2.28v-2.06C15.14 11.59 17 9.52 17 7c0-2.76-2.24-5-5-5zm0 8c-1.65 0-3-1.35-3-3s1.35-3 3-3 3 1.35 3 3-1.35 3-3 3z"/>
                        </svg>
                        <svg v-else viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2C9.24 2 7 4.24 7 7c0 2.76 2.24 5 5 5s5-2.24 5-5c0-2.76-2.24-5-5-5zm0 8c-1.65 0-3-1.35-3-3s1.35-3 3-3 3 1.35 3 3-1.35 3-3 3zm4 4h-2.06c-.48 1.72-1.25 3.3-2.25 4.57-.55.7-.88 1.16-.88 1.16L12 21l1.19-1.27s0 0-.02-.02c-.2-.25-.37-.5-.55-.76.99-1.26 1.76-2.84 2.25-4.55H17v-1.4h-3V13h-2v.4H9v1.4z"/>
                        </svg>
                      </span>
                    </div>
                    <span class="follow-id">趣生活号：{{ String(user.id).padStart(8, '0') }}</span>
                    <p class="follow-bio" v-if="user.bio">{{ user.bio }}</p>
                  </div>
                </div>
                <button 
                  v-if="user.id !== currentUser?.id"
                  class="follow-item-btn" 
                  :class="{ 'following': followingStatus[user.id] }"
                  @click.stop="handleListFollow(user.id)"
                  :disabled="followLoading[user.id]"
                >
                  <span v-if="followLoading[user.id]" class="btn-loading">
                    <svg class="loading-spinner" viewBox="0 0 24 24" fill="currentColor">
                      <circle class="spinner-path" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" stroke-linecap="round"/>
                    </svg>
                  </span>
                  <span v-else-if="followingStatus[user.id]">已关注</span>
                  <span v-else>关注</span>
                </button>
              </div>
            </transition-group>
          </div>
        </div>
      </div>
    </div>

    <!-- Following Modal -->
    <div v-if="showFollowingModal" class="modal-overlay" @click.self="closeFollowingModal">
      <div class="follow-modal">
        <div class="modal-header">
          <button class="modal-close" @click="closeFollowingModal">取消</button>
          <h2>关注</h2>
        </div>
        
        <div class="modal-content">
          <!-- Search Bar -->
          <div class="search-bar">
            <svg class="search-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
            <input 
              type="text" 
              v-model="searchQueryFollowing" 
              placeholder="搜索关注的人"
              class="search-input"
            />
          </div>
          
          <div v-if="isLoadingFollowing" class="loading-state">
            <p class="loading-text">加载中...</p>
          </div>
          <div v-else-if="filteredFollowing.length === 0" class="empty-state">
            <p class="empty-text">
              {{ searchQueryFollowing ? '未找到匹配的用户' : '暂无关注' }}
            </p>
          </div>
          <div v-else class="followers-list">
            <transition-group name="list" tag="div">
              <div 
                v-for="(user, index) in filteredFollowing" 
                :key="user.id" 
                class="follow-item"
                :class="{ 'animate-enter': true }"
                :style="{ '--index': index }"
              >
                <div class="follow-item-content" @click="navigateToUserProfile(user.id)">
                  <img :src="getImageUrl(user.avatar)" :alt="user.nickname" class="follow-avatar" />
                  <div class="follow-info">
                    <div class="follow-name-row">
                      <span class="follow-nickname">{{ user.nickname }}</span>
                      <span v-if="user.gender && user.gender !== '保密'" class="gender-badge">
                        <svg v-if="user.gender === '女'" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2C9.24 2 7 4.24 7 7c0 2.52 1.86 4.59 4.28 4.94V14H9v2h2.28v3h1.43v-3H15v-2h-2.28v-2.06C15.14 11.59 17 9.52 17 7c0-2.76-2.24-5-5-5zm0 8c-1.65 0-3-1.35-3-3s1.35-3 3-3 3 1.35 3 3-1.35 3-3 3z"/>
                        </svg>
                        <svg v-else viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2C9.24 2 7 4.24 7 7c0 2.76 2.24 5 5 5s5-2.24 5-5c0-2.76-2.24-5-5-5zm0 8c-1.65 0-3-1.35-3-3s1.35-3 3-3 3 1.35 3 3-1.35 3-3 3zm4 4h-2.06c-.48 1.72-1.25 3.3-2.25 4.57-.55.7-.88 1.16-.88 1.16L12 21l1.19-1.27s0 0-.02-.02c-.2-.25-.37-.5-.55-.76.99-1.26 1.76-2.84 2.25-4.55H17v-1.4h-3V13h-2v.4H9v1.4z"/>
                        </svg>
                      </span>
                    </div>
                    <span class="follow-id">趣生活号：{{ String(user.id).padStart(8, '0') }}</span>
                    <p class="follow-bio" v-if="user.bio">{{ user.bio }}</p>
                  </div>
                </div>
                <button 
                  v-if="user.id !== currentUser?.id"
                  class="follow-item-btn" 
                  :class="{ 'following': followingStatus[user.id] }"
                  @click.stop="handleListFollow(user.id)"
                  :disabled="followLoading[user.id]"
                >
                  <span v-if="followLoading[user.id]" class="btn-loading">
                    <svg class="loading-spinner" viewBox="0 0 24 24" fill="currentColor">
                      <circle class="spinner-path" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" stroke-linecap="round"/>
                    </svg>
                  </span>
                  <span v-else-if="followingStatus[user.id]">已关注</span>
                  <span v-else>关注</span>
                </button>
              </div>
            </transition-group>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  background: #ffffff;
  min-height: 100vh;
}

/* 深色模式 */
[data-theme="dark"] .profile-page {
  background: #1a1a1a;
}

/* Profile Header */
.profile-header {
  display: flex;
  gap: 32px;
  padding: 40px 60px;
  border-bottom: 1px solid var(--border-color);
}

/* 深色模式 */
[data-theme="dark"] .profile-header {
  border-bottom: 1px solid #333;
}

.avatar-section {
  flex-shrink: 0;
}

.profile-avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
}

.info-section {
  flex: 1;
  min-width: 0;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}

.nickname {
  font-size: 28px;
  font-weight: 600;
  color: #333333;
  margin: 0;
}

/* 深色模式 */
[data-theme="dark"] .nickname {
  color: #ffffff;
}

.edit-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  border-radius: 50%;
  transition: background 0.2s;
}

.edit-btn:hover {
  background: var(--bg-color);
}

.edit-btn svg {
  width: 20px;
  height: 20px;
}

.user-id {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 12px 0;
}

/* Follow Button */
.follow-btn {
  padding: 10px 24px;
  border-radius: 20px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #ff2442 0%, #ff7a85 100%);
  color: white;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.follow-btn:hover:not(:disabled):not(.following) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 36, 66, 0.3);
  background: linear-gradient(135deg, #ff4a62 0%, #ff939d 100%);
}

.follow-btn:active:not(:disabled):not(.following) {
  transform: translateY(0);
}

.follow-btn.following {
  background: var(--bg-color);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.follow-btn.following:hover:not(:disabled) {
  background: rgba(var(--primary-color-rgb), 0.05);
  color: var(--primary-color);
  border-color: var(--primary-color);
}

.follow-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.follow-btn-loading {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  animation: spin 1s linear infinite;
}

.spinner-path {
  stroke-dasharray: 50; /* 调整这个值来改变动画效果 */
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.bio {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 8px 0;
}

.gender-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background: var(--bg-color);
  border-radius: 50%;
  margin-bottom: 16px;
}

.gender-tag.male {
  background: rgba(93, 146, 255, 0.1);
}

.gender-tag.female {
  background: rgba(255, 87, 169, 0.1);
}

.gender-tag svg {
  width: 12px;
  height: 12px;
  color: var(--text-secondary);
}

.gender-tag.male svg {
  color: #5d92ff;
}

.gender-tag.female svg {
  color: #ff57a9;
}

.stats-row {
  display: flex;
  gap: 24px;
  margin-top: 16px;
}

.stat-item {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stat-number {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 13px;
  color: #999;
}

/* 深色模式 */
[data-theme="dark"] .stat-number {
  color: #fff;
}

[data-theme="dark"] .stat-label {
  color: #777;
}

/* Tabs */
.tabs-container {
  border-bottom: 1px solid #e0e0e0;
  padding: 0 60px;
}

.tabs {
  display: flex;
  gap: 32px;
}

.tab-btn {
  padding: 16px 8px;
  font-size: 15px;
  color: #666;
  position: relative;
  transition: color 0.2s;
}

.tab-btn:hover {
  color: #333;
}

.tab-btn.active {
  color: #333;
  font-weight: 600;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background: #333333;
  border-radius: 2px;
}

/* 深色模式 */
[data-theme="dark"] .tabs-container {
  border-bottom: 1px solid #333;
}

[data-theme="dark"] .tab-btn {
  color: #777;
}

[data-theme="dark"] .tab-btn:hover {
  color: #ccc;
}

[data-theme="dark"] .tab-btn.active {
  color: #fff;
}

[data-theme="dark"] .tab-btn.active::after {
  background: #fff;
}

/* Content Area */
.content-area {
  padding: 60px;
  display: flex;
  justify-content: center;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

/* 深色模式 */
[data-theme="dark"] .empty-icon svg {
  stroke: #555555;
}

.empty-text {
  font-size: 14px;
  color: #999999;
}

/* 深色模式 */
[data-theme="dark"] .empty-text {
  color: #777777;
}

/* Posts Grid */
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
  width: 100%;
}

.post-item {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.post-item:hover {
  transform: translateY(-4px);
}

.post-image {
  width: 100%;
  height: 220px;
  object-fit: cover;
  border-radius: 8px;
  background: #f0f0f0;
}

/* 深色模式 */
[data-theme="dark"] .post-image {
  background: #2a2a2a;
}

.post-title {
  font-size: 13px;
  color: #333333;
  margin: 8px 0 0 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 深色模式 */
[data-theme="dark"] .post-title {
  color: #ffffff;
}

.loading-state {
  text-align: center;
  padding: 40px;
}

.loading-text {
  color: #999;
  font-size: 14px;
}

/* Edit Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.edit-modal {
  background: #f8f8f8;
  border-radius: 16px;
  width: 400px;
  max-width: 90%;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

/* 深色模式 */
[data-theme="dark"] .edit-modal {
  background: #2a2a2a;
  border: 1px solid #404040;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h2 {
  font-size: 17px;
  font-weight: 600;
  margin: 0;
}

.modal-close,
.modal-save {
  font-size: 15px;
  padding: 4px 8px;
}

.modal-close {
  color: #666;
}

.modal-save {
  color: #ff2442;
  font-weight: 500;
}

.modal-save:hover {
  opacity: 0.8;
}

.modal-content {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #ff2442;
}

.form-group textarea {
  height: 80px;
  resize: none;
}

/* Search Bar */
.search-bar {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 20px;
  padding: 8px 16px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.search-bar:focus-within {
  background: var(--white);
  box-shadow: var(--shadow-md);
}

.search-icon {
  width: 16px;
  height: 16px;
  color: var(--text-secondary);
  margin-right: 8px;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 14px;
  outline: none;
  color: var(--text-primary);
}

.search-input::placeholder {
  color: var(--text-secondary);
}

.gender-select {
  display: flex;
  gap: 12px;
}

.gender-option {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.gender-option:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.gender-option.selected {
  background: #ff2442;
  border-color: #ff2442;
  color: white;
}

/* Avatar Edit Section */
.avatar-edit-section {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.avatar-preview {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-preview:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay svg {
  width: 24px;
  height: 24px;
  margin-bottom: 4px;
}

.avatar-overlay span {
  font-size: 11px;
  color: white;
}

/* Follow Modal */
.follow-modal {
  background: #f8f8f8;
  border-radius: 16px;
  width: 400px;
  max-width: 90%;
  max-height: 80vh;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

/* 深色模式 */
[data-theme="dark"] .follow-modal {
  background: #2a2a2a;
  border: 1px solid #404040;
}

.followers-list {
  overflow-y: auto;
  max-height: calc(80vh - 120px);
  padding-right: 8px;
}

.followers-list::-webkit-scrollbar {
  width: 6px;
}

.followers-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.followers-list::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.followers-list::-webkit-scrollbar-thumb:hover {
  background: #999;
}

.follow-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.follow-item:last-child {
  border-bottom: none;
}

.follow-item-content {
  display: flex;
  align-items: center;
  cursor: pointer;
  flex: 1;
}

.follow-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.follow-info {
  flex: 1;
  min-width: 0;
}

.follow-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.follow-nickname {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.gender-badge {
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  border-radius: 50%;
}

.gender-badge svg {
  width: 12px;
  height: 12px;
  color: #999;
}

.follow-id {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.follow-bio {
  font-size: 13px;
  color: #666;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Animation for list items */
.animate-enter {
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
  transform: translateY(10px);
  animation-delay: calc(var(--index) * 0.05s);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Follow Item Button */
.follow-item-btn {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  background: linear-gradient(135deg, #ff2442 0%, #ff7a85 100%);
  color: white;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  min-width: 70px;
  overflow: hidden;
}

.follow-item-btn:hover:not(:disabled):not(.following) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 36, 66, 0.3);
  background: linear-gradient(135deg, #ff4a62 0%, #ff939d 100%);
}

.follow-item-btn:active:not(:disabled):not(.following) {
  transform: translateY(0);
}

.follow-item-btn.following {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #e0e0e0;
}

.follow-item-btn.following:hover:not(:disabled) {
  background: #f0f0f0;
  color: #ff2442;
  border-color: #ff2442;
}

.follow-item-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-loading {
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-loading .loading-spinner {
  width: 12px;
  height: 12px;
}

/* Transition Group */
.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.list-move {
  transition: transform 0.3s ease;
}
</style>
