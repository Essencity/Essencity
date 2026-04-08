<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import AiSummary from './AiSummary.vue'

const props = defineProps({
  post: {
    type: Object,
    required: true
  },
  currentUser: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'interaction'])

const videoRef = ref(null)
const commentInputRef = ref(null)
const isPlaying = ref(false)
const isLiked = ref(props.post.likeStatus || false)
const isCollected = ref(props.post.collectionStatus || false)
const likeCount = ref(props.post.likeCount || props.post.likes || 0)
const collectCount = ref(props.post.collectionCount || 0)
const comments = ref([])
const newComment = ref('')
const isLoadingComments = ref(false)
const replyToCommentId = ref(null)
const replyToNickname = ref(null)
const showDeleteMenu = ref(null)
const showAiSummary = ref(false)
const showPostDeleteMenu = ref(false)

const handleDeletePost = async () => {
  if (!props.currentUser) return

  try {
    await fetch(`/api/posts/${props.post.id}`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' }
    })
    showPostDeleteMenu.value = false
    emit('interaction')
    emit('close')
  } catch (error) {
    console.error('Failed to delete post:', error)
  }
}
const commentLikes = ref({})

// 默认头像base64
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iI2NjYyI+PHBhdGggZD0iTTEyIDJDNC40OCAyIDIgNi40OCAyIDEyczQuNDggMTAgMTAgMTAgMTAtNC40OCAxMC0xMFMxNy41MiAyIDEyIDJ6bTAgM2MxLjY2IDAgMyAxLjM0IDMgM3MtMS4zNCAzLTMgMy0zLTEuMzQtMy0zIDEuMzQtMyAzLTN6bTAgMTQuMmMtMi41IDAtNC43MS0xLjI4LTYtMy4yMi4wMy0xLjk5IDQtMy4wOCA2LTMuMDggMS45OSAwIDUuOTcgMS4wOSA2IDMuMDgtMS4yOSAxLjk0LTMuNSAzLjIyLTYgMy4yMnoiLz48L3N2Zz4='

// Helper function to get avatar URL
const getAvatar = (url) => {
  if (!url || url.includes('default_avatar')) return defaultAvatar
  // If URL is already a full URL, return as is
  if (url.startsWith('http')) return url
  // If URL already starts with /api, return as is to avoid duplication
  if (url.startsWith('/api')) return url
  // Otherwise, use relative path for API requests
  return `/api${url}`
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


const formattedLikes = computed(() => {
  const num = likeCount.value
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num
})

const formattedCollects = computed(() => {
  const num = collectCount.value
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num
})

// 计算总评论数（主评论 + 子回复）
const commentCount = computed(() => {
  let count = comments.value.length
  comments.value.forEach(comment => {
    if (comment.subComments && comment.subComments.length > 0) {
      count += comment.subComments.length
    }
  })
  return count
})

const formattedCommentCount = computed(() => {
  const num = commentCount.value
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num
})

// Follow functionality
const isFollowing = ref(false)
const isFollowLoading = ref(false)

const togglePlay = () => {
  if (videoRef.value) {
    if (isPlaying.value) {
      videoRef.value.pause()
    } else {
      videoRef.value.play()
    }
    isPlaying.value = !isPlaying.value
  }
}

const handleVideoEnded = () => {
  isPlaying.value = false
}

const closeModal = () => {
  emit('close')
}

const fetchComments = async () => {
  if (!props.post.id) return
  
  isLoadingComments.value = true
  try {
    const response = await fetch(`/api/posts/${props.post.id}/comments`)
    const data = await response.json()
    
    // 防御性检查：确保data是数组
    if (!Array.isArray(data)) {
      console.warn('Comments API did not return an array:', data)
      comments.value = []
      return
    }
    
    // 直接使用后端返回的评论树结构，只需要转换日期字段格式
    const convertComments = (commentList) => {
      if (!Array.isArray(commentList)) return []
      return commentList.map(comment => ({
        ...comment,
        createdAt: comment.created_at,
        subComments: comment.subComments ? convertComments(comment.subComments) : []
      }))
    }
    
    comments.value = convertComments(data)
  } catch (error) {
    console.error('Failed to fetch comments:', error)
    comments.value = []
  } finally {
    isLoadingComments.value = false
  }
}

// 获取当前用户对帖子的点赞和收藏状态
const fetchLikeStatus = async () => {
  if (!props.post.id || !props.currentUser) return
  
  try {
    // 获取点赞状态
    const likeRes = await fetch(`/api/posts/${props.post.id}/like/status?userId=${props.currentUser.id}`)
    if (likeRes.ok) {
      const likeData = await likeRes.json()
      isLiked.value = likeData.liked
      likeCount.value = likeData.likeCount
    }
    
    // 获取收藏状态
    const collectRes = await fetch(`/api/posts/${props.post.id}/collect/status?userId=${props.currentUser.id}`)
    if (collectRes.ok) {
      const collectData = await collectRes.json()
      isCollected.value = collectData.collected
      collectCount.value = collectData.collectionCount
    }
  } catch (error) {
    console.error('Failed to fetch like/collect status:', error)
  }
}

const handleLike = async () => {
  if (!props.currentUser) {
    alert('请先登录')
    return
  }
  
  try {
    if (isLiked.value) {
      await fetch(`/api/posts/${props.post.id}/unlike`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: props.currentUser.id })
      })
      likeCount.value = Math.max(0, likeCount.value - 1)
    } else {
      await fetch(`/api/posts/${props.post.id}/like`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: props.currentUser.id })
      })
      likeCount.value++
    }
    isLiked.value = !isLiked.value
    emit('interaction')
  } catch (error) {
    console.error('Failed to toggle like:', error)
  }
}

