# Essencity 开发待办

> 最后更新：2026-03-17

## 项目概述
- 仿小红书社交平台
- 技术栈：Vue 3 + Spring Boot 3 + MySQL 8.0
- 前端：http://localhost:5173
- 后端：http://localhost:8080

---

## AI 功能（待开发）

### 前提
- [ ] 申请火山引擎豆包 API Key
  - 访问 https://www.volcengine.com/docs/82379
  - 创建账号 → 获取 API Key

### 后端
- [ ] 添加 ai_summary 字段到 Post 实体
- [ ] 创建 AiController (`/api/ai/summary/{noteId}`)
- [ ] 创建 AiService（封装豆包 API 调用）
- [ ] 实现缓存逻辑（检查 ai_summary 是否已有值）

### 前端
- [ ] 创建 `src/api/ai.js` API 模块
- [ ] 创建 `src/components/AiSummary.vue` 组件
- [ ] 修改 `PostDetailModal.vue` 集成 AI 总结
- [ ] 创建 `src/components/AiLoading.vue` 动画组件

### 语音搜索
- [ ] 创建 `src/components/VoiceSearch.vue` 页面（设计稿 voice.png）
- [ ] 创建 `src/composables/useSpeech.js`（Web Speech API 封装）
- [ ] 集成到搜索功能

---

## 其他待开发功能

### 高优先级
- [ ] 话题标签系统（发布页 + 详情页）
- [ ] 多图笔记（最多9张）
- [ ] 楼中楼评论 UI

### 中优先级
- [ ] 个人主页 Tab 切换（笔记/收藏/点赞）
- [ ] 通知系统完善（评论通知）
- [ ] 关注/粉丝系统完善
- [ ] 评论点赞
- [ ] 评论排序（按时间/热度）

### 低优先级
- [ ] 笔记分享功能
- [ ] 草稿箱功能
- [ ] 修改密码
- [ ] 响应式适配移动端

---

## 已完成功能 ✅

### 后端
- 用户注册/登录
- 个人资料编辑
- 关注/粉丝系统
- 帖子发布（视频/图片）
- 帖子列表/详情
- 帖子删除
- 图片/视频上传
- 点赞/收藏
- 评论（支持二级）
- 评论删除
- 通知系统

### 前端
- 发现页（搜索 + 分类标签 + 瀑布流）
- 创作页（视频/图片上传）
- 个人主页
- 通知页
- 登录/注册弹窗
- 完善资料弹窗
- 帖子详情弹窗

---

## 数据库

- 数据库名：xiaohongshu
- 用户：root
- 密码：123456
- 表：users, posts, comments, likes, collections, follows, notifications

---

## 笔记

- AI 功能使用火山引擎豆包大模型
- 语音搜索使用 Web Speech API
- 暂时不做 AI 总结，等 API Key 申请完成后再开发
