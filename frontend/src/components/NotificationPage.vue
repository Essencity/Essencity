<script setup>
import { ref, onMounted, watch } from 'vue'

// Helper function to fix URL
const fixUrl = (url) => {
  if (!url) return null
  // If URL already starts with /api, strip it to avoid duplication
  if (url.startsWith('/api')) {
    return url.replace('/api', '')
  }
  // For relative paths, return as is - getMediaUrl will add the correct base
  if (url.startsWith('/uploads')) {
    return url
  }
  return url
}

// Helper function to get full image URL
const getImageUrl = (url) => {
  if (!url) return '/default-avatar.png'
  // If URL contains default_avatar, return as is
  if (url.includes('default_avatar')) return url
  // If URL is already a full URL with correct context path, return as is
  if (url.startsWith('http')) return url
  // Fix URL first to avoid duplication
  const fixedUrl = fixUrl(url)
  // Otherwise, prepend backend base URL
  return `/api${fixedUrl}`
}

// Helper function to get full image/video URL
const getMediaUrl = (url) => {
  if (!url) return null
  // Fix URL first to avoid duplication
  const fixedUrl = fixUrl(url)
  // If URL is already a full URL, return as is
  if (fixedUrl.startsWith('http')) return fixedUrl
  // Otherwise, prepend backend base URL with context path
  return `/api${fixedUrl}`
}

const props = defineProps({
  currentUser: {
    type: Object,
    default: null
  }
})

const activeTab = ref('comments')
const notifications = ref([])
const loading = ref(false)

const tabs = [
  { id: 'comments', label: '评论和回复' },
  { id: 'likes', label: '赞和收藏' },
  { id: 'follows', label: '新增关注' }
]

const fetchNotifications = async () => {
  if (!props.currentUser) return
  
  loading.value = true
  try {
    const res = await fetch(`/api/notifications?userId=${props.currentUser.id}&type=${activeTab.value}`)
    if (res.ok) {
      const data = await res.json()
      notifications.value = data
      // Debug: Log notification data to check post structure
      console.log('Notification data:', data)
    }
  } catch (error) {
    console.error('Failed to fetch notifications:', error)
  } finally {
    loading.value = false
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  // Within 24 hours
  if (diff < 24 * 60 * 60 * 1000) {
    if (diff < 60 * 60 * 1000) {
      const mins = Math.max(1, Math.floor(diff / (60 * 1000)))
      return `${mins}分钟前`
    }
    const hours = Math.floor(diff / (60 * 60 * 1000))
    return `${hours}小时前`
  }
  
  // Within 1 year
  if (diff < 365 * 24 * 60 * 60 * 1000) {
    return `${date.getMonth() + 1}-${date.getDate()}`
  }
  
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

watch(activeTab, () => {
  fetchNotifications()
})

onMounted(() => {
  if (props.currentUser) {
    fetchNotifications()
  }
})
</script>

<template>
  <div class="notification-container">
    <div class="tabs-header">
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
    
    <div class="notification-content">
      <div v-if="loading" class="loading-state">加载中...</div>
      <div v-else-if="notifications.length === 0" class="empty-state">
        暂无消息
      </div>
      <div v-else class="notification-list">
        <div v-for="item in notifications" :key="item.id" class="notification-item">
          <!-- Avatar -->
          <img :src="getImageUrl(item.actor?.avatar)" class="actor-avatar" alt="avatar" />
          
          <div class="item-content">
            <div class="item-header">
              <span class="actor-name">{{ item.actor?.nickname || item.actor?.username }}</span>
              <span class="action-desc">
                <template v-if="item.type === 'LIKE'">赞了你的笔记</template>
                <template v-else-if="item.type === 'COLLECTION'">收藏了你的笔记</template>
                <template v-else-if="item.type === 'COMMENT'">评论了你的笔记</template>
                <template v-else-if="item.type === 'REPLY'">回复了你的评论</template>
                <template v-else-if="item.type === 'FOLLOW'">开始关注你了</template>
              </span>
              <span class="time">{{ formatTime(item.createdAt) }}</span>
            </div>
            
            <div v-if="item.content" class="comment-text">
              {{ item.content }}
            </div>
          </div>
          
          
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notification-container {
  padding: 0 40px;
  height: 100%;
  width: 100%;
  max-width: none;
  margin: 0 auto;
}

.tabs-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  padding: 20px 0;
  margin-bottom: 16px;
  position: sticky;
  top: 0;
  background: var(--bg-color);
  z-index: 10;
}

.tab-btn {
  background: none;
  border: none;
  font-size: 16px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 10px 28px;
  border-radius: 20px;
  transition: all 0.2s;
}

.tab-btn.active {
  background-color: rgba(0, 0, 0, 0.05);
  color: var(--text-primary);
  font-weight: 600;
}

.tab-btn:hover:not(.active) {
  color: var(--text-primary);
}

.notification-content {
  background-color: var(--white);
  min-height: 500px;
  border-radius: 12px;
  padding: 16px 40px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.loading-state, .empty-state {
  padding: 40px;
  text-align: center;
  color: var(--text-secondary);
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 24px 16px;
  border-bottom: 1px solid var(--border-color);
}

.actor-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 16px;
  object-fit: cover;
  flex-shrink: 0;
}

.item-content {
  flex: 1;
  margin-right: 20px;
  min-width: 0;
}

.item-header {
  font-size: 15px;
  margin-bottom: 8px;
  line-height: 1.6;
}

.actor-name {
  font-weight: 600;
  margin-right: 8px;
  color: var(--text-primary);
}

.action-desc {
  color: var(--text-secondary);
  margin-right: 10px;
}

.time {
  font-size: 13px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  color: var(--text-primary);
  margin-top: 6px;
  line-height: 1.5;
}




</style>
