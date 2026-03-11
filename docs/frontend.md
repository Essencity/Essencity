# Essencity 前端设计文档


> 项目：Essencity - 仿小红书社交平台  
> 技术栈：Vue 3 + Vite + Element Plus + Axios  
> 文档版本：v1.0  
> 日期：2026-03-11  
> 项目周期：15周（与后端同步）

* * *

## 1. 前端架构概述


Essencity 前端基于 **Vue 3 Composition API** 构建，采用 **Vite** 作为构建工具，**Element Plus** 作为 UI 组件库。

前端核心职责：

* 用户界面渲染与交互体验
* 路由管理与页面导航
* 状态管理（用户状态、笔记数据）
* API 请求封装与错误处理
* 响应式布局（PC + 移动端适配）
* 图片上传与预览
* Web Speech API 语音搜索集成
* AI 总结功能展示

系统采用 **JWT Token** 进行身份认证，通过 **Axios 拦截器** 统一处理请求/响应。

* * *

## 2. 技术栈详情


| 类别 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Vue.js | 3.x | Composition API 风格 |
| 构建工具 | Vite | 5.x | 快速冷启动、HMR |
| UI 组件库 | Element Plus | 2.x | 适配桌面端 + 移动端 |
| 路由 | Vue Router | 4.x | History 模式 |
| HTTP 客户端 | Axios | 1.x | 请求/响应拦截器 |
| 状态管理 | Pinia | 2.x | 轻量级状态管理 |
| 样式预处理器 | SCSS | - | 变量、混入、嵌套 |
| 语音识别 | Web Speech API | - | 浏览器原生 API |
| 包管理器 | npm | - | Node.js 22.x |
| 开发语言 | JavaScript / TypeScript | ES2020+ | 建议使用 TypeScript |

* * *

## 3. 前端架构设计

### 3.1 整体架构

```
Browser/移动端
     │
     ▼
Vue 3 Application (SPA)
  ├─ Router (页面路由)
  ├─ Pinia (状态管理)
  ├─ Axios (API 通信)
  ├─ Components (组件层)
  ├─ Views (页面层)
  ├─ Composables (组合式函数)
  └─ Utils (工具函数)
     │
     ▼
REST API (Spring Boot)
```

### 3.2 架构分层说明

| 层级 | 职责 | 对应目录 |
|------|------|----------|
| **Presentation** | UI 渲染、用户交互 | `components/`, `views/` |
| **State Management** | 全局状态、数据缓存 | `stores/` (Pinia) |
| **Business Logic** | 业务逻辑复用 | `composables/` |
| **Data Access** | API 请求、数据转换 | `api/` |
| **Infrastructure** | 工具函数、常量配置 | `utils/`, `config/` |

* * *

## 4. 项目目录结构