const handleCollect = async () => {
  if (!props.currentUser) {
    alert('请先登录')
    return
  }
  
  try {
    if (isCollected.value) {
      await fetch(`/api/posts/${props.post.id}/uncollect`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: props.currentUser.id })
      })
      collectCount.value = Math.max(0, collectCount.value - 1)
    } else {
      await fetch(`/api/posts/${props.post.id}/collect`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: props.currentUser.id })
      })
      collectCount.value++
    }
    isCollected.value = !isCollected.value
    emit('interaction')
  } catch (error) {
    console.error('Failed to toggle collect:', error)
  }
}

const handleSubmitComment = async () => {
  if (!props.currentUser) {
    alert('请先登录')
    return
  }
  
  const content = newComment.value.trim()
  if (!content) return
  
  try {
    const response = await fetch(`/api/posts/${props.post.id}/comments`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: props.currentUser.id,
        content: content,
        parent_id: replyToCommentId.value
      })
    })
    
    const newC = await response.json()
    newC.user = props.currentUser
    
    if (replyToCommentId.value) {
      // 如果是回复，需要找到父评论并添加到子评论列表
      // 注意：后端可能将子评论归并到根评论下，所以使用newC.parent_id查找
      const parentComment = comments.value.find(c => c.id === newC.parent_id)
      if (parentComment) {
        if (!parentComment.subComments) {
          parentComment.subComments = []
        }
        parentComment.subComments.unshift({
          id: newC.id,
          post_id: newC.post_id,
          user_id: newC.user_id,
          content: newC.content,
          createdAt: newC.created_at,
          user: newC.user,
          parent_id: newC.parent_id,
          replyToUser: newC.replyToUser
        })
      }
    } else {
      // 如果是新评论，添加到评论列表顶部
      comments.value.unshift({
        id: newC.id,
        post_id: newC.post_id,
        user_id: newC.user_id,
        content: newC.content,
        createdAt: newC.created_at,
        user: newC.user
      })
    }
    
    newComment.value = ''
    // 重置回复目标
    replyToCommentId.value = null
    replyToNickname.value = null
    emit('interaction')
  } catch (error) {
    console.error('Failed to post comment:', error)
  }
}

