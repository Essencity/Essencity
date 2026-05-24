<script setup>
import { ref, nextTick, computed } from 'vue'
import { askAiAboutPost } from '../api/ai.js'
import AiLoading from './AiLoading.vue'

const props = defineProps({
  postId: { type: Number, required: true },
  title: { type: String, default: '' },
  content: { type: String, default: '' }
})

const emit = defineEmits(['close'])

const messages = ref([])
const inputText = ref('')
const isLoading = ref(false)
const chatContainer = ref(null)

const canSend = computed(() => inputText.value.trim().length > 0 && !isLoading.value)

const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const handleSend = async () => {
  if (!canSend.value) return

  const question = inputText.value.trim()
  inputText.value = ''

  // 添加用户消息
  messages.value.push({ role: 'user', text: question })
  await scrollToBottom()

  isLoading.value = true

  try {
    // 构建历史记录（最近5轮）
    const history = []
    const recentMessages = messages.value.slice(-11, -1) // 最多10条历史（5轮）
    for (const msg of recentMessages) {
      history.push({
        role: msg.role === 'user' ? 'user' : 'assistant',
        content: msg.text
      })
    }

    const response = await askAiAboutPost(
      props.postId,
      props.title,
      props.content,
      question,
      history
    )

    messages.value.push({ role: 'ai', text: response.answer || '抱歉，我无法回答这个问题。' })
  } catch (e) {
    messages.value.push({ role: 'ai', text: '抱歉，回答失败，请稍后重试。' })
  } finally {
    isLoading.value = false
    await scrollToBottom()
  }
}

const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}
</script>

<template>
  <div class="ai-qa-overlay" @click.self="emit('close')">
    <div class="ai-qa-modal">
      <div class="modal-header">
        <div class="header-left">
          <span class="ai-badge">AI</span>
          <span class="header-title">帖子问答</span>
        </div>
        <button class="close-btn" @click="emit('close')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <div class="post-info">
        <span class="post-title">{{ title }}</span>
      </div>

      <div class="chat-container" ref="chatContainer">
        <div v-if="messages.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
            </svg>
          </div>
          <p class="empty-text">基于帖子内容提问</p>
          <p class="empty-hint">例如：这个怎么做？有什么注意事项？</p>
        </div>

        <template v-else>
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role]"
          >
            <div class="message-avatar">
              <span v-if="msg.role === 'user'">你</span>
              <span v-else class="ai-avatar">AI</span>
            </div>
            <div class="message-bubble">
              <div class="message-text">{{ msg.text }}</div>
            </div>
          </div>

          <div v-if="isLoading" class="message ai">
            <div class="message-avatar">
              <span class="ai-avatar">AI</span>
            </div>
            <div class="message-bubble loading-bubble">
              <AiLoading size="small" />
            </div>
          </div>
        </template>
      </div>

      <div class="input-area">
        <textarea
          v-model="inputText"
          placeholder="输入你的问题..."
          @keydown="handleKeydown"
          :disabled="isLoading"
          rows="1"
        />
        <button
          class="send-btn"
          @click="handleSend"
          :disabled="!canSend"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-qa-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.ai-qa-modal {
  width: 90%;
  max-width: 500px;
  height: 80vh;
  max-height: 600px;
  background: white;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
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

.header-title {
  font-size: 15px;
  font-weight: 500;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  width: 28px;
  height: 28px;
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
  width: 18px;
  height: 18px;
}

.post-info {
  padding: 10px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.post-title {
  font-size: 13px;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}

.empty-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-text {
  font-size: 14px;
  margin-bottom: 4px;
}

.empty-hint {
  font-size: 12px;
  opacity: 0.7;
}

.message {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e9ecef;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #666;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: #667eea;
  color: white;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.message-bubble {
  max-width: 75%;
  padding: 10px 14px;
  border-radius: 12px;
  background: #f1f3f5;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
}

.message.user .message-bubble {
  background: #667eea;
  color: white;
}

.message.ai .message-bubble {
  background: #f1f3f5;
}

.loading-bubble {
  padding: 12px 18px;
}

.message-text {
  white-space: pre-wrap;
  word-break: break-word;
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid #e9ecef;
  background: white;
}

.input-area textarea {
  flex: 1;
  border: 1px solid #e9ecef;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 14px;
  resize: none;
  outline: none;
  transition: border-color 0.2s;
  max-height: 100px;
  line-height: 1.4;
}

.input-area textarea:focus {
  border-color: #667eea;
}

.input-area textarea:disabled {
  background: #f8f9fa;
  cursor: not-allowed;
}

.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.2s;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.send-btn svg {
  width: 18px;
  height: 18px;
}

@media (max-width: 768px) {
  .ai-qa-modal {
    width: 100%;
    height: 100vh;
    max-height: 100vh;
    border-radius: 0;
  }
}
</style>