```
essencity-frontend/
├── public/                          # 静态资源（不经过构建）
│   ├── favicon.ico
│   └── images/
│       └── default-avatar.png       # 默认头像
├── src/
│   ├── api/                         # API 接口封装
│   │   ├── index.js                 # Axios 实例配置
│   │   ├── user.js                  # 用户模块 API
│   │   ├── note.js                  # 笔记模块 API
│   │   ├── comment.js               # 评论模块 API
│   │   ├── topic.js                 # 话题模块 API
│   │   ├── search.js                # 搜索模块 API
│   │   ├── draft.js                 # 草稿模块 API
│   │   └── ai.js                    # AI 模块 API
│   │
│   ├── assets/                      # 构建资源
│   │   ├── images/                  # 图片资源
│   │   └── styles/                  # 全局样式
│   │       ├── variables.scss       # SCSS 变量
│   │       ├── mixins.scss          # SCSS 混入
│   │       ├── global.scss          # 全局样式
│   │       └── element-override.scss # Element Plus 样式覆盖
│   │
│   ├── components/                  # 公共组件
│   │   ├── common/                  # 通用组件
│   │   │   ├── AppHeader.vue        # 顶部导航栏
│   │   │   ├── AppFooter.vue        # 底部栏（移动端）
│   │   │   ├── LoadingSpinner.vue   # 加载动画
│   │   │   ├── EmptyState.vue       # 空状态展示
│   │   │   ├── BackToTop.vue        # 返回顶部
│   │   │   └── ImageLazyLoad.vue    # 图片懒加载
│   │   │
│   │   ├── note/                    # 笔记相关组件
│   │   │   ├── NoteCard.vue         # 笔记卡片（瀑布流）
│   │   │   ├── NoteList.vue         # 笔记列表容器
│   │   │   ├── NoteDetail.vue       # 笔记详情内容
│   │   │   ├── ImageUploader.vue    # 图片上传组件
│   │   │   ├── ImagePreview.vue     # 图片预览组件
│   │   │   ├── NoteActionBar.vue    # 点赞/收藏/分享操作栏
│   │   │   └── TopicTag.vue         # 话题标签组件
│   │   │
│   │   ├── comment/                 # 评论相关组件
│   │   │   ├── CommentItem.vue      # 单条评论（支持嵌套）
│   │   │   ├── CommentList.vue      # 评论列表
│   │   │   ├── CommentInput.vue     # 评论输入框
│   │   │   └── CommentReply.vue     # 回复组件
│   │   │
│   │   ├── search/                  # 搜索相关组件
│   │   │   ├── SearchBar.vue        # 搜索栏
│   │   │   ├── VoiceSearch.vue      # 语音搜索按钮
│   │   │   ├── SearchFilter.vue     # 搜索筛选
│   │   │   └── SearchHistory.vue    # 搜索历史
│   │   │
│   │   ├── user/                    # 用户相关组件
│   │   │   ├── UserCard.vue         # 用户信息卡片
│   │   │   ├── UserStats.vue        # 用户统计（笔记数/获赞数）
│   │   │   ├── AvatarUpload.vue     # 头像上传
│   │   │   └── FollowButton.vue     # 关注按钮（预留）
│   │   │
│   │   └── ai/                      # AI 功能组件
│   │       ├── AiSummary.vue        # AI 总结展示
│   │       └── AiLoading.vue        # AI 生成中动画
│   │
│   ├── composables/                 # 组合式函数（逻辑复用）
│   │   ├── useUser.js               # 用户状态与操作
│   │   ├── useNote.js               # 笔记操作逻辑
│   │   ├── useComment.js            # 评论操作逻辑
│   │   ├── useSearch.js             # 搜索逻辑
│   │   ├── useSpeech.js             # 语音识别
│   │   ├── useInfiniteScroll.js     # 无限滚动加载
│   │   ├── useImageUpload.js        # 图片上传逻辑
│   │   ├── useLikeCollect.js        # 点赞/收藏逻辑
│   │   └── useFormatTime.js         # 时间格式化
│   │
│   ├── router/                      # 路由配置
│   │   ├── index.js                 # 路由主配置
│   │   ├── routes.js                # 路由定义
│   │   └── guards.js                # 路由守卫（认证检查）
│   │
│   ├── stores/                      # Pinia 状态管理
│   │   ├── index.js                 # Store 入口
│   │   ├── userStore.js             # 用户状态
│   │   ├── noteStore.js             # 笔记状态
│   │   ├── searchStore.js           # 搜索状态
│   │   └── draftStore.js            # 草稿状态（本地存储）
│   │
│   ├── views/                       # 页面视图
│   │   ├── auth/                    # 认证页面
│   │   │   ├── LoginView.vue
│   │   │   └── RegisterView.vue
│   │   │
│   │   ├── home/                    # 首页
│   │   │   ├── HomeView.vue         # 首页（瀑布流笔记列表）
│   │   │   └── FollowingView.vue    # 关注页（预留）
│   │   │
│   │   ├── note/                    # 笔记页面
│   │   │   ├── NoteDetailView.vue   # 笔记详情页
│   │   │   ├── NotePublishView.vue  # 发布笔记页
│   │   │   └── NoteEditView.vue     # 编辑笔记页
│   │   │
│   │   ├── search/                  # 搜索页面
│   │   │   └── SearchResultView.vue # 搜索结果页
│   │   │
│   │   ├── topic/                   # 话题页面
│   │   │   └── TopicDetailView.vue  # 话题详情页
│   │   │
│   │   ├── user/                    # 用户中心
│   │   │   ├── ProfileView.vue      # 个人主页
│   │   │   ├── MyNotesView.vue      # 我的笔记
│   │   │   ├── MyLikesView.vue      # 我的点赞
│   │   │   ├── MyCollectsView.vue   # 我的收藏
│   │   │   └── SettingsView.vue     # 设置页
│   │   │
│   │   └── draft/                   # 草稿箱
│   │       └── DraftListView.vue    # 草稿列表
│   │
│   ├── utils/                       # 工具函数
│   │   ├── request.js               # Axios 封装
│   │   ├── storage.js               # localStorage 封装
│   │   ├── format.js                # 格式化工具（时间/数字）
│   │   ├── validate.js              # 表单验证
│   │   ├── constants.js             # 常量定义
│   │   └── debounce.js              # 防抖节流
│   │
│   ├── config/                      # 配置文件
│   │   ├── index.js                 # 全局配置
│   │   └── api.config.js            # API 配置
│   │
│   ├── App.vue                      # 根组件
│   └── main.js                      # 入口文件
│
├── index.html                       # HTML 模板
├── vite.config.js                   # Vite 配置
├── package.json
└── README.md
```

