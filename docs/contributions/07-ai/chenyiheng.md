# 陈熠恒 - AI 功能前端集成贡献说明

## 基本信息

- **姓名**: 陈熠恒
- **学号**: 2312190613
- **日期**: 2026-04-20

---

## 贡献内容概述

本次贡献主要完成 AI 功能的前端集成工作，包括 AI Summary 组件、语音搜索功能及相关优化。

| 模块 | 文件 | 说明 |
|------|------|------|
| AI Summary | `src/components/AiSummary.vue` | AI 总结弹窗组件 |
| AI 加载 | `src/components/AiLoading.vue` | AI 加载动画组件 |
| AI API | `src/api/ai.js` | AI 相关 API 封装 |
| 语音搜索 | `src/composables/useSpeech.js` | Web Speech API 封装 |
| 语音组件 | `src/components/VoiceSearch.vue` | 语音搜索弹窗组件 |

---

## 1. AI Summary 功能

### 组件说明

**AiSummary.vue** - AI 总结弹窗组件

用户点击帖子详情页右下角的 AI 按钮后，弹出此组件，显示 AI 生成的帖子内容摘要。

**功能特点**：
- 三种状态：loading（加载中）、error（错误）、hasSummary（已有总结）
- 智能获取/生成流程：先获取已有总结，失败则自动生成
- 错误重试机制
- 响应式模态框设计

**AiLoading.vue** - AI 加载动画组件

- 三种尺寸：small、medium、large
- 三个点弹性跳动动画
- 可配置加载提示文字

### 组件逻辑

```javascript
// 工作流程
1. 点击"获取总结"按钮
2. 先调用 GET /api/ai/summary/{postId} 获取已有总结
3. 如果获取成功（返回 ai_summary），直接显示
4. 如果获取失败（404 或无数据），自动调用 POST /api/ai/summary 生成新总结
5. 生成成功后显示总结内容
```

### API 模块

```javascript
// ai.js
export async function getAiSummary(postId) {
  return fetch(`/api/ai/summary/${postId}`).then(res => res.json())
}

export async function generateAiSummary(postId, title, content) {
  return fetch('/api/ai/summary', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ postId, title, content })
  }).then(res => res.json())
}
```

---

## 2. 语音搜索功能

### 功能说明

允许用户通过麦克风输入搜索内容，替代键盘打字，提升搜索体验。

### API 封装

**useSpeech.js** - Web Speech API 组合式函数

```javascript
export function useSpeech() {
  // 状态
  const isListening = ref(false)
  const isSupported = ref(false)
  const transcript = ref('')
  const error = ref(null)

  // 方法
  const checkSupport = () => { /* 检测浏览器兼容性 */ }
  const startListening = () => { /* 开始录音 */ }
  const stopListening = () => { /* 停止录音 */ }
  const resetTranscript = () => { /* 重置转录内容 */ }
}
```

**功能特点**：
- 浏览器兼容性检测（Chrome、Edge、Safari 支持，Firefox 不支持）
- 实时语音转文字
- 多种错误处理（无语音、麦克风不可用、权限拒绝、网络错误）
- 自动清理机制

### 组件实现

**VoiceSearch.vue** - 语音搜索弹窗

- 弹窗打开时立即检测浏览器兼容性
- 实时转录文字显示
- 动态波形动画效果
- 搜索和清除功能

---

## 3. 问题修复

### AI Summary 组件逻辑修复

**问题**：组件只调用 GET 接口获取已有总结，不支持生成新总结

**解决**：修改 fetchSummary 方法，获取失败时自动触发生成流程

### 语音识别浏览器兼容性检测修复

**问题**：所有浏览器都显示"浏览器不支持"

**原因**：`isSupported` 初始值为 `false`，只有调用 startListening() 时才检测

**解决**：新增 checkSupport() 方法，弹窗打开时立即调用检测

### 语音识别重复使用问题

**问题**：第一次点击后，第二次点击无反应

**原因**：recognition 实例用过一次后无法重复启动

**解决**：
1. 添加 `if (isListening.value) return` 防止重复启动
2. start() 失败时将 recognition = null，下次重新初始化

---

## 技术要点

### 前端交互设计

1. **状态管理** - 使用 Vue 3 ref/reactive 管理组件状态
2. **错误处理** - 分层捕获错误，提供友好提示
3. **用户体验** - 加载动画、操作反馈、错误重试

### API 调用流程

```
前端组件 → ai.js API 模块 → 后端 AIController → AIService → MiniMax API
```

### 浏览器兼容性

| 浏览器 | 语音搜索支持 |
|--------|-------------|
| Chrome | 完全支持 |
| Edge | 完全支持 |
| Safari | 部分支持 |
| Firefox | 不支持 |

---

## 相关文件清单

| 文件路径 | 说明 |
|----------|------|
| `frontend/src/api/ai.js` | AI API 封装 |
| `frontend/src/components/AiSummary.vue` | AI 总结弹窗组件 |
| `frontend/src/components/AiLoading.vue` | AI 加载动画组件 |
| `frontend/src/components/VoiceSearch.vue` | 语音搜索弹窗组件 |
| `frontend/src/composables/useSpeech.js` | Web Speech API Hook |