const handleDeleteComment = async (commentId) => {
  if (!props.currentUser) return
  
  try {
    await fetch('/api/posts/comments/' + commentId, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: props.currentUser.id })
    })
    
    // 处理顶级评论的删除
    let deleted = false
    comments.value = comments.value.filter(c => {
      if (c.id === commentId) {
        deleted = true
        return false
      }
      
      // 处理子评论的删除
      if (c.subComments && c.subComments.length > 0) {
        const originalLength = c.subComments.length
        c.subComments = c.subComments.filter(sc => sc.id !== commentId)
        if (c.subComments.length < originalLength) {
          deleted = true
        }
      }
      
      return true
    })
    
    if (!deleted) {
      // 如果在顶层和直接子评论中都没找到，可能是嵌套更深的子评论
      comments.value.forEach(c => {
        if (c.subComments && c.subComments.length > 0) {
          c.subComments.forEach(sc => {
            if (sc.subComments && sc.subComments.length > 0) {
              const originalLength = sc.subComments.length
              sc.subComments = sc.subComments.filter(ssc => ssc.id !== commentId)
              if (sc.subComments.length < originalLength) {
                deleted = true
              }
            }
          })
        }
      })
    }
  } catch (error) {
    console.error('Failed to delete comment:', error)
  }
}

const focusCommentInput = () => {
  if (commentInputRef.value) {
    commentInputRef.value.focus()
  }
}

const setReplyTarget = (comment) => {
  replyToCommentId.value = comment.id
  replyToNickname.value = comment.user.nickname
  focusCommentInput()
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''

  const date = new Date(dateStr)

  // 检查日期是否有效
  if (isNaN(date.getTime())) return ''

  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'

  return date.toLocaleDateString('zh-CN')
}