* * *

## 5. 核心模块设计


### 5.1 API 层 (`api/`)

统一封装 Axios 实例，处理请求拦截（添加 Token）、响应拦截（错误处理、Token 过期）。

**`api/index.js` 示例：**

```javascript
import axios from 'axios'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器：添加 Token
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
```

**模块 API 示例 (`api/user.js`)：**

```javascript
import request from './index'

export const userApi = {
  register: (data) => request.post('/users/register', data),
  login: (data) => request.post('/users/login', data),
  getProfile: () => request.get('/users/me'),
  updateProfile: (data) => request.put('/users/me', data),
  uploadAvatar: (formData) => request.post('/users/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
```

### 5.2 状态管理 (`stores/`)

使用 **Pinia** 进行状态管理，替代 Vuex，更轻量、TypeScript 支持更好。

**`stores/userStore.js` 示例：**

```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  
  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.nickname || '未登录')
  
  // Actions
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const fetchUserInfo = async () => {
    try {
      const data = await userApi.getProfile()
      userInfo.value = data
      return data
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    setToken,
    fetchUserInfo,
    logout
  }
})
```

### 5.3 组合式函数 (`composables/`)

封装可复用的业务逻辑，遵循 Vue 3 Composition API 最佳实践 。

**`composables/useSpeech.js` 语音搜索：**

```javascript
import { ref } from 'vue'

export function useSpeech() {
  const isListening = ref(false)
  const transcript = ref('')
  const error = ref(null)
  
  // 检查浏览器支持
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
  if (!SpeechRecognition) {
    error.value = '浏览器不支持语音识别'
    return { isListening, transcript, error, startListening: () => {} }
  }
  
  const recognition = new SpeechRecognition()
  recognition.lang = 'zh-CN'
  recognition.continuous = false
  recognition.interimResults = false
  
  const startListening = () => {
    isListening.value = true
    recognition.start()
  }
  
  recognition.onresult = (event) => {
    transcript.value = event.results[0][0].transcript
    isListening.value = false
  }
  
  recognition.onerror = (event) => {
    error.value = event.error
    isListening.value = false
  }
  
  recognition.onend = () => {
    isListening.value = false
  }
  
  return {
    isListening,
    transcript,
    error,
    startListening
  }
}
```

**`composables/useInfiniteScroll.js` 无限滚动：**

```javascript
import { ref, onMounted, onUnmounted } from 'vue'

export function useInfiniteScroll(callback, options = {}) {
  const { threshold = 100, immediate = true } = options
  const loading = ref(false)
  const finished = ref(false)
  const containerRef = ref(null)
  
  const handleScroll = async () => {
    if (loading.value || finished.value) return
    
    const container = containerRef.value
    if (!container) return
    
    const { scrollTop, scrollHeight, clientHeight } = container
    if (scrollHeight - scrollTop - clientHeight < threshold) {
      loading.value = true
      try {
        const hasMore = await callback()
        if (!hasMore) finished.value = true
      } finally {
        loading.value = false
      }
    }
  }
  
  onMounted(() => {
    if (immediate) handleScroll()
    containerRef.value?.addEventListener('scroll', handleScroll)
  })
  
  onUnmounted(() => {
    containerRef.value?.removeEventListener('scroll', handleScroll)
  })
  
  return {
    containerRef,
    loading,
    finished,
    reset: () => {
      finished.value = false
      loading.value = false
    }
  }
}
```

