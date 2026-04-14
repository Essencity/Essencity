<script setup>
import { ref, watch, onMounted } from 'vue'
import { useSpeech } from '@/composables/useSpeech.js'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'search'])

const searchQuery = ref('')
const { isListening, isSupported, transcript, error, checkSupport, startListening, stopListening, resetTranscript } = useSpeech()

watch(() => props.visible, (newVal) => {
  if (newVal) {
    checkSupport()
  }
  if (!newVal) {
    resetTranscript()
    searchQuery.value = ''
  }
}, { immediate: true })

watch(transcript, (newTranscript) => {
  if (newTranscript) {
    searchQuery.value = newTranscript
  }
})

const handleStartListening = () => {
  if (isListening.value) {
    stopListening()
  } else {
    startListening()
  }
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    emit('search', searchQuery.value.trim())
    emit('close')
  }
}

const handleClose = () => {
  stopListening()
  emit('close')
}

const handleKeyup = (e) => {
  if (e.key === 'Enter') {
    handleSearch()
  }
}

const clearSearch = () => {
  searchQuery.value = ''
  resetTranscript()
}
</script>

<template>
  <div v-if="visible" class="voice-search-overlay" @click.self="handleClose">
    <div class="voice-search-modal">
      <div class="modal-header">
        <h3>语音搜索</h3>
        <button class="close-btn" @click="handleClose">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>

      <div class="modal-body">
        <div v-if="!isSupported" class="not-supported">
          <svg viewBox="0 0 24 24" fill="currentColor" class="warning-icon">
            <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
          </svg>
          <p>当前浏览器不支持语音识别</p>
          <p class="suggestion">请使用 Chrome、Safari 或 Edge 浏览器</p>
        </div>

        <template v-else>
          <div class="search-input-wrapper">
            <input
              type="text"
              v-model="searchQuery"
              placeholder="说点什么..."
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <button v-if="searchQuery" class="clear-btn" @click="clearSearch">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
              </svg>
            </button>
          </div>

          <div class="voice-btn-wrapper">
            <button
              class="voice-btn"
              :class="{ listening: isListening }"
              @click="handleStartListening"
            >
              <svg viewBox="0 0 24 24" fill="currentColor" class="mic-icon">
                <path d="M12 14c1.66 0 2.99-1.34 2.99-3L15 5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3zm5.3-3c0 3-2.54 5.1-5.3 5.1S6.7 14 6.7 11H5c0 3.41 2.72 6.23 6 6.72V21h2v-3.28c3.28-.48 6-3.3 6-6.72h-1.7z"/>
              </svg>
              <span v-if="isListening" class="listening-text">正在听...</span>
              <span v-else class="idle-text">按住说话</span>
            </button>
          </div>

          <div v-if="isListening" class="listening-indicator">
            <div class="wave"></div>
            <div class="wave"></div>
            <div class="wave"></div>
          </div>

          <div v-if="error" class="error-message">
            {{ error }}
          </div>

          <button
            v-if="searchQuery"
            class="search-btn"
            @click="handleSearch"
          >
            搜索
          </button>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.voice-search-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1200;
}

.voice-search-modal {
  background: var(--white);
  border-radius: 16px;
  width: 360px;
  max-width: 90vw;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.close-btn:hover {
  background: var(--bg-color);
  color: var(--text-primary);
}

.close-btn svg {
  width: 20px;
  height: 20px;
}

.modal-body {
  padding: 24px 20px;
}

.not-supported {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  width: 48px;
  height: 48px;
  color: #faad14;
  margin-bottom: 16px;
}

.not-supported p {
  font-size: 14px;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.suggestion {
  font-size: 12px;
  color: var(--text-secondary);
}

.search-input-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  padding: 12px 40px 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 24px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.search-input:focus {
  border-color: var(--primary-color);
}

.clear-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
}

.clear-btn svg {
  width: 16px;
  height: 16px;
}

.voice-btn-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.voice-btn {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--primary-color);
  border: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 16px rgba(var(--primary-color-rgb), 0.4);
}

.voice-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(var(--primary-color-rgb), 0.5);
}

.voice-btn.listening {
  background: #ff4d4f;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 77, 79, 0.7);
  }
  70% {
    box-shadow: 0 0 0 20px rgba(255, 77, 79, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(255, 77, 79, 0);
  }
}

.mic-icon {
  width: 32px;
  height: 32px;
  color: white;
}

.listening-text,
.idle-text {
  font-size: 12px;
  color: white;
  margin-top: 4px;
}

.listening-indicator {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-bottom: 20px;
  height: 30px;
  align-items: flex-end;
}

.wave {
  width: 4px;
  height: 20px;
  background: var(--primary-color);
  border-radius: 2px;
  animation: wave 1s ease-in-out infinite;
}

.wave:nth-child(1) {
  animation-delay: 0s;
}

.wave:nth-child(2) {
  animation-delay: 0.1s;
}

.wave:nth-child(3) {
  animation-delay: 0.2s;
}

@keyframes wave {
  0%, 100% {
    height: 10px;
  }
  50% {
    height: 25px;
  }
}

.error-message {
  text-align: center;
  padding: 12px;
  background: #fff2f0;
  border-radius: 8px;
  color: #ff4d4f;
  font-size: 13px;
  margin-bottom: 16px;
}

.search-btn {
  width: 100%;
  padding: 12px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.search-btn:hover {
  opacity: 0.9;
}
</style>