const handleCommentLike = async (commentId) => {
  if (!props.currentUser) return

  const currentStatus = commentLikes.value[commentId] || false

  try {
    const endpoint = currentStatus ? 'unlike' : 'like'
    const response = await fetch(`/api/comments/${commentId}/${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: props.currentUser.id })
    })

    const data = await response.json()
    if (data.success !== false) {
      commentLikes.value[commentId] = !currentStatus
      updateCommentLikeCount(commentId, !currentStatus)
    }
  } catch (error) {
    console.error('Failed to like comment:', error)
  }
}

const updateCommentLikeCount = (commentId, isLiked) => {
  const updateInList = (list) => {
    for (const comment of list) {
      if (comment.id === commentId) {
        comment.likeCount = (comment.likeCount || 0) + (isLiked ? 1 : -1)
        return true
      }
      if (comment.subComments && updateInList(comment.subComments)) {
        return true
      }
    }
    return false
  }
  updateInList(comments.value)
}

const getCommentLikeCount = (comment) => {
  return comment.likeCount || 0
}

const isCommentLiked = (commentId) => {
  return commentLikes.value[commentId] || false
}

// Check follow status
const checkFollowStatus = async () => {
  if (!props.currentUser || !props.post.author_id) return
  
  try {
    const response = await fetch(`/api/auth/following-status?followerId=${props.currentUser.id}&followingId=${props.post.author_id}`)
    const data = await response.json()
    if (data.success) {
      isFollowing.value = data.isFollowing
    }
  } catch (error) {
    console.error('Failed to check follow status:', error)
  }
}

// Handle follow/unfollow
const handleFollow = async () => {
  if (!props.currentUser) {
    alert('请先登录')
    return
  }
  
  if (props.currentUser.id === props.post.author_id) {
    alert('不能关注自己')
    return
  }
  
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
        followingId: props.post.author_id
      })
    })
    
    const data = await response.json()
    if (data.success) {
      isFollowing.value = !isFollowing.value
      emit('interaction')
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

onMounted(() => {
  fetchComments()
  fetchLikeStatus()
  checkFollowStatus()
})

watch(() => props.post.id, () => {
  fetchComments()
})
</script>

<template>
  <div class="modal-overlay" @click.self="closeModal">
    <div class="post-detail-modal">
      <!-- Close button -->
      <button class="close-btn" @click="closeModal">
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
        </svg>
      </button>
      
      <!-- Left: Media -->
      <div class="media-section">
        <div class="media-container">
          <!-- Video post -->
          <template v-if="post.type === 'video'">
            <video 
              ref="videoRef"
              :src="getMediaUrl(post.videoUrl || post.url)"
              :poster="getMediaUrl(post.coverUrl)"
              class="media-content"
              controls
              @ended="handleVideoEnded"
              @click="togglePlay"
            />
            <div v-if="!isPlaying" class="play-overlay" @click="togglePlay">
              <svg class="play-icon" viewBox="0 0 24 24" fill="white">
                <path d="M8 5v14l11-7z"/>
              </svg>
            </div>
          </template>
          
          <!-- Image post (support multiple images) -->
          <template v-else-if="post.type === 'image'">
            <div v-if="post.imageUrls && post.imageUrls.length > 1" class="image-grid">
              <img 
                v-for="(imageUrl, index) in post.imageUrls" 
                :key="index"
                :src="getMediaUrl(imageUrl)"
                :alt="`${post.title} - 图片 ${index + 1}`"
                class="media-content"
              />
            </div>
            <img 
              v-else
              :src="getMediaUrl(post.imageUrl) || getMediaUrl(post.coverUrl)"
              :alt="post.title"
              class="media-content"
            />
          </template>
          
          <!-- Article post -->
          <template v-else-if="post.type === 'article'">
            <div class="article-container">
              <div class="article-cover">
                <img 
                v-if="post.coverUrl"
                :src="getMediaUrl(post.coverUrl)"
                :alt="post.title"
                class="article-cover-image"
              />
              </div>
              <div class="article-preview">
                <h2 class="article-title">{{ post.title }}</h2>
                <p class="article-excerpt">{{ post.description || '点击查看全文' }}</p>
              </div>
            </div>
          </template>
        </div>
      </div>
      
      <!-- Right: Details -->
      <div class="details-section">
        <!-- Author info -->
        <div class="author-header">
          <img :src="getAvatar(post.authorAvatar)" :alt="post.authorNickname" class="author-avatar" />
          <span class="author-name">{{ post.authorNickname }}</span>
          <button
            v-if="currentUser && currentUser.id !== post.author_id"
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
          <div v-if="currentUser && currentUser.id === post.author_id" class="post-actions">
            <button
              @click="showPostDeleteMenu = !showPostDeleteMenu"
              class="post-actions-btn"
            >
              ⋮
            </button>
            <div v-if="showPostDeleteMenu" class="post-delete-menu">
              <button
                @click="handleDeletePost"
                class="post-delete-menu-btn"
              >
                删除
              </button>
            </div>
          </div>
        </div>
        
        <!-- Content -->
        <div class="content-area">
          <h2 class="post-title">{{ post.title }}</h2>
          <p class="post-description">{{ post.description || '' }}</p>
          <p class="post-date">{{ formatDate(post.created_at || post.createdAt) }}</p>
        </div>
        
        <!-- Comments section -->
        <div class="comments-section">
          <h3 class="comments-title">共 {{ formattedCommentCount }} 条评论</h3>
          <div v-if="isLoadingComments" class="loading-comments">
            <p>加载中...</p>
          </div>
          <div v-else class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <img :src="getAvatar(comment.user?.avatar)" :alt="comment.user?.nickname" class="comment-avatar" />
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-username">{{ comment.user.nickname }}</span>
                  <span v-if="comment.user_id === post.author_id || comment.user?.id === post.author_id" class="author-badge">作者</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-footer">
                  <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                  <button
                    v-if="currentUser"
                    @click="setReplyTarget(comment)"
                    class="reply-comment-btn"
                  >
                    回复
                  </button>
                  <button
                    class="comment-like-btn"
                    :class="{ liked: isCommentLiked(comment.id) }"
                    @click="handleCommentLike(comment.id)"
                  >
                    <svg viewBox="0 0 24 24" :fill="isCommentLiked(comment.id) ? 'var(--primary-color)' : 'none'" :stroke="isCommentLiked(comment.id) ? 'var(--primary-color)' : 'currentColor'" stroke-width="2">
                      <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                    </svg>
                    <span v-if="getCommentLikeCount(comment) > 0">{{ getCommentLikeCount(comment) }}</span>
                  </button>
                  <div class="comment-actions">
                    <button 
                      v-if="currentUser && comment.user_id === currentUser.id" 
                      @click="showDeleteMenu = showDeleteMenu === comment.id ? null : comment.id" 
                      class="comment-actions-btn"
                    >
                      ⋮
                    </button>
                    <div v-if="showDeleteMenu === comment.id" class="delete-menu">
                      <button 
                        @click="handleDeleteComment(comment.id); showDeleteMenu = null" 
                        class="delete-menu-btn"
                      >
                        删除
                      </button>
                    </div>
                  </div>
                </div>
                
                <!-- Sub comments -->
                <div v-if="comment.subComments && comment.subComments.length > 0" class="sub-comments">
                  <div v-for="subComment in comment.subComments" :key="subComment.id" class="sub-comment-item">
                    <img :src="getAvatar(subComment.user.avatar)" :alt="subComment.user.nickname" class="sub-comment-avatar" />
                    <div class="sub-comment-content">
                      <div class="sub-comment-header">
                        <span class="sub-comment-username">{{ subComment.user.nickname }}</span>
                        <span v-if="subComment.user_id === post.author_id || subComment.user?.id === post.author_id" class="author-badge">作者</span>
                      </div>
                      <div class="sub-comment-main">
                        <span class="sub-comment-text">
                          <span v-if="subComment.replyToUser" class="reply-target">回复 {{ subComment.replyToUser.nickname }} : </span>
                          {{ subComment.content }}
                        </span>
                      </div>
                      <div class="sub-comment-footer">
                        <span class="sub-comment-time">{{ formatDate(subComment.createdAt) }}</span>
                        <button
                          v-if="currentUser"
                          @click="setReplyTarget(subComment)"
                          class="reply-comment-btn"
                        >
                          回复
                        </button>
                        <button
                          class="comment-like-btn"
                          :class="{ liked: isCommentLiked(subComment.id) }"
                          @click="handleCommentLike(subComment.id)"
                        >
                          <svg viewBox="0 0 24 24" :fill="isCommentLiked(subComment.id) ? 'var(--primary-color)' : 'none'" :stroke="isCommentLiked(subComment.id) ? 'var(--primary-color)' : 'currentColor'" stroke-width="2">
                            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                          </svg>
                          <span v-if="getCommentLikeCount(subComment) > 0">{{ getCommentLikeCount(subComment) }}</span>
                        </button>
                        <div class="comment-actions">
                          <button 
                            v-if="currentUser && subComment.user_id === currentUser.id" 
                            @click="showDeleteMenu = showDeleteMenu === subComment.id ? null : subComment.id" 
                            class="comment-actions-btn"
                          >
                            ⋮
                          </button>
                          <div v-if="showDeleteMenu === subComment.id" class="delete-menu">
                            <button 
                              @click="handleDeleteComment(subComment.id); showDeleteMenu = null" 
                              class="delete-menu-btn"
                            >
                              删除
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <p v-if="comments.length === 0" class="no-comments">暂无评论，快来发表你的看法吧~</p>
          </div>
        </div>
        
        <!-- Bottom actions -->
        <div class="actions-bar">
          <div class="comment-input-wrapper">
            <input 
              ref="commentInputRef"
              type="text" 
              :placeholder="replyToNickname ? `回复 ${replyToNickname}` : '说点什么...'" 
              class="comment-input" 
              v-model="newComment"
              @keyup.enter="handleSubmitComment"
            />
          </div>
          <div class="action-buttons">
            <div 
              class="action-item" 
              :class="{ active: isLiked }" 
              @click="handleLike"
            >
              <svg viewBox="0 0 24 24" :fill="isLiked ? 'var(--primary-color)' : 'none'" :stroke="isLiked ? 'var(--primary-color)' : 'currentColor'" stroke-width="2">
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
              </svg>
              <span>{{ formattedLikes }}</span>
            </div>
            <div 
              class="action-item" 
              :class="{ active: isCollected }" 
              @click="handleCollect"
            >
              <svg viewBox="0 0 24 24" :fill="isCollected ? 'var(--primary-color)' : 'none'" :stroke="isCollected ? 'var(--primary-color)' : 'currentColor'" stroke-width="2">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
              <span>{{ formattedCollects }}</span>
            </div>
            <div class="action-item" @click="focusCommentInput">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
              </svg>
              <span>{{ formattedCommentCount }}</span>
            </div>
            <div class="action-item" @click="showAiSummary = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
              </svg>
              <span>AI</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <AiSummary
      v-if="showAiSummary"
      :post-id="post.id"
      :title="post.title"
      :content="post.description"
      @close="showAiSummary = false"
    />
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.post-detail-modal {
  display: flex;
  background: var(--white);
  border-radius: 12px;
  overflow: hidden;
  max-width: 90vw;
  max-height: 90vh;
  width: 1000px;
  height: 680px;
  position: relative;
}

.close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
}

.close-btn svg {
  width: 20px;
  height: 20px;
  color: var(--text-primary);
}

.close-btn:hover {
  background: var(--white);
}

/* Left media section */
.media-section {
  flex: 1;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 0;
}

.media-container {
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
}

.media-content {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
}

.play-icon {
  width: 80px;
  height: 80px;
  opacity: 0.9;
}

/* Right details section */
.details-section {
  width: 400px;
  display: flex;
  flex-direction: column;
  background: var(--white);
}

.author-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  flex: 1;
  margin-left: 12px;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
}

.follow-btn {
  padding: 6px 16px;
  background: var(--primary-color);
  color: var(--white);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.follow-btn:hover {
  background: rgba(var(--primary-color-rgb), 0.9);
}

.follow-btn.following {
  background: rgba(0, 0, 0, 0.03);
  color: var(--text-secondary);
}

.follow-btn.following:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--primary-color);
}

.follow-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.post-actions {
  position: relative;
}

.post-actions-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(0, 0, 0, 0.03);
  font-size: 18px;
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.post-actions-btn:hover {
  background: rgba(0, 0, 0, 0.06);
  color: var(--text-primary);
}

.post-delete-menu {
  position: absolute;
  right: 0;
  top: 100%;
  margin-top: 4px;
  background: var(--white);
  border-radius: 4px;
  box-shadow: var(--shadow-md);
  padding: 4px 0;
  z-index: 10;
  min-width: 80px;
}

.post-delete-menu-btn {
  width: 100%;
  padding: 6px 16px;
  border: none;
  background: none;
  color: var(--primary-color);
  font-size: 13px;
  cursor: pointer;
  text-align: left;
}

.post-delete-menu-btn:hover {
  background: rgba(0, 0, 0, 0.03);
}

.follow-btn-loading {
  display: flex;
  align-items: center;
  gap: 6px;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  animation: spin 1s linear infinite;
}

.spinner-path {
  animation: dash 1.5s ease-in-out infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes dash {
  0% {
    stroke-dasharray: 1, 150;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -35;
  }
  100% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -124;
  }
}

.content-area {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.post-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.post-description {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 12px 0;
  white-space: pre-wrap;
}

.post-date {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0;
}

.comments-section {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
}

.comments-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 16px 0;
}

.no-comments {
  text-align: center;
  color: var(--text-secondary);
  font-size: 13px;
  padding: 40px 0;
}

.loading-comments {
  text-align: center;
  color: var(--text-secondary);
  font-size: 13px;
  padding: 40px 0;
}

.comment-item {
  display: flex;
  margin-bottom: 16px;
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  margin-right: 12px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  margin-bottom: 4px;
}

.comment-username {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

.author-badge {
  display: inline-block;
  font-size: 10px;
  color: var(--primary-color);
  background: rgba(255, 46, 99, 0.1);
  padding: 1px 4px;
  border-radius: 3px;
  margin-left: 6px;
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: var(--text-secondary);
  margin-right: 12px;
}

.comment-text {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  margin: 0 0 6px 0;
  word-break: break-word;
}

.comment-footer {
  display: flex;
  align-items: center;
  margin-top: 4px;
}

.delete-comment-btn {
  font-size: 12px;
  color: var(--text-secondary);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  margin-left: 12px;
}

.delete-comment-btn:hover {
  color: var(--primary-color);
}

.reply-comment-btn {
  font-size: 12px;
  color: var(--text-secondary);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
}

.reply-comment-btn:hover {
  color: var(--primary-color);
}

.comment-like-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-secondary);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  margin-left: 8px;
}

.comment-like-btn svg {
  width: 16px;
  height: 16px;
}

.comment-like-btn:hover {
  color: var(--primary-color);
}

.comment-like-btn.liked {
  color: var(--primary-color);
}

/* Comment actions */
.comment-actions {
  position: relative;
  margin-left: auto;
}

.comment-actions-btn {
  width: 24px;
  height: 24px;
  border: none;
  background: none;
  font-size: 18px;
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.comment-actions-btn:hover {
  background: rgba(0, 0, 0, 0.03);
  color: var(--text-primary);
}

.delete-menu {
  position: absolute;
  right: 0;
  top: 100%;
  margin-top: 4px;
  background: var(--white);
  border-radius: 4px;
  box-shadow: var(--shadow-md);
  padding: 4px 0;
  z-index: 10;
  min-width: 80px;
}

.delete-menu-btn {
  width: 100%;
  padding: 6px 16px;
  border: none;
  background: none;
  color: var(--primary-color);
  font-size: 13px;
  cursor: pointer;
  text-align: left;
}

.delete-menu-btn:hover {
  background: rgba(0, 0, 0, 0.03);
}

/* Sub comments */
.sub-comments {
  margin-left: 0px;
  margin-top: 8px;
}

.sub-comment-item {
  display: flex;
  margin-bottom: 12px;
}

.sub-comment-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  margin-right: 8px;
  margin-top: 2px;
}

.sub-comment-content {
  flex: 1;
}

.sub-comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.sub-comment-main {
  margin-bottom: 2px;
}

.sub-comment-username {
  font-size: 12px;
  font-weight: 500;
  color: var(--text-primary);
  margin-right: 6px;
}

.sub-comment-text {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
  word-break: break-word;
}

.sub-comment-footer {
  display: flex;
  align-items: center;
  margin-top: 2px;
}

.sub-comment-time {
  font-size: 11px;
  color: var(--text-secondary);
  margin-right: 12px;
}

.action-item.active {
  color: var(--primary-color);
}

.action-item.active svg {
  color: var(--primary-color);
}

.action-item:hover {
  color: var(--primary-color);
}

.actions-bar {
  padding: 12px 20px;
  border-top: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: 16px;
}

.comment-input-wrapper {
  flex: 1;
}

.comment-input {
  width: 100%;
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  background: var(--white);
  color: var(--text-primary);
}

.comment-input:focus {
  border-color: var(--primary-color);
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--text-secondary);
  cursor: pointer;
}

.action-item svg {
  width: 20px;
  height: 20px;
}

.action-item span {
  font-size: 13px;
}

.action-item:hover {
  color: #ff2442;
}
.reply-target {
  color: var(--text-secondary);
  font-size: 14px;
  margin-right: 4px;
}
</style>
