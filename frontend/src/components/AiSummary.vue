<script setup>
import { ref } from 'vue'
import { getAiSummary } from '@/api/ai.js'

const props = defineProps({
  postId: {
    type: [Number, String],
    required: true
  },
  title: {
    type: String,
    default: ''
  },
  content: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close'])

const summary = ref('')
const loading = ref(false)
const error = ref(null)
const hasSummary = ref(false)

const fetchSummary = async () => {
  loading.value = true
  error.value = null
  
  try {
    const data = await getAiSummary(props.postId)
    if (data.ai_summary) {
      summary.value = data.ai_summary
      hasSummary.value = true
    } else {
      error.value = '暂无AI总结'
    }
  } catch (err) {
    error.value = '获取总结失败，请稍后再试'
    console.error('AI Summary error:', err)
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  emit('close')
}
</script>

<template>
  <div class="ai-summary-overlay" @click.self="handleClose">
    <div class="ai-summary-modal">
      <div class="modal-header">
        <h3>AI 总结</h3>
        <button class="close-btn" @click="handleClose">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      
      <div class="modal-body">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>正在生成总结...</p>
        </div>
        
        <div v-else-if="error" class="error-state">
          <p>{{ error }}</p>
          <button class="retry-btn" @click="fetchSummary">重试</button>
        </div>
        
        <div v-else-if="hasSummary" class="summary-content">
          <p>{{ summary }}</p>
        </div>
        
        <div v-else class="empty-state">
          <p>点击按钮获取AI总结</p>
          <button class="fetch-btn" @click="fetchSummary">
            获取总结
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-summary-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
}

.ai-summary-modal {
  background: var(--white);
  border-radius: 16px;
  width: 400px;
  max-width: 90vw;
  max-height: 80vh;
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

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 20px 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  color: var(--text-secondary);
  font-size: 14px;
}

.error-state {
  text-align: center;
  padding: 20px 0;
}

.error-state p {
  color: #ff4d4f;
  font-size: 14px;
  margin-bottom: 12px;
}

.retry-btn,
.fetch-btn {
  background: var(--primary-color);
  color: white;
  padding: 10px 24px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: opacity 0.2s;
}

.retry-btn:hover,
.fetch-btn:hover {
  opacity: 0.9;
}

.summary-content {
  padding: 16px 0;
}

.summary-content p {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
}

.empty-state {
  text-align: center;
  padding: 20px 0;
}

.empty-state p {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
}
</style>
