<script setup>
import { ref, computed } from 'vue'

const emit = defineEmits(['close', 'login-success'])

const mode = ref('login') // 'login' or 'register'
const username = ref('')
const password = ref('')
const confirmPassword = ref('')

const loading = ref(false)
const errorMessage = ref('')

const isFormValid = computed(() => {
  if (mode.value === 'login') {
    return username.value.length >= 3 && password.value.length >= 6
  } else {
    return username.value.length >= 3 && 
           password.value.length >= 6 && 
           password.value === confirmPassword.value
  }
})

const switchMode = () => {
  mode.value = mode.value === 'login' ? 'register' : 'login'
  errorMessage.value = ''
}

const handleSubmit = async () => {
  if (!isFormValid.value) return
  
  loading.value = true
  errorMessage.value = ''
  
  try {
    const endpoint = mode.value === 'login' ? '/auth/login' : '/auth/register'
    const body = mode.value === 'login' 
      ? { username: username.value, password: password.value }
      : { username: username.value, password: password.value }
    
    const response = await fetch(`/api${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
    
    const data = await response.json()
    
    if (!response.ok) {
      throw new Error(data.message || '操作失败')
    }
    
    // Fix URL if needed
    if (data.avatar && data.avatar.startsWith('http://localhost:3000')) {
      data.avatar = data.avatar.replace('http://localhost:3000', '/api')
    }

    // Success! Store user info and emit event
    localStorage.setItem('user', JSON.stringify(data))
    emit('login-success', data)
    
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="auth-modal">
      <button class="close-btn" @click="emit('close')">
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
        </svg>
      </button>
      
      <div class="modal-header">
        <h2>{{ mode === 'login' ? '登录' : '注册' }}</h2>
        <p class="subtitle">{{ mode === 'login' ? '欢迎回来！' : '创建一个新账号' }}</p>
      </div>
      
      <form @submit.prevent="handleSubmit" class="auth-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            id="username"
            v-model="username" 
            type="text" 
            placeholder="请输入用户名 (至少3位)"
            autocomplete="username"
          />
        </div>
        

        
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            id="password"
            v-model="password" 
            type="password" 
            placeholder="请输入密码 (至少6位)"
            autocomplete="current-password"
          />
        </div>
        
        <div v-if="mode === 'register'" class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input 
            id="confirmPassword"
            v-model="confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            autocomplete="new-password"
          />
          <p v-if="confirmPassword && password !== confirmPassword" class="error-text">
            两次密码不一致
          </p>
        </div>
        
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        
        <button 
          type="submit" 
          class="submit-btn"
          :disabled="!isFormValid || loading"
        >
          {{ loading ? '处理中...' : (mode === 'login' ? '登录' : '注册') }}
        </button>
      </form>
      
      <div class="mode-switch">
        <span>{{ mode === 'login' ? '还没有账号？' : '已有账号？' }}</span>
        <button @click="switchMode" class="switch-btn">
          {{ mode === 'login' ? '立即注册' : '去登录' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.auth-modal {
  background: white;
  border-radius: 16px;
  width: 400px;
  max-width: 90vw;
  padding: 32px;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
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

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f0f0f0;
  color: #333;
}

.close-btn svg {
  width: 20px;
  height: 20px;
}

.modal-header {
  text-align: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.subtitle {
  color: #888;
  font-size: 14px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #ff2442;
  box-shadow: 0 0 0 3px rgba(255, 36, 66, 0.1);
}

.error-text {
  color: #ff2442;
  font-size: 12px;
}

.error-message {
  color: #ff2442;
  font-size: 14px;
  text-align: center;
  padding: 8px;
  background: rgba(255, 36, 66, 0.1);
  border-radius: 8px;
}

.submit-btn {
  background: #ff2442;
  color: white;
  padding: 14px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 8px;
  transition: background 0.2s, transform 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: #e61e3a;
  transform: translateY(-1px);
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.mode-switch {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #666;
}

.switch-btn {
  color: #ff2442;
  font-weight: 500;
  margin-left: 4px;
}

.switch-btn:hover {
  text-decoration: underline;
}
</style>
