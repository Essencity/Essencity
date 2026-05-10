# Essencity 前端设计文档

> 项目：Essencity - 仿小红书社交平台
> 技术栈：Vue 3 + Vite 5 + Vant 4
> 文档版本：v2.0
> 日期：2026-05-10

---

## 1. 前端架构概述

Essencity 前端基于 **Vue 3 Composition API** 构建，采用 **Vite 5** 作为构建工具，**Vant 4** 作为移动端 UI 组件库。

前端核心职责：

- 用户界面渲染与交互体验
- 响应式布局（PC 端 + 移动端适配）
- API 请求封装与错误处理
- 图片上传与预览
- Web Speech API 语音搜索集成
- AI 总结功能展示

---

## 2. 技术栈详情

| 类别 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Vue.js | 3.4+ | Composition API |
| 构建工具 | Vite | 5.x | 快速冷启动、HMR |
| 移动端 UI | Vant | 4.x | TabBar、Popup 等 |
| HTTP 客户端 | Fetch API | - | 原生 API |
| 语音识别 | Web Speech API | - | 浏览器原生 API |
| 测试 | Vitest | 1.x | 单元测试框架 |

---

## 3. 移动端适配方案

### 3.1 断点设计

```css
/* 移动端 */
@media (max-width: 768px) { ... }

/* 桌面端 */
@media (min-width: 769px) { ... }
```

### 3.2 布局切换

| 组件 | 桌面端 | 移动端 |
|------|--------|--------|
| 导航 | 左侧边栏 (240px) | 底部 TabBar |
| 瀑布流 | 3-5 列 (固定 210px) | 2 列 (自适应) |
| 帖子详情 | 弹窗 (1000x680) | 全屏 |
| 头部 | 搜索栏 + 操作链接 | 简化搜索栏 |

### 3.3 关键组件

**MobileBottomBar.vue** - 移动端底部导航栏
- 使用 Vant 的 `van-tabbar` 组件
- 4 个 Tab：首页、发布、消息、我
- Safe Area 适配刘海屏

**MasonryGrid.vue** - 瀑布流布局
- 桌面端：JS 计算列数（2-5 列）
- 移动端：固定 2 列，宽度自适应

---

## 4. 项目目录结构

```
frontend/
├── src/
│   ├── api/                    # API 接口封装
│   │   ├── index.js            # 主要 API
│   │   └── ai.js               # AI 相关 API
│   ├── components/             # Vue 组件
│   │   ├── MobileBottomBar.vue # 移动端底部导航
│   │   ├── TheSidebar.vue      # 桌面端侧边栏
│   │   ├── TheHeader.vue       # 顶部搜索栏
│   │   ├── CategoryTabs.vue    # 分类标签
│   │   ├── MasonryGrid.vue     # 瀑布流布局
│   │   ├── PostCard.vue        # 帖子卡片
│   │   ├── PostDetailModal.vue # 帖子详情
│   │   ├── CreationPage.vue    # 发布页
│   │   ├── ProfilePage.vue     # 个人主页
│   │   ├── NotificationPage.vue# 通知页
│   │   ├── AuthModal.vue       # 登录弹窗
│   │   ├── AiSummary.vue       # AI 总结
│   │   └── VoiceSearch.vue     # 语音搜索
│   ├── composables/            # 组合式函数
│   │   └── useSpeech.js        # 语音识别
│   ├── App.vue                 # 主组件
│   ├── main.js                 # 入口文件
│   └── style.css               # 全局样式
├── index.html
├── vite.config.js
└── package.json
```

---

## 5. CSS 变量系统

```css
:root {
  /* 品牌色 */
  --primary-color: #ff2442;
  
  /* 文字颜色 */
  --text-primary: #333333;
  --text-secondary: #666666;
  
  /* 背景色 */
  --bg-color: #f8f8f8;
  --white: #ffffff;
  
  /* 布局尺寸 */
  --sidebar-width: 240px;
  --header-height: 72px;
  --mobile-tabbar-height: 50px;
  
  /* 圆角 */
  --radius-lg: 16px;
  --radius-md: 12px;
  --radius-full: 9999px;
  
  /* 阴影 */
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

/* 暗色模式 */
html.dark {
  --text-primary: #e6e6e6;
  --text-secondary: #999999;
  --bg-color: #121212;
  --white: #1f1f1f;
}
```

---

## 6. 组件设计

### 6.1 App.vue

主组件，负责：
- 视图切换（discovery/publish/notification/profile）
- 用户状态管理
- 帖子数据获取
- 移动端/桌面端布局切换

### 6.2 帖子卡片 (PostCard)

```css
/* 桌面端 */
.media-container {
  width: 210px;
  height: 280px;
}

/* 移动端 */
@media (max-width: 768px) {
  .media-container {
    width: 100%;
    aspect-ratio: 3 / 4;
  }
}
```

### 6.3 帖子详情 (PostDetailModal)

- 桌面端：左右布局（左图右文），1000x680px
- 移动端：上下布局（图在上，信息在下），全屏

---

## 7. Vant 4 集成

### 7.1 按需引入

通过 `unplugin-vue-components` 实现自动按需引入：

```js
// vite.config.js
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from '@vant/auto-import-resolver'

export default defineConfig({
  plugins: [
    vue(),
    Components({
      resolvers: [VantResolver()]
    })
  ]
})
```

### 7.2 主题定制

```css
:root {
  --van-primary-color: var(--primary-color);
  --van-tabbar-height: var(--mobile-tabbar-height);
  --van-tabbar-item-active-color: var(--primary-color);
}
```

---

## 8. 测试

```bash
npm run test          # 运行测试
npm run test:coverage # 运行测试并生成覆盖率报告
```

覆盖率要求：
- 组件：80%+
- 组合式函数：60%+

---

## 9. 构建与部署

```bash
npm run build    # 构建生产版本
npm run preview  # 预览生产版本
```

构建产物在 `dist/` 目录，可部署到任何静态文件服务器。
