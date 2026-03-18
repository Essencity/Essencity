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

- **前端**: Vue 3 (Composition API) + Vite + Element Plus + Axios + Vue Router
- **后端**: Spring Boot 3 + Spring Data JPA + MySQL 8.0 + JWT
- **AI**: 火山引擎豆包大模型

## 项目结构

```
Essencity/
├── frontend/                    # Vue 3 前端
│   ├── src/
│   │   ├── components/          # Vue 组件
│   │   ├── config/              # 配置文件
│   │   ├── App.vue              # 主组件
│   │   ├── main.js              # 入口文件
│   │   └── style.css            # 全局样式
│   └── package.json
│
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/com/xiaohongshu/
│   │   ├── config/              # 配置类
│   │   ├── controller/          # REST API 控制器
│   │   ├── service/            # 业务逻辑层
│   │   ├── repository/         # 数据访问层 (JPA)
│   │   ├── entity/              # 实体类
│   │   └── dto/                 # 数据传输对象
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── schema.sql           # 数据库表结构
│   │   └── static/uploads/      # 上传的文件
│   └── pom.xml
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

- 前端通过 Vite 代理访问后端 API (配置在 vite.config.js)
- 后端使用 JWT 进行身份验证
- 文件上传存储在 backend/src/main/resources/static/uploads/
