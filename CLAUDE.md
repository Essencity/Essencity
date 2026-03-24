# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Essencity 是一个仿小红书的社交平台，支持图文笔记发布、互动交流、AI 智能总结、语音搜索等功能。

## 常用命令

### 前端
```bash
cd frontend
npm install
npm run dev      # 开发服务器 http://localhost:5173
npm run build    # 构建生产版本
npm run preview  # 预览生产版本
```

### 后端
```bash
cd backend
./mvnw spring-boot:run   # 运行后端服务
# 或使用 IDE 直接运行 XiaohongshuApplication.java
```
后端运行在 http://localhost:8080，Swagger 文档：http://localhost:8080/swagger-ui.html

## 技术栈

- **前端**: Vue 3 (Composition API) + Vite + Element Plus + Axios + Vue Router + Pinia
- **后端**: Spring Boot 3 + Spring Data JPA + MySQL 8.0 + JWT
- **AI**: 火山引擎豆包大模型

## 项目结构

```
Essencity/
├── frontend/                    # Vue 3 前端
│   └── src/
│       ├── api/                 # API 接口封装
│       ├── components/          # Vue 组件
│       ├── config/              # 配置文件
│       ├── router/              # 路由配置
│       ├── stores/              # Pinia 状态管理
│       ├── views/               # 页面视图
│       ├── App.vue              # 主组件
│       ├── main.js              # 入口文件
│       └── style.css            # 全局样式
│
├── backend/                     # Spring Boot 后端
│   └── src/main/java/com/xiaohongshu/
│       ├── config/              # 配置类 (CORS、JWT、文件上传等)
│       ├── controller/          # REST API 控制器
│       ├── dto/                 # 数据传输对象
│       ├── entity/              # 实体类
│       ├── repository/          # 数据访问层 (JPA)
│       ├── service/             # 业务逻辑层
│       ├── DataInitializer.java # 数据初始化（根包）
│       ├── DbConnectionTest.java# 数据库连接测试（根包）
│       └── XiaohongshuApplication.java
│   └── src/main/resources/
│       ├── application.properties
│       ├── schema.sql           # 数据库表结构
│       └── static/uploads/     # 上传的文件
│
└── docs/                        # 项目文档
```

## 数据库

- MySQL 8.0
- 数据库名: xiaohongshu
- 用户: root / 密码: 123456
- 核心表: users, posts, comments, likes, collections, follows, notifications

## 当前分支

- 主分支: main

## 开发注意事项

### 前端
- 通过 Vite 代理访问后端 API（配置在 vite.config.js）
- 使用 Vue Router 管理路由，Pinia 管理全局状态
- API 接口封装在 `src/api/` 目录

### 后端
- 使用 JWT 进行身份验证，Token 有效期见 `application.properties`
- 文件上传存储在 `backend/src/main/resources/static/uploads/`
- 跨域配置在 `config/CorsConfig.java`

### 安全规范
- JWT Secret 不得提交到代码仓库（使用环境变量或 config 文件）
- 文件上传需校验文件类型和大小（后端已在 config 中配置）
- 密码使用 BCrypt 加密存储
- SQL 注入防护：统一使用 JPA Repository，避免拼接 SQL

## AI 配置（火山引擎豆包大模型）

- API 配置项在 `backend/src/main/resources/application.properties`
- Key: `volcengine.api-key`
- Base URL: `volcengine.base-url`
- 模型: `volcengine.chat-model`
- 如需更换模型，修改对应配置项即可

## 环境变量

后端敏感配置（不提交到 Git）：
```bash
# application.properties 中注释的占位符
# volcengine.api-key=your_api_key_here
# jwt.secret=your_jwt_secret_here
```
