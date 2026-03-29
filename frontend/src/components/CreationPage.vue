<script setup>
import { ref, computed, defineProps } from 'vue'

const props = defineProps({
  currentUser: {
    type: Object,
    default: null
  }
})

const activeTab = ref('video') // 'video', 'image'
const videoFile = ref(null)
const videoUrl = ref('')
const coverUrl = ref('')
const videoInfo = ref(null)
const title = ref('')
const content = ref('')
const publishType = ref('immediate') // 'immediate' or 'scheduled'
const scheduledTime = ref('')
const isUploading = ref(false)
const showSettings = ref(false)

const fileInputRef = ref(null)
const coverInputRef = ref(null)
const coverSource = ref('auto') // 'auto' or 'manual'
const manualCoverUrl = ref('')

const tags = ['穿搭', '美食', '彩妆', '影视', '职场', '情感', '家居', '游戏', '旅行', '健身']
const selectedTag = ref('')
const customTagInput = ref('')
const showCustomTagInput = ref(false)

const toggleCustomTagInput = () => {
  showCustomTagInput.value = !showCustomTagInput.value
  if (showCustomTagInput.value) {
    customTagInput.value = ''
  }
}

const selectCustomTag = () => {
  const tag = customTagInput.value.trim().replace(/^#/, '')
  if (tag && !selectedTag.value) {
    selectedTag.value = '#' + tag
  }
  showCustomTagInput.value = false
  customTagInput.value = ''
}

const isCustomTag = computed(() => {
  return selectedTag.value && !tags.includes(selectedTag.value)
})

// Image handling
const imageFiles = ref([])
const imageUrls = ref([])
const imageInfos = ref([])
const imageInputRef = ref(null)

const formatFileSize = (bytes) => {
  if (bytes < 1024 * 1024) {
    return (bytes / 1024).toFixed(2) + ' KB'
  }
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

const formatDuration = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}min ${secs}s`
}

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  processVideoFile(file)
}

const handleDrop = (event) => {
  event.preventDefault()
  const file = event.dataTransfer.files[0]
  if (file && file.type.startsWith('video/')) {
    processVideoFile(file)
  }
}

const handleDragOver = (event) => {
  event.preventDefault()
}

const processVideoFile = (file) => {
  isUploading.value = true
  videoFile.value = file
  
  // Create video URL for preview
  videoUrl.value = URL.createObjectURL(file)
  
  // Create video element to extract info and cover
  const video = document.createElement('video')
  video.preload = 'metadata'
  video.src = videoUrl.value
  
  video.onloadedmetadata = () => {
    videoInfo.value = {
      name: file.name,
      size: formatFileSize(file.size),
      duration: formatDuration(video.duration),
      width: video.videoWidth,
      height: video.videoHeight
    }
    
    // Seek to 1 second to get a good cover frame
    video.currentTime = Math.min(1, video.duration * 0.1)
  }
  
  video.onseeked = () => {
    // Extract cover from video frame
    const canvas = document.createElement('canvas')
    canvas.width = video.videoWidth
    canvas.height = video.videoHeight
    const ctx = canvas.getContext('2d')
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
    coverUrl.value = canvas.toDataURL('image/jpeg', 0.8)
    isUploading.value = false
  }
  
  video.onerror = () => {
    console.error('Error loading video')
    isUploading.value = false
  }
}

const triggerFileInput = () => {
  fileInputRef.value?.click()
}

const replaceVideo = () => {
  videoFile.value = null
  videoUrl.value = ''
  coverUrl.value = ''
  manualCoverUrl.value = ''
  coverSource.value = 'auto'
  videoInfo.value = null
  triggerFileInput()
}

// Image logic
const triggerImageInput = () => {
  document.getElementById('imageInput').click()
}

const handleImageSelect = (event) => {
  const files = Array.from(event.target.files)
  if (files.length === 0) return

  const remainingSlots = 9 - imageFiles.value.length
  const filesToAdd = files.slice(0, remainingSlots)

  filesToAdd.forEach(file => {
    processImageFile(file)
  })
}

const handleImageDrop = (event) => {
  event.preventDefault()
  const files = Array.from(event.dataTransfer.files).filter(f => f.type.startsWith('image/'))
  if (files.length === 0) return

  const remainingSlots = 9 - imageFiles.value.length
  const filesToAdd = files.slice(0, remainingSlots)

  filesToAdd.forEach(file => {
    processImageFile(file)
  })
}

const processImageFile = (file) => {
  imageFiles.value.push(file)

  const reader = new FileReader()
  reader.onload = (e) => {
    imageUrls.value.push(e.target.result)

    const img = new Image()
    img.onload = () => {
      imageInfos.value.push({
        name: file.name,
        size: formatFileSize(file.size),
        width: img.width,
        height: img.height
      })
    }
    img.src = e.target.result
  }
  reader.readAsDataURL(file)
}

const removeImage = (index) => {
  imageFiles.value.splice(index, 1)
  imageUrls.value.splice(index, 1)
  imageInfos.value.splice(index, 1)
}

const replaceImage = (index) => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      imageFiles.value[index] = file
      const reader = new FileReader()
      reader.onload = (ev) => {
        imageUrls.value[index] = ev.target.result
        const img = new Image()
        img.onload = () => {
          imageInfos.value[index] = {
            name: file.name,
            size: formatFileSize(file.size),
            width: img.width,
            height: img.height
          }
        }
        img.src = ev.target.result
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

const triggerCoverInput = () => {
  coverInputRef.value?.click()
}

const handleCoverUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const reader = new FileReader()
  reader.onload = (e) => {
    manualCoverUrl.value = e.target.result
    coverSource.value = 'manual'
  }
  reader.readAsDataURL(file)
}

const selectAutoCover = () => {
  coverSource.value = 'auto'
}

const selectManualCover = () => {
  if (manualCoverUrl.value) {
    coverSource.value = 'manual'
  } else {
    triggerCoverInput()
  }
}

// Get the active cover URL based on selection
const activeCoverUrl = computed(() => {
  return coverSource.value === 'manual' && manualCoverUrl.value 
    ? manualCoverUrl.value 
    : coverUrl.value
})

const handlePublish = async () => {
  if (activeTab.value === 'video' && (!videoFile.value || !title.value.trim())) {
    alert('请上传视频并填写标题')
    return
  }
  if (activeTab.value === 'image' && (imageFiles.value.length === 0 || !title.value.trim())) {
    alert('请上传图片并填写标题')
    return
  }

  try {
    let finalUrl = ''
    let finalCoverUrl = ''
    let imageUrls = []
    let postType = activeTab.value

    if (activeTab.value === 'video') {
      const videoFormData = new FormData()
      videoFormData.append('file', videoFile.value)

      const uploadRes = await fetch('/api/posts/upload', {
        method: 'POST',
        body: videoFormData
      })
      const uploadData = await uploadRes.json()
      finalUrl = uploadData.url

      const coverToUpload = coverSource.value === 'manual' && manualCoverUrl.value
        ? manualCoverUrl.value
        : coverUrl.value

      if (coverToUpload) {
        const coverBlob = await fetch(coverToUpload).then(r => r.blob())
        const coverFormData = new FormData()
        coverFormData.append('file', coverBlob, 'cover.jpg')

        const coverRes = await fetch('/api/posts/upload', {
          method: 'POST',
          body: coverFormData
        })
        const coverData = await coverRes.json()
        finalCoverUrl = coverData.url
      }
    } else {
      for (let i = 0; i < imageFiles.value.length; i++) {
        const imageFormData = new FormData()
        imageFormData.append('file', imageFiles.value[i])

        const uploadRes = await fetch('/api/posts/upload', {
          method: 'POST',
          body: imageFormData
        })
        const uploadData = await uploadRes.json()
        imageUrls.push(uploadData.url)
      }
      finalUrl = imageUrls[0]
      finalCoverUrl = imageUrls[0]
    }

    const postRes = await fetch('/api/posts', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: title.value,
        description: content.value,
        type: postType,
        url: finalUrl,
        cover_url: finalCoverUrl,
        imageUrls: imageUrls,
        author_id: props.currentUser?.id || 0,
        tag: selectedTag.value
      })
    })

    if (postRes.ok) {
      alert('发布成功！')
      videoFile.value = null
      videoUrl.value = ''
      coverUrl.value = ''
      videoInfo.value = null

      imageFiles.value = []
      imageUrls.value = []
      imageInfos.value = []

      title.value = ''
      content.value = ''
    }
  } catch (error) {
    console.error('Publish error:', error)
    alert('发布失败，请重试')
  }
}


const canPublish = computed(() => {
  if (activeTab.value === 'video') return videoFile.value && title.value.trim().length > 0
  if (activeTab.value === 'image') return imageFiles.value.length > 0 && title.value.trim().length > 0
  return false
})
</script>

<template>
  <div class="creation-container">
    <!-- Hidden file inputs -->
    <input 
      ref="fileInputRef"
      type="file" 
      accept="video/*" 
      @change="handleFileSelect" 
      style="display: none"
    />
    <input 
      ref="coverInputRef"
      type="file" 
      accept="image/*" 
      @change="handleCoverUpload" 
      style="display: none"
    />
    <input 
      id="imageInput"
      ref="imageInputRef"
      type="file" 
      accept="image/*" 
      @change="handleImageSelect" 
      style="display: none"
    />
    
    <div class="main-body">
      <main class="content-area">
        <!-- Tabs -->
        <div class="tabs">
          <span 
            class="tab" 
            :class="{ active: activeTab === 'video' }"
            @click="activeTab = 'video'"
          >上传视频</span>
          <span 
            class="tab" 
            :class="{ active: activeTab === 'image' }"
            @click="activeTab = 'image'"
          >上传图文</span>
          <span class="drafts">草稿箱(0)</span>
        </div>

        <!-- Video Section -->
        <template v-if="activeTab === 'video'">
          <template v-if="!videoFile">
            <div 
              class="upload-area"
              @drop="handleDrop"
              @dragover="handleDragOver"
              @click="triggerFileInput"
            >
              <div class="upload-content">
                <div class="cloud-icon">
                  <svg viewBox="0 0 24 24" width="64" height="64" fill="#e0e0e0">
                    <path d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"/>
                  </svg>
                </div>
                <p class="upload-text">拖拽视频到此或点击上传</p>
                <button class="upload-btn" @click.stop="triggerFileInput">上传视频</button>
              </div>
            </div>

            <div class="specs-grid">
              <div class="spec-item">
                <h3>视频大小</h3>
                <p>支持时长60分钟以内，最大20GB的视频文件</p>
              </div>
              <div class="spec-item">
                <h3>视频格式</h3>
                <p>支持常用视频格式，推荐使用mp4、mov</p>
              </div>
              <div class="spec-item">
                <h3>视频分辨率</h3>
                <p>推荐上传720P（1280*720）及以上视频</p>
              </div>
            </div>
          </template>

          <template v-else>
            <div class="edit-form">
              <!-- Video Info -->
              <div class="video-info-section">
                <div class="video-info-header">
                  <span class="file-name">{{ videoInfo?.name }}</span>
                  <button class="replace-btn" @click="replaceVideo">替换视频</button>
                </div>
                <div class="video-meta">
                  <span class="success-badge">✓ 上传成功</span>
                  <span>视频大小：{{ videoInfo?.size }}</span>
                  <span>视频时长：{{ videoInfo?.duration }}</span>
                </div>
              </div>

              <!-- Cover Section -->
              <div class="section">
                <h3 class="section-title">封面设置</h3>
                <div class="cover-options">
                  <div 
                    class="cover-option" 
                    :class="{ selected: coverSource === 'auto' }"
                    @click="selectAutoCover"
                  >
                    <img v-if="coverUrl" :src="coverUrl" alt="自动截取" class="cover-image" />
                    <div v-else class="cover-placeholder">截取中...</div>
                    <span class="cover-label">自动截取</span>
                    <div v-if="coverSource === 'auto'" class="cover-check">✓</div>
                  </div>
                  
                  <div 
                    class="cover-option" 
                    :class="{ selected: coverSource === 'manual' && manualCoverUrl }"
                    @click="selectManualCover"
                  >
                    <img v-if="manualCoverUrl" :src="manualCoverUrl" alt="手动上传" class="cover-image" />
                    <div v-else class="cover-placeholder upload-hint">
                      <svg viewBox="0 0 24 24" width="24" height="24" fill="#999">
                        <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                      </svg>
                      <span>上传封面</span>
                    </div>
                    <span class="cover-label">{{ manualCoverUrl ? '手动上传' : '点击上传' }}</span>
                    <div v-if="coverSource === 'manual' && manualCoverUrl" class="cover-check">✓</div>
                  </div>
                </div>
              </div>

              <!-- Title -->
              <div class="section">
                <h3 class="section-title">标题 <span class="char-count">{{ title.length }}/20</span></h3>
                <input 
                  v-model="title"
                  type="text" 
                  class="title-input"
                  placeholder="填写标题会有更多赞哦～"
                  maxlength="20"
                />
              </div>

              <!-- Tag Selection -->
              <div class="section">
                <h3 class="section-title">添加标签</h3>
                <div class="tag-list">
                  <span
                    v-for="tag in tags"
                    :key="tag"
                    class="tag-item"
                    :class="{ active: selectedTag === tag }"
                    @click="selectedTag = tag"
                  >
                    {{ tag }}
                  </span>
                  <span
                    v-if="!showCustomTagInput && !isCustomTag"
                    class="tag-item custom-trigger"
                    @click="toggleCustomTagInput"
                  >
                    + 自定义
                  </span>
                  <span
                    v-if="isCustomTag"
                    class="tag-item active custom-tag"
                    @click="toggleCustomTagInput"
                  >
                    {{ selectedTag }}
                  </span>
                </div>
                <div v-if="showCustomTagInput" class="custom-tag-input-wrapper">
                  <span class="tag-prefix">#</span>
                  <input
                    v-model="customTagInput"
                    type="text"
                    class="custom-tag-input"
                    placeholder="输入话题"
                    maxlength="20"
                    @keyup.enter="selectCustomTag"
                    @blur="selectCustomTag"
                  />
                </div>
              </div>

              <!-- Content -->
              <div class="section">
                <h3 class="section-title">正文内容</h3>
                <textarea 
                  v-model="content"
                  class="content-input"
                  placeholder="输入正文描述，真诚有价值的分享予人温暖"
                  rows="4"
                ></textarea>
              </div>

              <div class="publish-actions">
                <button 
                  class="publish-btn" 
                  :disabled="!canPublish"
                  @click="handlePublish"
                >
                  发布
                </button>
                <button class="save-draft-btn">暂存离开</button>
              </div>
            </div>
          </template>
        </template>

        <!-- Image Section -->
        <template v-else-if="activeTab === 'image'">
          <template v-if="!imageFile">
            <div 
              class="upload-area"
              @drop="handleImageDrop"
              @dragover="handleDragOver"
              @click="triggerImageInput"
            >
              <div class="upload-content">
                <div class="cloud-icon">
                  <svg viewBox="0 0 24 24" width="64" height="64" fill="#e0e0e0">
                    <path d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"/>
                  </svg>
                </div>
                <p class="upload-text">拖拽图片到此或点击上传</p>
                <button class="upload-btn" @click.stop="triggerImageInput">上传图片</button>
              </div>
            </div>

            <div class="specs-grid">
              <div class="spec-item">
                <h3>图片大小</h3>
                <p>支持最大20MB的图片文件</p>
              </div>
              <div class="spec-item">
                <h3>图片格式</h3>
                <p>支持jpg、png、webp等常用格式</p>
              </div>
              <div class="spec-item">
                <h3>图片分辨率</h3>
                <p>推荐上传高清图片，展示效果更好</p>
              </div>
            </div>
          </template>

          <template v-else>
            <div class="edit-form">
              <!-- Image Grid -->
              <div class="section">
                <h3 class="section-title">图片 ({{ imageFiles.length }}/9)</h3>
                <div class="image-grid">
                  <div
                    v-for="(img, index) in imageUrls"
                    :key="index"
                    class="image-grid-item"
                  >
                    <img :src="img" alt="预览" class="grid-image" />
                    <div class="image-overlay">
                      <button class="overlay-btn" @click="replaceImage(index)">
                        <svg viewBox="0 0 24 24" fill="currentColor">
                          <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                        </svg>
                      </button>
                      <button class="overlay-btn remove" @click="removeImage(index)">
                        <svg viewBox="0 0 24 24" fill="currentColor">
                          <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                        </svg>
                      </button>
                    </div>
                    <span v-if="index === 0" class="cover-badge">封面</span>
                  </div>
                  <div
                    v-if="imageFiles.length < 9"
                    class="image-grid-item add-more"
                    @click="triggerImageInput"
                  >
                    <svg viewBox="0 0 24 24" fill="currentColor">
                      <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                    </svg>
                    <span>添加图片</span>
                  </div>
                </div>
              </div>

              <!-- Title -->
              <div class="section">
                <h3 class="section-title">标题 <span class="char-count">{{ title.length }}/20</span></h3>
                <input 
                  v-model="title"
                  type="text" 
                  class="title-input"
                  placeholder="填写标题会有更多赞哦～"
                  maxlength="20"
                />
              </div>

              <!-- Tag Selection -->
              <div class="section">
                <h3 class="section-title">添加标签</h3>
                <div class="tag-list">
                  <span 
                    v-for="tag in tags" 
                    :key="tag"
                    class="tag-item"
                    :class="{ active: selectedTag === tag }"
                    @click="selectedTag = tag"
                  >
                    {{ tag }}
                  </span>
                </div>
              </div>

              <!-- Content -->
              <div class="section">
                <h3 class="section-title">正文内容</h3>
                <textarea 
                  v-model="content"
                  class="content-input"
                  placeholder="输入正文描述，真诚有价值的分享予人温暖"
                  rows="4"
                ></textarea>
              </div>

              <div class="publish-actions">
                <button 
                  class="publish-btn" 
                  :disabled="!canPublish"
                  @click="handlePublish"
                >
                  发布
                </button>
                <button class="save-draft-btn">暂存离开</button>
              </div>
            </div>
          </template>
        </template>
      </main>
    </div>
  </div>
</template>

<style scoped>
.creation-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--bg-color);
}

.main-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: var(--bg-color);
}

.tabs {
  display: flex;
  align-items: center;
  gap: 32px;
  background: var(--white);
  padding: 16px 24px;
  border-radius: 4px 4px 0 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.tab {
  font-size: 16px;
  color: var(--text-secondary);
  cursor: pointer;
  padding-bottom: 4px;
}

.tab.active {
  color: var(--primary-color);
  font-weight: 600;
  border-bottom: 2px solid var(--primary-color);
}

.drafts {
  margin-left: auto;
  font-size: 14px;
  color: var(--text-secondary);
}

/* Upload Area */
.upload-area {
  background-color: var(--white);
  border-radius: 4px;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed var(--border-color);
  margin-bottom: 32px;
  cursor: pointer;
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: var(--primary-color);
}

.upload-content {
  text-align: center;
}

.upload-text {
  margin: 16px 0 24px;
  color: var(--text-secondary);
}

.upload-btn {
  background-color: var(--primary-color);
  color: white;
  padding: 10px 32px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.specs-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.spec-item h3 {
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.spec-item p {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
}

/* Edit Form (Full Width) */
.edit-form {
  background: var(--white);
  border-radius: 8px;
  padding: 24px;
  width: 100%;
}

.video-info-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.video-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.file-name {
  font-weight: 600;
  font-size: 16px;
}

.replace-btn {
  color: var(--primary-color);
  font-size: 14px;
}

.video-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-secondary);
}

.success-badge {
  color: #52c41a;
}

.section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.char-count {
  color: var(--text-secondary);
  font-weight: normal;
}

.cover-options {
  display: flex;
  gap: 16px;
}

.cover-option {
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.2s;
}

.cover-option.selected {
  box-shadow: 0 0 0 2px var(--primary-color);
}

.cover-image {
  width: 120px;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  display: block;
}

.cover-placeholder {
  width: 120px;
  height: 160px;
  background: #f5f5f5;
  border: 1px dashed #d0d0d0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 13px;
}

.cover-placeholder.upload-hint {
  flex-direction: column;
  gap: 8px;
}

.cover-label {
  position: absolute;
  bottom: 8px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0,0,0,0.6);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
}

.cover-check {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 20px;
  height: 20px;
  background: var(--primary-color);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.image-grid-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.grid-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-grid-item:hover .image-overlay {
  opacity: 1;
}

.overlay-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #333;
}

.overlay-btn:hover {
  background: #f0f0f0;
}

.overlay-btn.remove:hover {
  background: #ff4d4f;
  color: white;
}

.overlay-btn svg {
  width: 16px;
  height: 16px;
}

.cover-badge {
  position: absolute;
  bottom: 8px;
  left: 8px;
  background: var(--primary-color);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.image-grid-item.add-more {
  border: 2px dashed #d0d0d0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #999;
  transition: all 0.2s;
}

.image-grid-item.add-more:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.image-grid-item.add-more svg {
  width: 32px;
  height: 32px;
}

.image-grid-item.add-more span {
  font-size: 13px;
}

.title-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
}

.title-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.content-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
}

.content-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.settings-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.settings-toggle span {
  font-size: 10px;
  transition: transform 0.3s;
}

.settings-toggle span.rotate {
  transform: rotate(180deg);
}

.settings-panel {
  margin-top: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.setting-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.setting-row:last-child {
  margin-bottom: 0;
}

.setting-label {
  width: 80px;
  font-size: 14px;
  color: var(--text-secondary);
}

.publish-options {
  display: flex;
  gap: 24px;
}

.radio-option {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
}

.datetime-input {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
}

.publish-actions {
  display: flex;
  gap: 16px;
  margin-top: 32px;
}

.publish-btn {
  background: var(--primary-color);
  color: white;
  padding: 12px 48px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
}

.publish-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.save-draft-btn {
  padding: 12px 32px;
  border: 1px solid #e0e0e0;
  border-radius: 24px;
  font-size: 14px;
  color: var(--text-secondary);
}

/* Preview Section */
.preview-section {
  width: 320px;
  flex-shrink: 0;
}

.preview-tabs {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.preview-tab {
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  padding-bottom: 4px;
}

.preview-tab.active {
  color: var(--primary-color);
  border-bottom: 2px solid var(--primary-color);
}

.phone-frame {
  background: #1a1a1a;
  border-radius: 32px;
  padding: 12px;
  position: relative;
}

.phone-notch {
  display: flex;
  justify-content: space-between;
  padding: 8px 20px;
  color: white;
  font-size: 12px;
}

/* New Phone Preview Styles */
.phone-status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  color: white;
  font-size: 12px;
}

.status-icons {
  display: flex;
  gap: 4px;
  font-size: 10px;
}

.phone-screen {
  position: relative;
  background: #1a1a1a;
  min-height: 560px;
  display: flex;
  flex-direction: column;
}

.phone-nav {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  color: white;
  font-size: 24px;
  z-index: 10;
}

.nav-back {
  cursor: pointer;
  font-weight: 300;
}

.nav-search {
  font-size: 18px;
}

.phone-video-container {
  background: #000;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.phone-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.phone-content-area {
  flex: 1;
  background: white;
  padding: 16px;
  border-radius: 0;
}

.phone-post-content {
  margin-bottom: 16px;
}

.phone-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.phone-text {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.phone-user-section {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.phone-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.phone-user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.phone-username {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.phone-meta {
  font-size: 11px;
  color: #999;
}

.phone-follow-btn {
  background: var(--primary-color);
  color: white;
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 12px;
}

/* Right side action bar */
.phone-side-actions {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 20px;
  z-index: 10;
}

.side-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
  font-size: 10px;
  gap: 4px;
  text-shadow: 0 1px 3px rgba(0,0,0,0.5);
}

.side-action-label {
  font-size: 11px;
  background: rgba(255,255,255,0.2);
  padding: 4px 8px;
  border-radius: 12px;
}

/* Bottom comment bar */
.phone-comment-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #1a1a1a;
  border-radius: 0 0 20px 20px;
}

.phone-comment-input {
  flex: 1;
  background: #333;
  border: none;
  border-radius: 20px;
  padding: 10px 16px;
  color: #999;
  font-size: 13px;
}

.comment-icons {
  font-size: 18px;
}

/* Tag Selector */
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tag-item {
  padding: 6px 16px;
  background: var(--white);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.tag-item:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.tag-item.active {
  background: #fffbfb;
  border-color: var(--primary-color);
  color: var(--primary-color);
  font-weight: 500;
}

.tag-item.custom-trigger {
  border-style: dashed;
}

.tag-item.custom-trigger:hover {
  background: var(--bg-color);
}

.custom-tag-input-wrapper {
  display: flex;
  align-items: center;
  margin-top: 12px;
  padding: 8px 12px;
  background: var(--white);
  border: 1px solid var(--primary-color);
  border-radius: 20px;
  width: fit-content;
}

.tag-prefix {
  color: var(--primary-color);
  font-size: 14px;
  font-weight: 500;
}

.custom-tag-input {
  border: none;
  outline: none;
  font-size: 14px;
  width: 150px;
  background: transparent;
}
</style>

