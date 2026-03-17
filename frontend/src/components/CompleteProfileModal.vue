<script setup>
import { ref } from 'vue'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['complete'])

const nickname = ref('')
const gender = ref('保密') // 男, 女, 保密
const avatarFile = ref(null)
const avatarPreview = ref(props.user.avatar || '') // Use existing or empty
const loading = ref(false)
const errorMessage = ref('')

// File upload handler
const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 2 * 1024 * 1024) {
      errorMessage.value = '图片大小不能超过 2MB'
      return
    }
    avatarFile.value = file
    const reader = new FileReader()
    reader.onload = (e) => {
      avatarPreview.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const handleSubmit = async () => {
  if (!nickname.value.trim()) {
    errorMessage.value = '请输入昵称'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    // 1. Upload Avatar if changed
    let avatarUrl = props.user.avatar
    if (avatarFile.value) {
      const formData = new FormData()
      formData.append('file', avatarFile.value)
      
      const uploadRes = await fetch('http://localhost:8080/api/uploads', {
        method: 'POST',
        body: formData
      })
      
      if (!uploadRes.ok) throw new Error('头像上传失败')
      const uploadData = await uploadRes.json()
      // Fix URL port 3000 issue if backend returns it
      avatarUrl = uploadData.url.replace('http://localhost:3000', '/api')
    }

    // 2. Update Profile
    const updateRes = await fetch('http://localhost:8080/api/auth/profile', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: props.user.id,
        nickname: nickname.value,
        gender: gender.value,
        avatar: avatarUrl
      })
    })

    if (!updateRes.ok) throw new Error('资料更新失败')
    const updatedUser = await updateRes.json()
    
    // Fix avatar URL in updated user if needed
    if (updatedUser.avatar && updatedUser.avatar.startsWith('http://localhost:3000')) {
        updatedUser.avatar = updatedUser.avatar.replace('http://localhost:3000', '/api')
    }

    emit('complete', updatedUser)

  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="modal-overlay">
    <div class="complete-modal">
      <div class="modal-header">
        <h2>完善资料</h2>
        <p class="subtitle">让我们更了解你</p>
      </div>

      <div class="form-content">
        <!-- Avatar Upload -->
        <div class="avatar-upload">
          <div class="avatar-preview" @click="$refs.fileInput.click()">
            <img v-if="avatarPreview" :src="avatarPreview" alt="Avatar" />
            <div v-else class="placeholder">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
            </div>
            <div class="upload-icon">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>
            </div>
          </div>
          <input 
            ref="fileInput"
            type="file" 
            accept="image/*" 
            class="hidden-input"
            @change="handleFileChange"
          />
          <span class="upload-hint">点击上传头像</span>
        </div>

        <!-- Form Fields -->
        <div class="form-group">
          <label>昵称</label>
          <input 
            v-model="nickname" 
            type="text" 
            placeholder="给自己起个好听的名字" 
            class="input-field"
          />
        </div>

        <div class="form-group">
          <label>性别</label>
          <div class="gender-options">
            <label class="gender-opt male" :class="{ active: gender === '男' }">
              <input type="radio" value="男" v-model="gender" />
              <span>男生</span>
            </label>
            <label class="gender-opt female" :class="{ active: gender === '女' }">
              <input type="radio" value="女" v-model="gender" />
              <span>女生</span>
            </label>
            <label class="gender-opt" :class="{ active: gender === '保密' }">
              <input type="radio" value="保密" v-model="gender" />
              <span>保密</span>
            </label>
          </div>
        </div>

        <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>

        <button class="submit-btn" :disabled="loading" @click="handleSubmit">
          {{ loading ? '保存中...' : '完成进入首页' }}
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
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(5px);
}

.complete-modal {
  background: var(--white);
  border-radius: 20px;
  width: 400px;
  max-width: 90vw;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

.modal-header {
  text-align: center;
  margin-bottom: 32px;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.subtitle {
  color: var(--text-secondary);
  font-size: 14px;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #f5f5f5;
  position: relative;
  cursor: pointer;
  overflow: hidden;
  border: 2px solid var(--border-color);
  transition: all 0.2s;
}

.avatar-preview:hover {
  border-color: var(--primary-color);
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.placeholder svg {
  width: 40px;
  height: 40px;
}

.upload-icon {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-preview:hover .upload-icon {
  opacity: 1;
}

.upload-icon svg {
  width: 16px;
  height: 16px;
}

.hidden-input {
  display: none;
}

.upload-hint {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 12px;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.input-field {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  background: var(--bg-color);
  color: var(--text-primary);
  transition: all 0.2s;
}

.input-field:focus {
  outline: none;
  border-color: var(--primary-color);
  background: var(--white);
}

.gender-options {
  display: flex;
  gap: 12px;
}

.gender-opt {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--text-secondary);
}

.gender-opt input {
  display: none;
}

.gender-opt.active {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(var(--primary-color-rgb), 0.05);
}

.gender-opt.male.active {
  border-color: #5d92ff;
  color: #5d92ff;
  background: rgba(93, 146, 255, 0.1);
}

.gender-opt.female.active {
  border-color: #ff57a9;
  color: #ff57a9;
  background: rgba(255, 87, 169, 0.1);
}

.error-msg {
  color: #ff2442;
  font-size: 12px;
  text-align: center;
  margin-bottom: 16px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 36, 66, 0.3);
}
</style>