* * *

## 6. 路由设计


### 6.1 路由结构 (`router/routes.js`)

```javascript
export const routes = [
  // 首页
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/HomeView.vue'),
    meta: { title: '首页' }
  },
  
  // 认证
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录', guestOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { title: '注册', guestOnly: true }
  },
  
  // 笔记
  {
    path: '/note/:id',
    name: 'NoteDetail',
    component: () => import('@/views/note/NoteDetailView.vue'),
    meta: { title: '笔记详情' }
  },
  {
    path: '/publish',
    name: 'NotePublish',
    component: () => import('@/views/note/NotePublishView.vue'),
    meta: { title: '发布笔记', requiresAuth: true }
  },
  {
    path: '/note/:id/edit',
    name: 'NoteEdit',
    component: () => import('@/views/note/NoteEditView.vue'),
    meta: { title: '编辑笔记', requiresAuth: true }
  },
  
  // 搜索
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/search/SearchResultView.vue'),
    meta: { title: '搜索结果' }
  },
  
  // 话题
  {
    path: '/topic/:id',
    name: 'TopicDetail',
    component: () => import('@/views/topic/TopicDetailView.vue'),
    meta: { title: '话题详情' }
  },
  
  // 用户中心
  {
    path: '/user/:id',
    name: 'UserProfile',
    component: () => import('@/views/user/ProfileView.vue'),
    meta: { title: '个人主页' }
  },
  {
    path: '/me/likes',
    name: 'MyLikes',
    component: () => import('@/views/user/MyLikesView.vue'),
    meta: { title: '我的点赞', requiresAuth: true }
  },
  {
    path: '/me/collects',
    name: 'MyCollects',
    component: () => import('@/views/user/MyCollectsView.vue'),
    meta: { title: '我的收藏', requiresAuth: true }
  },
  {
    path: '/me/settings',
    name: 'Settings',
    component: () => import('@/views/user/SettingsView.vue'),
    meta: { title: '设置', requiresAuth: true }
  },
  
  // 草稿
  {
    path: '/drafts',
    name: 'DraftList',
    component: () => import('@/views/draft/DraftListView.vue'),
    meta: { title: '草稿箱', requiresAuth: true }
  },
  
  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在' }
  }
]
```

### 6.2 路由守卫 (`router/guards.js`)

```javascript
import { useUserStore } from '@/stores/userStore'

export function setupRouterGuards(router) {
  router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    
    // 设置页面标题
    document.title = to.meta.title ? `${to.meta.title} - Essencity` : 'Essencity'
    
    // 需要登录的页面
    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
    
    // 游客专属页面（登录后不能访问）
    if (to.meta.guestOnly && userStore.isLoggedIn) {
      next('/')
      return
    }
    
    next()
  })
}
```

* * *

## 7. 响应式布局设计


### 7.1 断点设计

基于 Element Plus 的响应式断点，适配 PC 和移动端 ：

| 断点名称 | 尺寸范围 | 设备类型 | 布局列数 |
|---------|---------|---------|---------|
| xs | < 768px | 手机 | 2 列 |
| sm | ≥ 768px | 平板 | 3 列 |
| md | ≥ 992px | 小桌面 | 4 列 |
| lg | ≥ 1200px | 大桌面 | 5 列 |
| xl | ≥ 1920px | 超大屏 | 6 列 |

### 7.2 布局适配策略

**PC 端布局：**
- 顶部固定导航栏（Logo、搜索框、用户菜单）
- 左侧边栏（导航菜单）+ 右侧内容区
- 笔记瀑布流展示（Pinterest 风格）
- 图片预览使用 Element Plus 的 `el-image-viewer`

**移动端布局：**
- 底部固定 Tab 导航（首页、发布、我的）
- 单列笔记列表
- 全屏图片预览（手势滑动）
- 底部弹出评论输入框

**响应式类名规范：**

