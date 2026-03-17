<script setup>
import { ref } from 'vue'

const props = defineProps({
  activeItem: {
    type: String,
    required: true
  },
  currentUser: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['change', 'show-login', 'logout'])

// Helper function to get full image URL
const getImageUrl = (url) => {
  if (!url) return ''
  // If URL is already a full URL or contains default_avatar, return as is
  if (url.startsWith('http') || url.includes('default_avatar')) return url
  // Otherwise, prepend backend base URL
  return `/api${url}`
}

const showMoreMenu = ref(false)

const menuItems = [
  { id: 'discovery', label: '发现', icon: 'M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z' },
  { id: 'publish', label: '发布', icon: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z' },
  { id: 'notification', label: '通知', icon: 'M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2zm-2 1H8v-6c0-2.48 1.51-4.5 4-4.5s4 2.02 4 4.5v6z' }
]

const toggleMoreMenu = () => {
  showMoreMenu.value = !showMoreMenu.value
}

const handleLogout = () => {
  showMoreMenu.value = false
  emit('logout')
}

// Dark Mode Logic
const isDarkMode = ref(localStorage.getItem('theme') === 'dark')

const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark')
    document.documentElement.setAttribute('data-theme', 'dark')
    localStorage.setItem('theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    document.documentElement.removeAttribute('data-theme')
    localStorage.setItem('theme', 'light')
  }
}

import { onMounted } from 'vue'
onMounted(() => {
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark')
    document.documentElement.setAttribute('data-theme', 'dark')
  }
})
</script>

<template>
  <aside class="sidebar">
    <div class="logo-area">
      <h1 class="logo-text">趣生活</h1>
    </div>
    
    <nav class="nav-menu">
      <a 
        v-for="item in menuItems" 
        :key="item.id"
        href="#" 
        class="nav-item"
        :class="{ active: activeItem === item.id }"
        @click.prevent="emit('change', item.id)"
      >
        <div class="icon-wrapper">
          <svg class="icon" viewBox="0 0 24 24" fill="currentColor">
            <path :d="item.icon" />
          </svg>
        </div>
        <span class="label">{{ item.label }}</span>
      </a>
      
      <!-- User profile as nav item when logged in (below 通知) -->
      <a 
        v-if="currentUser" 
        href="#" 
        class="nav-item user-nav-item"
        :class="{ active: activeItem === 'profile' }"
        @click.prevent="emit('change', 'profile')"
      >
        <div class="icon-wrapper">
          <img :src="getImageUrl(currentUser.avatar)" :alt="currentUser.nickname" class="nav-avatar" />
        </div>
        <span class="label">我</span>
      </a>
    </nav>

    <!-- Show Login Promo when NOT logged in -->
    <div v-if="!currentUser" class="login-promo">
      <button class="login-btn-large" @click="emit('show-login')">登录</button>
      <div class="promo-card">
        <p class="promo-title">马上登录即可</p>
        <div class="promo-list">
          <div class="promo-item">
            <svg class="promo-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-1.91z"/>
            </svg>
            <span>刷到更懂你的优质内容</span>
          </div>
          <div class="promo-item">
            <svg class="promo-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C7.03 2 3 6.03 3 11s4.03 9 9 9 9-4.03 9-9-4.03-9-9-9zM7 11c0-2.76 2.24-5 5-5s5 2.24 5 5-2.24 5-5 5-5-2.24-5-5zm5-3c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
             </svg>
            <span>搜索最新种草、拔草信息</span>
          </div>
          <div class="promo-item">
            <svg class="promo-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
            </svg>
            <span>查看收藏、点赞的笔记</span>
          </div>
          <div class="promo-item">
            <svg class="promo-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"/>
            </svg>
            <span>与他人更好互动、交流</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="more-menu">
      <button class="more-btn" @click="toggleMoreMenu">
        <svg class="icon" viewBox="0 0 24 24" fill="currentColor">
          <path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
        </svg>
        <span>更多</span>
      </button>
      
      <!-- Dropdown menu -->
      <div v-if="showMoreMenu" class="more-dropdown">
        <div class="dropdown-item dark-mode-item" @click.stop="toggleDarkMode">
          <div class="dark-mode-label">
            <svg v-if="isDarkMode" class="menu-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 3c-4.97 0-9 4.03-9 9s4.03 9 9 9 9-4.03 9-9c0-.46-.04-.92-.1-1.36-.98 1.37-2.58 2.26-4.4 2.26-3.03 0-5.5-2.47-5.5-5.5 0-1.82.89-3.42 2.26-4.4-.44-.06-.9-.1-1.36-.1z"/>
            </svg>
            <svg v-else class="menu-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M6.76 4.84l-1.8-1.79-1.41 1.41 1.79 1.79 1.42-1.41zM4 10.5H1v2h3v-2zm9-9.95h-2V3.5h2V.55zm7.45 3.91l-1.41-1.41-1.79 1.79 1.41 1.41 1.79-1.79zm-3.21 13.7l1.79 1.8 1.41-1.41-1.8-1.79-1.4 1.4zM20 10.5v2h3v-2h-3zm-8-5c-3.31 0-6 2.69-6 6s2.69 6 6 6 6-2.69 6-6-2.69-6-6-6zm-1 16.95h2V19.5h-2v2.95zm-7.45-3.91l1.41 1.41 1.79-1.8-1.41-1.41-1.79 1.8z"/>
            </svg>
            <span>深色模式</span>
          </div>
          <div class="switch" :class="{ 'on': isDarkMode }">
            <div class="switch-handle"></div>
          </div>
        </div>
        <a v-if="currentUser" href="#" class="dropdown-item" @click.prevent="handleLogout">退出登录</a>
      </div>
    </div>
    
    <!-- Click outside to close -->
    <div v-if="showMoreMenu" class="overlay" @click="showMoreMenu = false"></div>
  </aside>
