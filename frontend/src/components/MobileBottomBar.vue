<script setup>
defineProps({
  activeItem: {
    type: String,
    required: true
  },
  currentUser: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['change', 'show-login'])

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http') || url.includes('default_avatar')) return url
  return `/api${url}`
}

const onTabChange = (name) => {
  if ((name === 'publish' || name === 'profile') && !currentUser) {
    emit('show-login')
    return
  }
  emit('change', name)
}
</script>

<template>
  <nav class="mobile-tabbar">
    <button
      class="tab-item"
      :class="{ active: activeItem === 'discovery' }"
      @click="onTabChange('discovery')"
    >
      <svg class="tab-icon" viewBox="0 0 24 24" fill="currentColor">
        <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z" />
      </svg>
      <span class="tab-label">首页</span>
    </button>

    <button
      class="tab-item"
      :class="{ active: activeItem === 'notification' }"
      @click="onTabChange('notification')"
    >
      <svg class="tab-icon" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2zm-2 1H8v-6c0-2.48 1.51-4.5 4-4.5s4 2.02 4 4.5v6z" />
      </svg>
      <span class="tab-label">消息</span>
    </button>

    <button class="tab-item publish-btn" @click="onTabChange('publish')">
      <svg class="publish-icon" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z" />
      </svg>
    </button>

    <button
      class="tab-item"
      :class="{ active: activeItem === 'profile' }"
      @click="onTabChange('profile')"
    >
      <img
        v-if="currentUser"
        :src="getImageUrl(currentUser.avatar)"
        :alt="currentUser.nickname"
        class="tab-avatar"
      />
      <svg v-else class="tab-icon" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
      </svg>
      <span class="tab-label">我</span>
    </button>
  </nav>
</template>

<style scoped>
.mobile-tabbar {
  display: none;
}

@media (max-width: 768px) {
  .mobile-tabbar {
    display: flex;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: var(--mobile-tabbar-height);
    padding-bottom: var(--safe-area-bottom);
    background: var(--white);
    border-top: 1px solid var(--border-color);
    z-index: 200;
    align-items: center;
    justify-content: space-around;
  }

  .tab-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    flex: 1;
    height: 100%;
    color: var(--text-secondary);
    font-size: 10px;
    gap: 2px;
    position: relative;
    -webkit-tap-highlight-color: transparent;
  }

  .tab-item.active {
    color: var(--primary-color);
  }

  .tab-icon {
    width: 24px;
    height: 24px;
  }

  .tab-avatar {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    object-fit: cover;
  }

  .tab-item.active .tab-avatar {
    border: 2px solid var(--primary-color);
  }

  .tab-label {
    font-size: 10px;
    line-height: 1;
  }

  .publish-btn {
    color: var(--white);
  }

  .publish-icon {
    width: 44px;
    height: 44px;
    color: var(--primary-color);
    filter: drop-shadow(0 2px 8px rgba(255, 36, 66, 0.3));
    transform: translateY(-4px);
  }
}
</style>
