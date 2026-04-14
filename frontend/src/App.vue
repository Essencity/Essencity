<script setup>
import { ref, onMounted, computed } from 'vue'
import TheSidebar from './components/TheSidebar.vue'
import TheHeader from './components/TheHeader.vue'
import CategoryTabs from './components/CategoryTabs.vue'
import MasonryGrid from './components/MasonryGrid.vue'
import CreationPage from './components/CreationPage.vue'
import NotificationPage from './components/NotificationPage.vue'
import AuthModal from './components/AuthModal.vue'
import ProfilePage from './components/ProfilePage.vue'
import PostDetailModal from './components/PostDetailModal.vue'
import CompleteProfileModal from './components/CompleteProfileModal.vue'

const currentView = ref('discovery')
const showAuthModal = ref(false)
const showCompleteProfileModal = ref(false)
const currentUser = ref(null)
const pendingNavigation = ref(null) // Store intended navigation when login is required
const selectedPost = ref(null)
const showPostDetail = ref(false)
const editingPost = ref(null)

// Helper function to get media URL
const getMediaUrl = (url) => {
  if (!url) return null
  // If URL is already a full URL, return as is
  if (url.startsWith('http')) return url
  // If URL already starts with /api, return as is to avoid duplication
  if (url.startsWith('/api')) return url
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

const handleOpenDetail = async (post) => {
  // 如果帖子数据不完整(从收藏/点赞列表来的)，先获取完整数据
  if (!post.author || !post.url) {
    try {
      const response = await fetch(`/api/posts/${post.id}`)
      if (response.ok) {
        const fullPost = await response.json()
        selectedPost.value = {
          id: fullPost.id,
          title: fullPost.title,
          description: fullPost.content || fullPost.description || '',
          image: fullPost.type === 'video' 
            ? (getMediaUrl(fixUrl(fullPost.coverUrl)) || getMediaUrl(fixUrl(fullPost.imageUrl))) 
            : (getMediaUrl(fixUrl(fullPost.imageUrl)) || getMediaUrl(fixUrl(fullPost.url))),
          url: fullPost.type === 'video' ? (getMediaUrl(fixUrl(fullPost.videoUrl)) || getMediaUrl(fixUrl(fullPost.url))) : (getMediaUrl(fixUrl(fullPost.imageUrl)) || getMediaUrl(fixUrl(fullPost.url))),
          videoUrl: fullPost.type === 'video' ? (getMediaUrl(fixUrl(fullPost.videoUrl)) || getMediaUrl(fixUrl(fullPost.url))) : null,
          coverUrl: getMediaUrl(fixUrl(fullPost.coverUrl)) || getMediaUrl(fixUrl(fullPost.imageUrl)),
          imageUrl: getMediaUrl(fixUrl(fullPost.imageUrl)) || getMediaUrl(fixUrl(fullPost.url)),
          imageUrls: fullPost.imageUrls ? fullPost.imageUrls.map(url => getMediaUrl(fixUrl(url))) : null,
          type: fullPost.type || 'image',
          author: fullPost.author?.nickname || '未知用户',
          authorId: fullPost.author?.id,
          authorAvatar: getMediaUrl(fixUrl(fullPost.author?.avatar)),
          authorNickname: fullPost.author?.nickname || '未知用户',
          createdAt: fullPost.createdAt,
          created_at: fullPost.createdAt,
          likeCount: fullPost.likeCount || 0,
          collectionCount: fullPost.collectionCount || 0
        }
        showPostDetail.value = true
        return
      }
    } catch (error) {
      console.error('Failed to fetch post details:', error)
    }
  }
  selectedPost.value = post
  showPostDetail.value = true
}

const handleCloseDetail = () => {
  showPostDetail.value = false
  selectedPost.value = null
}

const handleEditPost = (post) => {
  // 先关闭弹窗
  showPostDetail.value = false
  selectedPost.value = null

  // 延迟设置编辑数据，确保弹窗已关闭
  setTimeout(() => {
    editingPost.value = post
    currentView.value = 'publish'
  }, 100)
}

const handleCreationSuccess = () => {
  editingPost.value = null
  currentView.value = 'discovery'
  fetchPosts()
}

const checkProfileCompletion = (user) => {
  if (user && !user.nickname) {
    showCompleteProfileModal.value = true
  }
}

const handleNavChange = (id) => {
  // Check if trying to publish without login
  if (id === 'publish' && !currentUser.value) {
    pendingNavigation.value = 'publish'
    showAuthModal.value = true
    return
  }
  
  currentView.value = id
  // Re-fetch posts when navigating to discovery
  if (id === 'discovery') {
    fetchPosts()
  }
}

const handleShowLogin = () => {
  showAuthModal.value = true
}

const handleLoginSuccess = (user) => {
  currentUser.value = user
  showAuthModal.value = false
  checkProfileCompletion(user)
  
  // Navigate to pending page if there was one
  if (pendingNavigation.value) {
    currentView.value = pendingNavigation.value
    pendingNavigation.value = null
  }
}

const handleProfileCompleted = (updatedUser) => {
  currentUser.value = updatedUser
  localStorage.setItem('user', JSON.stringify(updatedUser))
  showCompleteProfileModal.value = false
}

const handleLogout = () => {
  currentUser.value = null
  localStorage.removeItem('user')
  currentView.value = 'discovery'
}

const handleUpdateUser = (updatedUser) => {
  currentUser.value = updatedUser
  localStorage.setItem('user', JSON.stringify(updatedUser))
}

const handleBackFromProfile = () => {
  currentView.value = 'discovery'
}

// Check if user is already logged in (from localStorage)
onMounted(() => {
  const savedUser = localStorage.getItem('user')
  if (savedUser) {
    try {
      const user = JSON.parse(savedUser)
      // Fix avatar URL if legacy
      if (user && user.avatar && user.avatar.startsWith('http://localhost:3000')) {
        user.avatar = user.avatar.replace('http://localhost:3000', '/api')
        // Update storage
        localStorage.setItem('user', JSON.stringify(user))
      }
      currentUser.value = user
      checkProfileCompletion(user)
    } catch (e) {
      localStorage.removeItem('user')
    }
  }
})

const posts = ref([])
const currentTag = ref(null)
const loading = ref(false)

const handleCategoryChange = (tag) => {
  currentTag.value = tag === '推荐' ? null : tag
  fetchPosts()
}

const fetchPosts = async (query = '') => {
  try {
    loading.value = true
    let url = '/api/posts'
    const params = []
    
    // 如果是搜索 triggered from Header (query is passed), we should respect it
    if (query && typeof query === 'string') {
        params.push(`title=${encodeURIComponent(query)}`)
    }
    
    // Always apply tag if selected
    if (currentTag.value) {
      params.push(`tag=${encodeURIComponent(currentTag.value)}`)
    }

    if (params.length > 0) {
      url += `?${params.join('&')}`
    }

    const response = await fetch(url)
    if (!response.ok) throw new Error('Failed to fetch posts')
    
    const data = await response.json()
    // Transform data to match PostCard props
    posts.value = data.map(post => {
        // 从API返回的驼峰格式字段获取URL
        const fixedAvatar = fixUrl(post.author?.avatar) || fixUrl(post.user?.avatar) || fixUrl(post.authorAvatar) || '/default-avatar.png'
        const fixedAuthorAvatar = fixUrl(post.author?.avatar) || fixUrl(post.authorAvatar) || '/default-avatar.png'
        // API返回的是驼峰格式的coverUrl、videoUrl和imageUrl字段
        const fixedCoverUrl = fixUrl(post.coverUrl)
        const fixedImageUrl = fixUrl(post.imageUrl || post.url) // 图片类型的URL
        const fixedUrl = fixUrl(post.url) // 通用URL
        const fixedVideoUrl = fixUrl(post.videoUrl || post.url) // 视频类型的URL

        return {
          id: post.id,
          title: post.title,
          description: post.content || post.description || '',
          // Prefer coverUrl for video
          image: post.type === 'video' 
            ? (getMediaUrl(fixedCoverUrl) || getMediaUrl(fixedImageUrl) || '/api/uploads/video_cover_placeholder.svg') 
            : (getMediaUrl(fixedImageUrl) || getMediaUrl(fixedUrl)), 
          videoUrl: post.type === 'video' ? (getMediaUrl(fixedVideoUrl) || getMediaUrl(fixedUrl)) : null,
          url: post.type === 'video' ? (getMediaUrl(fixedVideoUrl) || getMediaUrl(fixedUrl)) : (getMediaUrl(fixedImageUrl) || getMediaUrl(fixedUrl)),
          cover_url: getMediaUrl(fixedCoverUrl) || getMediaUrl(fixedImageUrl),
          coverUrl: getMediaUrl(fixedCoverUrl) || getMediaUrl(fixedImageUrl),
          type: post.type || 'image',
          user: post.author?.nickname || post.user?.nickname || '未知用户',
          avatar: getMediaUrl(fixedAvatar),
          author: post.author?.nickname || post.user?.nickname || '未知用户',
          author_id: post.author?.id || post.authorId,
          authorId: post.author?.id || post.authorId,
          author_avatar: getMediaUrl(fixedAuthorAvatar),
          authorAvatar: getMediaUrl(fixedAuthorAvatar),
          likes: post.likeCount || post.likes || 0,
          createdAt: post.createdAt || post.created_at
        }
    })
  } catch (error) {
    console.error('Error fetching posts:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPosts()
})
</script>

<template>
  <div class="app-container">
    <TheSidebar 
      :active-item="currentView" 
      :current-user="currentUser"
      @change="handleNavChange" 
      @show-login="handleShowLogin"
      @logout="handleLogout"
    />
    
    <div class="main-content">
      <template v-if="currentView === 'discovery'">
        <TheHeader 
          :current-user="currentUser"
          @search="fetchPosts"
        />
        <CategoryTabs @change="handleCategoryChange" />
        <div class="content-scroll">
          <MasonryGrid 
            :items="posts" 
            @open-detail="handleOpenDetail"
          />
        </div>
      </template>

      <template v-else-if="currentView === 'publish'">
        <CreationPage
          :current-user="currentUser"
          :editing-post="editingPost"
          @success="handleCreationSuccess"
        />
      </template>

      <template v-else-if="currentView === 'notification'">
        <NotificationPage :current-user="currentUser" />
      </template>

      <template v-else-if="currentView === 'profile'">
        <ProfilePage 
          :current-user="currentUser" 
          :is-own-profile="true"
          @back="handleBackFromProfile"
          @update-user="handleUpdateUser"
          @open-detail="handleOpenDetail"
        />
      </template>
    </div>

    <!-- Modals -->
    <AuthModal 
      v-if="showAuthModal" 
      @close="showAuthModal = false; pendingNavigation = null"
      @login-success="handleLoginSuccess"
    />

    <PostDetailModal
      v-if="showPostDetail && selectedPost"
      :post="selectedPost"
      :current-user="currentUser"
      @close="handleCloseDetail"
      @interaction="fetchPosts"
      @edit="handleEditPost"
    />

    <CompleteProfileModal
      v-if="showCompleteProfileModal && currentUser"
      :user="currentUser"
      @complete="handleProfileCompleted"
    />
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  background-color: var(--bg-color);
  color: var(--text-primary);
}

.main-content {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  position: relative;
}

.content-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px;
}
</style>
