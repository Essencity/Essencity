# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Essencity 是一个仿小红书的社交平台，支持图文笔记发布、互动交流、AI 智能总结、语音搜索等功能。已适配移动端，支持响应式布局。

## 常用命令

### 前端
```bash
cd frontend
npm install
npm run dev      # 开发服务器 http://localhost:5173
npm run build    # 构建生产版本
npm run test     # 运行测试
npm run lint     # 代码检查
```

### 后端
```bash
cd backend
mvn spring-boot:run   # 运行后端服务
# 或使用 IDE 直接运行 XiaohongshuApplication.java
```
后端运行在 http://localhost:8080，Swagger 文档：http://localhost:8080/api/swagger-ui.html

## 技术栈

- **前端**: Vue 3 (Composition API) + Vite 5 + Vant 4 (移动端) + Web Speech API
- **后端**: Spring Boot 3.2 + Spring Data JPA + MySQL 8.0 + JWT + BCrypt
- **AI**: MiniMax 大模型

## 项目结构

```
Essencity/
├── frontend/                    # Vue 3 前端
│   └── src/
│       ├── api/                 # API 接口封装
│       ├── components/          # Vue 组件
│       ├── composables/         # 组合式函数
│       ├── App.vue              # 主组件
│       ├── main.js              # 入口文件
│       └── style.css            # 全局样式
│
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/com/xiaohongshu/
│   │   ├── config/              # 配置类 (CORS、JWT、Security)
│   │   ├── controller/          # REST API 控制器
│   │   ├── dto/                 # 数据传输对象
│   │   ├── entity/              # 实体类
│   │   ├── repository/          # 数据访问层 (JPA)
│   │   ├── service/             # 业务逻辑层
│   │   └── DataInitializer.java # 测试数据初始化
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-secrets.properties  # 敏感配置（不提交）
│   │   └── schema.sql           # 数据库表结构
│   └── uploads/                 # 上传的文件（运行时生成）
│
└── docs/                        # 项目文档
```

## 数据库

- MySQL 8.0
- 数据库名: xiaohongshu
- 用户: root / 密码: lyc
- 核心表: users, posts, comments, likes, collections, follows

## 移动端适配

项目使用 Vant 4 实现移动端适配：
- 断点: 768px
- 移动端: 底部 TabBar 导航，双列瀑布流，帖子详情全屏
- 桌面端: 左侧边栏导航，多列瀑布流，帖子详情弹窗
- 关键组件: `MobileBottomBar.vue`

## 测试数据

DataInitializer 会在数据库为空时自动插入测试数据：
- 5 个测试用户（密码都是 `123456`）
- 15 个测试帖子（美食/穿搭/旅行/健身/生活）
- 评论、点赞、收藏、关注等互动数据
- 测试图片存储在 `backend/uploads/test/`

## 开发注意事项

### 前端
- 通过 Vite 代理访问后端 API（配置在 vite.config.js）
- Vant 4 组件按需引入（通过 unplugin-vue-components）
- API 接口封装在 `src/api/` 目录

### 后端
- 使用 JWT 进行身份验证，Token 有效期 24 小时
- 文件上传存储在 `backend/uploads/`（非 static 目录）
- 敏感配置在 `application-secrets.properties`

### 安全规范
- JWT Secret 不得提交到代码仓库
- 文件上传需校验文件类型和大小
- 密码使用 BCrypt 加密存储
- SQL 注入防护：统一使用 JPA Repository

## AI 配置（MiniMax）

- API 配置项在 `application-secrets.properties`
- Base URL: `https://api.minimax.chat`
- 模型: `MiniMax-M2.1`