```scss
// assets/styles/mixins.scss
@mixin responsive($breakpoint) {
  @if $breakpoint == xs {
    @media (max-width: 767px) { @content; }
  } @else if $breakpoint == sm {
    @media (min-width: 768px) and (max-width: 991px) { @content; }
  } @else if $breakpoint == md {
    @media (min-width: 992px) and (max-width: 1199px) { @content; }
  } @else if $breakpoint == lg {
    @media (min-width: 1200px) { @content; }
  }
}

// 使用示例
.note-card {
  width: 100%;
  
  @include responsive(xs) {
    width: 50%; // 手机两列
  }
  
  @include responsive(lg) {
    width: 20%; // 桌面五列
  }
}
```

* * *

## 8. 核心组件设计


### 8.1 笔记卡片组件 (`components/note/NoteCard.vue`)

```vue
<template>
  <div class="note-card" @click="goToDetail">
    <div class="image-wrapper">
      <el-image 
        :src="note.cover" 
        :preview-src-list="[note.cover]"
        fit="cover"
        lazy
      />
      <div v-if="note.imageCount > 1" class="multi-image-badge">
        <el-icon><Picture /></el-icon>
        {{ note.imageCount }}
      </div>
    </div>
    
    <div class="content">
      <h3 class="title">{{ note.title }}</h3>
      <div class="author">
        <el-avatar :size="24" :src="note.author.avatar" />
        <span class="nickname">{{ note.author.nickname }}</span>
      </div>
      <div class="stats">
        <span class="stat-item">
          <el-icon><View /></el-icon>
          {{ formatCount(note.viewCount) }}
        </span>
        <span class="stat-item">
          <el-icon><Star /></el-icon>
          {{ formatCount(note.likeCount) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Picture, View, Star } from '@element-plus/icons-vue'
import { formatCount } from '@/utils/format'

const props = defineProps({
  note: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const goToDetail = () => {
  router.push(`/note/${props.note.id}`)
}
</script>

<style scoped lang="scss">
.note-card {
  break-inside: avoid;
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  cursor: pointer;
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.12);
  }
  
  .image-wrapper {
    position: relative;
    width: 100%;
    aspect-ratio: 3/4;
    
    .multi-image-badge {
      position: absolute;
      top: 8px;
      right: 8px;
      background: rgba(0,0,0,0.5);
      color: #fff;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 12px;
    }
  }
  
  .content {
    padding: 12px;
    
    .title {
      font-size: 14px;
      line-height: 1.5;
      margin-bottom: 8px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .author {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      
      .nickname {
        font-size: 12px;
        color: #666;
      }
    }
    
    .stats {
      display: flex;
      gap: 12px;
      font-size: 12px;
      color: #999;
      
      .stat-item {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}
</style>
```

### 8.2 语音搜索组件 (`components/search/VoiceSearch.vue`)

```vue
<template>
  <div class="voice-search">
    <el-button 
      :type="isListening ? 'danger' : 'primary'"
      :icon="Microphone"
      circle
      @click="toggleListening"
      :loading="isListening"
    />
    <span v-if="isListening" class="listening-text">正在聆听...</span>
  </div>
</template>

<script setup>
import { watch } from 'vue'
import { Microphone } from '@element-plus/icons-vue'
import { useSpeech } from '@/composables/useSpeech'

const emit = defineEmits(['result'])

const { isListening, transcript, error, startListening } = useSpeech()

const toggleListening = () => {
  if (!isListening.value) {
    startListening()
  }
}

// 监听识别结果
watch(transcript, (newVal) => {
  if (newVal) {
    emit('result', newVal)
  }
})

// 监听错误
watch(error, (newVal) => {
  if (newVal) {
    ElMessage.error('语音识别失败，请重试')
  }
})
</script>
```

### 8.3 AI 总结组件 (`components/ai/AiSummary.vue`)

