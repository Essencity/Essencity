<script setup>
import { ref, computed, watch } from 'vue'
import { aiAssist } from '../api/ai.js'
import AiLoading from './AiLoading.vue'

const props = defineProps({
  mode: { type: String, required: true }, // expand | polish | title
  title: { type: String, default: '' },
  content: { type: String, default: '' },
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['apply', 'close'])

const status = ref('idle') // idle | loading | success | error
const result = ref('')
const titleOptions = ref([])
const errorMsg = ref('')

const modeLabel = computed(() => {
  const labels = { expand: 'AI扩写', polish: 'AI润色', title: 'AI生成标题' }
  return labels[props.mode] || 'AI助手'
})

const handleGenerate = async () => {
  status.value = 'loading'
  errorMsg.value = ''
  result.value = ''
  titleOptions.value = []

  try {
    const response = await aiAssist(props.mode, props.title, props.content)
    const data = response.result || ''

    if (props.mode === 'title') {
      // 解析标题选项JSON数组
      try {
        let jsonStr = data.trim()
        if (!jsonStr.startsWith('[')) {
          const start = jsonStr.indexOf('[')
          const end = jsonStr.lastIndexOf(']')
          if (start >= 0 && end > start) {
            jsonStr = jsonStr.substring(start, end + 1)
          }
        }
        const parsed = JSON.parse(jsonStr)
        titleOptions.value = Array.isArray(parsed) ? parsed : [data]
      } catch {
        titleOptions.value = [data]
      }
    } else {
      result.value = data
    }
    status.value = 'success'
  } catch (e) {
    status.value = 'error'
    errorMsg.value = e.message || '生成失败'
  }
}

const handleApply = (text) => {
  emit('apply', text || result.value)
}

const handleRetry = () => {
  handleGenerate()
}

watch(() => props.visible, (newVal) => {
  if (newVal && status.value === 'idle') {
    handleGenerate()
  }
})
</script>

<template>
  <div class="ai-assist-panel" v-if="visible">
    <div class="panel-header">
      <div class="header-left">
        <span class="ai-badge">AI</span>
        <span class="mode-label">{{ modeLabel }}</span>
      </div>
      <div class="header-right">
        <button class="retry-btn" @click="handleRetry" :disabled="status === 'loading'">
          重新生成
        </button>
        <button class="close-btn" @click="emit('close')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>
    </div>

    <div class="panel-content">
      <AiLoading v-if="status === 'loading'" size="medium" />

      <template v-else-if="status === 'success'">
        <!-- 标题模式：显示多个选项 -->
        <div v-if="mode === 'title'" class="title-options">
          <div
            v-for="(option, index) in titleOptions"
            :key="index"
            class="title-option"
            @click="handleApply(option)"
          >
            <span class="option-index">{{ index + 1 }}</span>
            <span class="option-text">{{ option }}</span>
            <span class="option-action">使用</span>
          </div>
        </div>

        <!-- 扩写/润色模式：显示文本 -->
        <div v-else class="result-content">
          <div class="result-text">{{ result }}</div>
          <button class="apply-btn" @click="handleApply()">采纳</button>
        </div>
      </template>

      <div v-else-if="status === 'error'" class="error-content">
        <span class="error-msg">{{ errorMsg }}</span>
        <button class="retry-link" @click="handleRetry">重试</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-assist-panel {
  margin-top: 8px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  overflow: hidden;
  animation: slideDown 0.2s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-badge {
  background: rgba(255, 255, 255, 0.3);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.mode-label {
  font-size: 13px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.retry-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
  transition: background 0.2s;
}

.retry-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
}

.retry-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  width: 24px;
  height: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.close-btn:hover {
  opacity: 1;
}

.close-btn svg {
  width: 16px;
  height: 16px;
}

.panel-content {
  padding: 14px;
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-options {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.title-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.title-option:hover {
  border-color: #667eea;
  background: #f0f2ff;
}

.option-index {
  width: 24px;
  height: 24px;
  background: #667eea;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.option-text {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.option-action {
  color: #667eea;
  font-size: 12px;
  font-weight: 500;
  opacity: 0;
  transition: opacity 0.2s;
}

.title-option:hover .option-action {
  opacity: 1;
}

.result-content {
  width: 100%;
}

.result-text {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
  margin-bottom: 12px;
}

.apply-btn {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.apply-btn:hover {
  opacity: 0.9;
}

.error-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.error-msg {
  color: #dc3545;
  font-size: 13px;
}

.retry-link {
  background: none;
  border: none;
  color: #667eea;
  font-size: 13px;
  cursor: pointer;
  text-decoration: underline;
}

.retry-link:hover {
  color: #764ba2;
}
</style>
