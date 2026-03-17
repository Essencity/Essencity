<script setup>
import { computed } from 'vue'

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['open-detail'])

// Default avatar base64
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iI2NjYyI+PHBhdGggZD0iTTEyIDJDNC40OCAyIDIgNi40OCAyIDEyczQuNDggMTAgMTAgMTAgMTAtNC40OCAxMC0xMFMxNy41MiAyIDEyIDJ6bTAgM2MxLjY2IDAgMyAxLjM0IDMgM3MtMS4zNCAzLTMgMy0zLTEuMzQtMy0zIDEuMzQtMyAzLTN6bTAgMTQuMmMtMi41IDAtNC43MS0xLjI4LTYtMy4yMi4wMy0xLjk5IDQtMy4wOCA2LTMuMDggMS45OSAwIDUuOTcgMS4wOSA2IDMuMDgtMS4yOSAxLjk0LTMuNSAzLjIyLTYgMy4yMnoiLz48L3N2Zz4='

const getAvatar = (url) => {
  if (!url) return defaultAvatar
  // If URL is already a full URL or contains default_avatar, return as is
  if (url.startsWith('http') || url.includes('default_avatar')) return url
  // If URL already starts with /api, return as is to avoid duplication
  if (url.startsWith('/api')) return url
  // Otherwise, prepend backend base URL
  return `/api${url}`
}

// Helper function to get media URL
const getMediaUrl = (url) => {
  if (!url) return ''
  // If URL is already a full URL, return as is
  if (url.startsWith('http')) return url
  // If URL already starts with /api, return as is to avoid duplication
  if (url.startsWith('/api')) return url
  // Otherwise, prepend backend base URL
  return `/api${url}`
}

// Format numbers like 3.6w
const formattedLikes = computed(() => {
  const num = props.post.likes
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num
})

const handleCardClick = () => {
  emit('open-detail', props.post)
}
</script>

<template>
  <div class="post-card" @click="handleCardClick">
    <div class="media-container">
      <img :src="getMediaUrl(post.image)" :alt="post.title" class="cover-image" loading="lazy" />
      
      <!-- Video play icon in top-right corner -->
      <div v-if="post.type === 'video'" class="video-badge">
        <svg class="play-icon" viewBox="0 0 24 24" fill="white">
          <path d="M8 5v14l11-7z"/>
        </svg>
      </div>
    </div>
    
    <div class="card-content">
      <h3 class="post-title">{{ post.title }}</h3>
      
      <div class="card-footer">
        <div class="user-info">
          <img :src="getAvatar(post.avatar)" :alt="post.user" class="avatar" />
          <span class="username">{{ post.user }}</span>
        </div>
        
        <div class="like-info">
          <svg class="heart-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
          <span class="like-count">{{ formattedLikes }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-card {
  break-inside: avoid;
  margin-bottom: 20px;
  cursor: pointer;
}

.post-card:hover .media-container {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.media-container {
  position: relative;
  width: 210px;
  height: 280px;
  background: #f5f5f5;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.cover-image {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  object-position: center;
}

.video-player {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  object-position: center;
  background: #000;
}

/* Small play badge in top-right corner like Xiaohongshu */
.video-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.45);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-icon {
  width: 14px;
  height: 14px;
}

/* Text content outside the card */
.card-content {
  padding: 10px 4px 0;
  background: transparent;
}

.post-title {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
  margin: 0 0 8px 0;
  color: var(--text-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--text-secondary);
}

.user-info {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
}

.avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.username {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-secondary);
  font-size: 12px;
}

.like-info {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: 10px;
}

.heart-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
  color: var(--text-secondary);
}

.like-count {
  color: var(--text-secondary);
  font-size: 12px;
}
</style>
