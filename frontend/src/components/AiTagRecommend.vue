<script setup>
import { ref, watch } from 'vue'
import { recommendTags } from '../api/ai.js'
import AiLoading from './AiLoading.vue'

const props = defineProps({
  title: { type: String, default: '' },
  content: { type: String, default: '' },
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['select'])

const status = ref('idle') // idle | loading | success | error
const tags = ref([])
const errorMsg = ref('')

const handleRecommend = async () => {
  if (!props.title.trim()) return

  status.value = 'loading'
  errorMsg.value = ''
  tags.value = []

  try {
    const result = await recommendTags(props.title, props.content)
    tags.value = result.tags || []
    status.value = tags.value.length > 0 ? 'success' : 'error'
    if (tags.value.length === 0) {
      errorMsg.value = '未能生成推荐标签'
    }
  } catch (e) {
    status.value = 'error'
    errorMsg.value = e.message || '标签推荐失败'
  }
}

const handleSelect = (tag) => {
  emit('select', tag)
}

watch(() => props.visible, (newVal) => {
  if (newVal && props.title.trim() && status.value === 'idle') {
    handleRecommend()
  }
})
</script>

<template>
  <div class="ai-tag-recommend" v-if="visible">
    <div class="recommend-header">
      <span class="ai-badge">AI</span>
      <span class="recommend-label">智能推荐</span>
      <button class="refresh-btn" @click="handleRecommend" :disabled="status === 'loading'">
        重新推荐
      </button>
    </div>

    <div class="recommend-content">
      <AiLoading v-if="status === 'loading'" size="small" />

      <div v-else-if="status === 'success'" class="tag-list">
        <span
          v-for="tag in tags"
          :key="tag"
          class="ai-tag"
          @click="handleSelect(tag)"
        >
          # {{ tag }}
        </span>
      </div>

      <div v-else-if="status === 'error'" class="error-msg">
        {{ errorMsg }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-tag-recommend {
  margin-top: 12px;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.recommend-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.ai-badge {
  background: rgba(255, 255, 255, 0.3);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.recommend-label {
  font-size: 13px;
  opacity: 0.9;
}

.refresh-btn {
  margin-left: auto;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
  transition: background 0.2s;
}

.refresh-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
}

.refresh-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.recommend-content {
  min-height: 32px;
  display: flex;
  align-items: center;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.ai-tag {
  background: rgba(255, 255, 255, 0.25);
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  backdrop-filter: blur(10px);
}

.ai-tag:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: translateY(-1px);
}

.error-msg {
  font-size: 12px;
  opacity: 0.8;
}
</style>