```vue
<template>
  <div class="ai-summary">
    <div class="header">
      <el-icon><Magic /></el-icon>
      <span>AI 智能总结</span>
      <el-button 
        v-if="!summary && !loading"
        type="primary" 
        link 
        @click="generateSummary"
      >
        生成总结
      </el-button>
    </div>
    
    <div v-if="loading" class="loading">
      <el-skeleton :rows="2" animated />
    </div>
    
    <div v-else-if="summary" class="content">
      <p>{{ summary }}</p>
      <span class="tag">AI 生成</span>
    </div>
    
    <div v-else class="empty">
      点击上方按钮生成笔记总结
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Magic } from '@element-plus/icons-vue'
import { aiApi } from '@/api/ai'

const props = defineProps({
  noteId: {
    type: Number,
    required: true
  }
})

const summary = ref('')
const loading = ref(false)

const generateSummary = async () => {
  loading.value = true
  try {
    const data = await aiApi.generateSummary(props.noteId)
    summary.value = data.summary
  } catch (error) {
    ElMessage.error('生成失败，请稍后再试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.ai-summary {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin: 16px 0;
  
  .header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    font-weight: 500;
    
    .el-icon {
      color: #409eff;
    }
  }
  
  .content {
    position: relative;
    background: #fff;
    padding: 12px;
    border-radius: 6px;
    font-size: 14px;
    line-height: 1.6;
    color: #333;
    
    .tag {
      position: absolute;
      top: 4px;
      right: 4px;
      font-size: 10px;
      color: #909399;
      background: #f4f4f5;
      padding: 2px 6px;
      border-radius: 4px;
    }
  }
  
  .empty {
    text-align: center;
    color: #909399;
    font-size: 14px;
    padding: 20px;
  }
}
</style>
```

* * *

## 9. 关键功能实现


### 9.1 图片上传与预览

**支持功能：**
- 多选上传（最多 9 张）
- 拖拽排序
- 压缩预览
- 上传进度显示

**实现要点：**
- 使用 `FormData` 批量上传
- 前端压缩（canvas）减少传输体积
- 上传前格式/大小校验

### 9.2 瀑布流布局

**实现方案：**
- CSS `column-count` 实现简单瀑布流
- 或使用 `vue-masonry` 插件
- 图片懒加载（`loading="lazy"` 或 Intersection Observer）

### 9.3 评论楼中楼

**数据结构：**
```javascript
// 一级评论
{
  id: 1,
  content: '一级评论',
  user: {...},
  replies: [
    // 二级评论
    { id: 2, content: '回复1', replyTo: '用户名', ... },
    { id: 3, content: '回复2', replyTo: '用户名', ... }
  ]
}
```

**交互设计：**
- 一级评论默认展开 3 条回复，点击"展开更多"
- 回复时 @ 被回复用户
- 支持删除自己的评论

* * *

## 10. 开发环境配置


### 10.1 Vite 配置 (`vite.config.js`) 

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `
          @use "@/assets/styles/variables.scss" as *;
          @use "@/assets/styles/mixins.scss" as *;
        `
      }
    }
  },
  
  server: {
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'vue-vendor': ['vue', 'vue-router', 'pinia']
        }
      }
    }
  }
})
```

### 10.2 环境变量

**.env.development：**
```
VITE_API_BASE_URL=/api
VITE_UPLOAD_URL=/uploads
```

**.env.production：**
```
VITE_API_BASE_URL=https://api.essencity.com
VITE_UPLOAD_URL=https://api.essencity.com/uploads
```

* * *

## 11. 性能优化策略


### 11.1 加载优化

- **路由懒加载**：`component: () => import('@/views/...')`
- **组件异步加载**：使用 `defineAsyncComponent`
- **图片优化**：
  - 懒加载（`loading="lazy"`）
  - 响应式图片（`srcset`）
  - WebP 格式优先
- **代码分割**：Vite 自动分包，手动配置 vendor 分包

### 11.2 运行时优化

- **虚拟滚动**：长列表使用 `vue-virtual-scroller`
- **防抖节流**：搜索输入、滚动加载
- **Keep-Alive**：缓存页面状态（首页、个人中心）

### 11.3 网络优化

- **请求缓存**：API 响应缓存（如话题列表）
- **CDN 加速**：静态资源上传 CDN
- **Gzip/Brotli**：开启服务器压缩

* * *

## 12. 开发规范

### 12.1 代码风格

- **ESLint + Prettier**：统一代码格式
- **Vue 风格指南**：优先使用 Composition API
- **命名规范**：
  - 组件名：PascalCase（`NoteCard.vue`）
  - 组合式函数：camelCase 前缀 `use`（`useUser.js`）
  - API 文件：小写（`user.js`）

### 12.2 Git 提交规范

与后端保持一致：

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具
```

