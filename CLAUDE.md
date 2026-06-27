# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Essencity 是一个仿小红书的社交笔记分享平台，支持笔记发布、瀑布流浏览、点赞收藏、评论回复、关注、通知聚合、语音搜索和 DeepSeek AI 辅助能力。已适配移动端，支持响应式布局。

- 线上地址：https://essencity.vercel.app/
- API 文档：docs/api.md / docs/api.yaml
- 项目报告：docs/report.md

## 功能概览

| 模块   | 当前实现                                                       |
| ------ | -------------------------------------------------------------- |
| 用户系统 | 注册、登录、JWT 认证、个人资料编辑、头像上传、关注/取关、粉丝与关注列表 |
| 笔记系统 | 图文/视频笔记发布、编辑、删除、详情查看、多图上传与轮播、分类标签       |
| 内容浏览 | 搜索、分类筛选、瀑布流布局、桌面侧边栏、移动端底部导航                |
| 互动系统 | 点赞/取消点赞、收藏/取消收藏、评论、二级回复、评论删除                |
| 通知系统 | 基于点赞、收藏、评论、回复、关注数据聚合通知，不使用独立 notifications 表 |
| AI 能力 | AI 总结、标签推荐、创作助手、帖子问答，后端统一接入 DeepSeek API      |
| 语音搜索 | useSpeech.js 封装 Web Speech API，VoiceSearch.vue 提供语音搜索弹窗  |
| 响应式   | 桌面端多列瀑布流，移动端双列瀑布流和全屏详情体验                     |

## 常用命令

### 前端
```bash
cd frontend
npm install
npm run dev      # 开发服务器 http://localhost:5173
npm run build    # 构建生产版本
npm run test     # 运行测试（当前 16 个 spec 文件，215 项测试）
npm run lint     # 代码检查（ESLint 10，--max-warnings 0）
```

### 后端
```bash
cd backend
mvn spring-boot:run   # 运行后端服务 http://localhost:8080/api
mvn test              # 运行测试（当前 9 个测试文件，154 项测试，H2 数据库）
# 或使用 IDE 直接运行 XiaohongshuApplication.java
```

### Docker
```bash
docker compose up --build   # 启动后端 + Nginx 前端（数据库连接宿主机 MySQL）
```

## 技术栈

### 前端
- Vue 3 (Composition API) + Vite 5 + Vant 4（移动端样式资源）
- Vitest + @vue/test-utils（单元测试）
- ESLint 10（代码检查）
- Web Speech API（语音识别）

### 后端
- Spring Boot 3.2 + Spring Data JPA + Spring Security
- MySQL 8.0（本地）/ TiDB Cloud Serverless（云端）
- H2（测试数据库）
- JJWT + BCrypt
- Actuator + Micrometer（健康检查/指标）
- Logback JSON（结构化日志）
- DeepSeek API（AI 总结、推荐、创作、问答）

### 工程与部署
- GitHub Actions（CI：后端测试 + 前端测试 + Codecov 覆盖率上传）
- Codecov（覆盖率报告）
- gitleaks（敏感信息扫描）
- Docker / Docker Compose（本地容器化）
- Vercel（前端托管 + `/api/*` rewrite）
- Render（Spring Boot 后端云端部署）
- TiDB Cloud Serverless（MySQL 兼容云数据库）

## 项目结构

```text
Essencity/
├── frontend/
│   ├── src/
│   │   ├── api/                 # 主 API 封装和 AI API 封装
│   │   ├── components/          # 18 个 Vue 组件
│   │   ├── composables/         # useSpeech.js
│   │   ├── App.vue              # currentView 单页视图切换
│   │   ├── main.js
│   │   └── style.css
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── vercel.json
│   └── package.json
├── backend/
│   ├── src/main/java/com/xiaohongshu/
│   │   ├── config/              # SecurityConfig, JwtUtil, WebConfig, MetricsFilter
│   │   ├── controller/          # Auth, Post, AI, File, Notification, Health
│   │   ├── service/             # User, Post, AI, Notification
│   │   ├── repository/          # User, Post, Comment, Like, Collection, Follow
│   │   ├── entity/              # User, Post, Comment, Like, Collection, Follow
│   │   ├── dto/
│   │   ├── util/
│   │   └── DataInitializer.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-render.properties       # Render 部署配置
│   │   ├── application-secrets.properties      # 敏感配置（不提交）
│   │   ├── application-secrets.properties.example
│   │   ├── schema.sql
│   │   └── logback-spring.xml
│   ├── Dockerfile
│   └── pom.xml
├── docs/
│   ├── api.md
│   ├── api.yaml
│   └── report.md
└── docker-compose.yml
```

## API 入口

后端统一使用 `/api` context path，主要接口模块：

| 模块                   | 路径                      |
| ---------------------- | ------------------------- |
| 用户认证与关注           | `/api/auth/**`            |
| 帖子、点赞、收藏、评论     | `/api/posts/**`           |
| AI 总结、标签推荐、创作助手、帖子问答 | `/api/ai/**`    |
| 文件上传                | `/api/files/**`           |
| 通知聚合                | `/api/notifications/**`   |
| 健康检查                | `/api/health`             |

## 数据库

- MySQL 8.0，数据库名: xiaohongshu
- 本地用户: root / 密码: lyc
- 核心表: users, posts, comments, likes, collections, follows

## 测试数据

DataInitializer 会在数据库为空时自动插入测试数据：
- 5 个测试用户（密码都是 `123456`）
- 15 个测试帖子（美食/穿搭/旅行/健身/生活）
- 评论、点赞、收藏、关注等互动数据
- 测试图片存储在 `backend/uploads/test/`

| 用户名      | 昵称        | 内容方向             |
| ----------- | ----------- | -------------------- |
| `xiaohong`  | 小红爱生活   | 生活 / 美食 / 旅行   |
| `meishi`    | 美食家小王   | 美食                 |
| `travel`    | 背包客小李   | 旅行                 |
| `fashion`   | 时尚达人Lucy | 穿搭                 |
| `fitness`   | 健身教练阿杰  | 健身                 |

## 移动端适配

- 断点: 768px
- 移动端: 底部 TabBar 导航，双列瀑布流，帖子详情全屏
- 桌面端: 左侧边栏导航，多列瀑布流，帖子详情弹窗
- 关键组件: `MobileBottomBar.vue`

## 云端部署

- Vercel 托管前端，`vercel.json` 将 `/api/*` 转发到 Render 后端
- Render 托管 Spring Boot 后端，使用 `application-render.properties`
- TiDB Cloud Serverless 提供 MySQL 兼容云数据库

## 开发注意事项

### 前端
- 通过 Vite 代理访问后端 API（配置在 vite.config.js）
- Vant 4 组件按需引入（通过 unplugin-vue-components）
- API 接口封装在 `src/api/` 目录

### 后端
- 使用 JWT 进行身份验证，Token 有效期 24 小时
- 文件上传存储在 `backend/uploads/`（非 static 目录）
- 敏感配置在 `application-secrets.properties`（不提交）
- 本地运行时需创建 `application-secrets.properties`，填写 MySQL 密码、JWT 密钥、DeepSeek API Key（可为空）

### 安全规范
- JWT Secret 不得提交到代码仓库
- 文件上传需校验文件类型和大小
- 密码使用 BCrypt 加密存储
- SQL 注入防护：统一使用 JPA Repository

## AI 配置（DeepSeek）

- API 配置项在 `application-secrets.properties`（`deepseek.api-key`）
- DeepSeek API Key 可为空，为空时普通浏览和互动功能不受影响
