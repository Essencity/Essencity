<script setup>
import { computed, ref } from 'vue'
import VoiceSearch from './VoiceSearch.vue'

const props = defineProps({
  currentUser: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['search'])
const searchQuery = ref('')
const showVoiceSearch = ref(false)

const handleSearch = () => {
  emit('search', searchQuery.value)
}

const handleVoiceSearch = (query) => {
  searchQuery.value = query
  emit('search', query)
}

const placeholderText = computed(() => {
  return props.currentUser ? '搜索趣生活' : '登录探索更多内容'
})
</script>

<template>
  <header class="header">
    <div class="search-container">
      <div class="search-input-wrapper">
        <input
          type="text"
          v-model="searchQuery"
          @keyup.enter="handleSearch"
          :placeholder="placeholderText"
          class="search-input"
        />
        <button class="voice-btn" @click="showVoiceSearch = true">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 14c1.66 0 2.99-1.34 2.99-3L15 5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3zm5.3-3c0 3-2.54 5.1-5.3 5.1S6.7 14 6.7 11H5c0 3.41 2.72 6.23 6 6.72V21h2v-3.28c3.28-.48 6-3.3 6-6.72h-1.7z"/>
          </svg>
        </button>
        <button class="search-btn" @click="handleSearch">
          <svg class="icon" viewBox="0 0 24 24" fill="#666">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
          </svg>
        </button>
      </div>
    </div>

    <div class="right-actions">
      <a href="#" class="action-link">创作中心</a>
      <a href="#" class="action-link">业务合作</a>
    </div>

    <VoiceSearch
      :visible="showVoiceSearch"
      @close="showVoiceSearch = false"
      @search="handleVoiceSearch"
    />
  </header>
</template>

<style scoped>
.header {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: center; /* Changed from space-between to center */
  padding: 0 40px;
  background-color: var(--white);
  position: sticky;
  top: 0;
  z-index: 90;
}

.search-container {
  /* flex: 1; Removed flexible width */
  width: 100%;
  max-width: 600px;
  margin: 0; /* Auto margin handled by flex justify-center */
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background-color: var(--bg-color);
  border-radius: var(--radius-full);
  height: 48px;
  padding: 0 16px;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.search-input-wrapper:focus-within {
  background-color: var(--white);
  border-color: var(--border-color);
  box-shadow: var(--shadow-sm);
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 16px;
  margin-right: 8px;
  color: var(--text-primary);
  text-align: center; /* Optional: Center text inside input if desired, but user asked for search BAR to move to middle */
}
/* Reverting text-align center as it's usually weird for search inputs unless specified. */
.search-input {
  text-align: left;
}


.search-btn {
  padding: 4px;
}

.voice-btn {
  padding: 4px;
  margin-right: 4px;
  color: var(--text-secondary);
  transition: color 0.2s;
}

.voice-btn:hover {
  color: var(--primary-color);
}

.voice-btn svg {
  width: 22px;
  height: 22px;
  display: block;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 24px;
  position: absolute; /* Take out of flow to keep search strictly centered */
  right: 40px;
  height: 100%;
}

.action-link {
  color: var(--text-secondary);
  font-size: 16px;
  font-weight: 500;
}

.action-link:hover {
  color: var(--text-primary);
}

.login-btn {
  background-color: var(--primary-color);
  color: white;
  padding: 10px 24px;
  border-radius: var(--radius-full);
  font-weight: 600;
  font-size: 15px;
  transition: opacity 0.2s;
}

.login-btn:hover {
  opacity: 0.9;
}
</style>