</template>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--sidebar-width);
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  background: var(--white);
  z-index: 100;
}

.logo-area {
  height: var(--header-height);
  display: flex;
  align-items: center;
  padding-left: 12px;
  margin-bottom: 8px;
}

.logo-text {
  background-color: var(--primary-color);
  color: var(--white);
  font-size: 18px;
  font-weight: 900;
  padding: 6px 16px;
  border-radius: var(--radius-full);
  letter-spacing: 1px;
}

.nav-item {
  display: flex;
  align-items: center;
  height: 48px;
  padding: 0 16px;
  margin-bottom: 4px;
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 16px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

.nav-item.active {
  background-color: #ff24420d; /* very light red */
  color: var(--primary-color);
  font-weight: 600;
}

.icon-wrapper {
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.more-menu {
  margin-top: auto;
  padding-bottom: 24px;
}

.more-btn {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 12px 16px;
  color: var(--text-secondary);
  font-size: 16px;
  border-radius: var(--radius-full);
}

.more-btn:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

.more-btn span {
  margin-left: 12px;
}

.login-promo {
  margin-top: auto;
  padding: 0 8px 16px;
}

.login-btn-large {
  width: 100%;
  background-color: var(--primary-color);
  color: white;
  height: 48px;
  border-radius: 24px;
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(255, 36, 66, 0.2);
}

.promo-card {
  background-color: var(--white);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.promo-title {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 12px;
  text-align: center;
}

.promo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.promo-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 13px;
}

.promo-icon {
  width: 16px;
  height: 16px;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.more-menu {
  padding: 0 8px 24px;
}

.more-btn {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 12px 16px;
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 500;
  border-radius: var(--radius-full);
}

.more-btn:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

.more-btn span {
  margin-left: 12px;
}

/* User Profile Area (when logged in) */
.user-profile-area {
  margin-top: auto;
  padding: 0 8px 16px;
}

.user-info-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #fff5f6 0%, #fff 100%);
  border-radius: var(--radius-lg);
  border: 1px solid #ffe5e8;
  margin-bottom: 12px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ff2442;
}

.user-details {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-nickname {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-id {
  font-size: 12px;
  color: #888;
}

.logout-btn {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 24px;
  font-size: 14px;
  color: #666;
  background: white;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: #f5f5f5;
  border-color: #ccc;
}

/* Nav avatar for "我" item */
.nav-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.user-nav-item.active .nav-avatar {
  border: 2px solid var(--primary-color);
}

/* More menu dropdown */
.more-menu {
  position: relative;
}

.more-dropdown {
  position: absolute;
  bottom: 100%;
  left: 8px;
  right: 8px;
  background: var(--white);
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 8px 0;
  margin-bottom: 8px;
  z-index: 200;
}

.dropdown-item {
  display: block;
  padding: 12px 16px;
  color: var(--text-primary);
  font-size: 14px;
  transition: background 0.2s;
}

.dropdown-item:hover {
  background: var(--bg-color);
}

/* Overlay to close dropdown when clicking outside */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 150;
}

.dark-mode-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.dark-mode-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-icon {
  width: 18px;
  height: 18px;
  color: var(--text-secondary);
}

.switch {
  width: 36px;
  height: 20px;
  background-color: #e0e0e0;
  border-radius: 10px;
  position: relative;
  transition: background-color 0.3s;
}

.switch.on {
  background-color: var(--primary-color);
}

.switch-handle {
  width: 16px;
  height: 16px;
  background-color: white;
  border-radius: 50%;
  position: absolute;
  top: 2px;
  left: 2px;
  transition: transform 0.3s;
  box-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

.switch.on .switch-handle {
  transform: translateX(16px);
}

</style>