### 12.3 组件开发规范

**文件结构顺序 ：**
1. `<script setup>` - 组合式 API 逻辑
2. `<template>` - 模板
3. `<style scoped>` - 样式

**Props 定义：**
```javascript
defineProps({
  note: {
    type: Object,
    required: true,
    validator(value) {
      return value.id && value.title
    }
  }
})
```

* * *

## 13. 与后端协作接口

### 13.1 API 对接清单

根据 `api.md` 文档，前端需实现以下模块：

| 模块 | 接口数量 | 核心功能 |
|------|---------|---------|
| 用户模块 | 6 | 登录/注册、资料管理、头像上传 |
| 笔记模块 | 8 | CRUD、点赞/收藏、图片上传 |
| 评论模块 | 4 | 发表/删除/列表、点赞 |
| 话题模块 | 5 | 列表、详情、笔记筛选 |
| 搜索模块 | 4 | 笔记/用户/话题搜索 |
| 草稿模块 | 4 | 保存/列表/详情/删除 |
| AI 模块 | 1 | 生成总结 |

### 13.2 数据格式约定

**统一响应处理：**
```javascript
// 成功
{ code: 200, message: 'success', data: {...} }

// 失败
{ code: 400, message: '参数错误', data: null }

// 分页
{ 
  code: 200, 
  message: 'success', 
  data: {
    list: [...],
    page: 1,
    size: 10,
    total: 100
  }
}
```

### 13.3 错误处理策略

| 错误码 | 前端处理 |
|-------|---------|
| 400 | 提示具体错误信息（表单验证） |
| 401 | 跳转登录页，清除本地 Token |
| 403 | 提示"权限不足" |
| 404 | 显示 404 页面 |
| 500 | 提示"服务器错误，请稍后再试" |

* * *

## 14. 部署方案


### 14.1 构建命令

```bash
# 开发环境
npm run dev

# 生产构建
npm run build

# 预览生产构建
npm run preview
```

### 14.2 部署配置

**Nginx 配置示例：**

```nginx
server {
    listen 80;
    server_name essencity.com;
    
    location / {
        root /var/www/essencity-frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;  # 支持 History 模式
    }
    
    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /uploads {
        proxy_pass http://localhost:8080/uploads;
    }
}
```

* * *

## 15. 开发里程碑（与后端对齐）


| 阶段 | 周次 | 前端任务 | 交付物 |
|------|------|---------|--------|
| 基础搭建 | W1-2 | 项目初始化、路由配置、Axios 封装、Element Plus 引入 | 可运行的基础框架 |
| 用户系统 | W3-4 | 登录/注册页面、个人资料页、头像上传组件 | 用户模块完整功能 |
| 笔记功能 | W5-7 | 笔记卡片组件、瀑布流布局、发布/编辑页、图片上传 | 笔记核心功能 |
| 互动系统 | W8-9 | 点赞/收藏按钮、评论组件（楼中楼）、评论输入框 | 互动功能完整 |
| 话题搜索 | W10-11 | 话题标签页、搜索页、语音搜索组件、搜索结果页 | 搜索功能完整 |
| AI 功能 | W12-13 | AI 总结组件、分享功能、草稿箱页面 | AI 功能集成 |
| 优化收尾 | W14-15 | 移动端适配、性能优化、Bug 修复、代码整理 | 可上线版本 |

* * *

## 16. 附录


### 16.1 推荐 VS Code 插件

- **Vue - Official** (Volar) - Vue 3 官方插件
- **ESLint** - 代码检查
- **Prettier** - 代码格式化
- **Auto Import** - 自动导入 API
- **SCSS IntelliSense** - SCSS 提示

### 16.2 参考文档

- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus 官方文档](https://element-plus.org/)
- [Pinia 文档](https://pinia.vuejs.org/)
- [Vite 官方文档](https://vitejs.dev/)

---

**文档版本历史**

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2026-03-11 | 初始版本 | Frontend Team |
```