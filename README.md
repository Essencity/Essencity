# Essencity

[![CI](https://github.com/Essencity/Essencity/actions/workflows/ci.yml/badge.svg)](https://github.com/Essencity/Essencity/actions)
[![Backend Coverage](https://codecov.io/gh/Essencity/Essencity/branch/main/graph/badge.svg?token=7CZVSCDQ4K&flag=backend)](https://codecov.io/gh/Essencity/Essencity)
[![Frontend Coverage](https://codecov.io/gh/Essencity/Essencity/branch/main/graph/badge.svg?token=7CZVSCDQ4K&flag=frontend)](https://codecov.io/gh/Essencity/Essencity)

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.x-4FC08D?style=flat-square&logo=vue.js" alt="Vue">
  <img src="https://img.shields.io/badge/Vant-4.x-1989FA?style=flat-square" alt="Vant">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=spring-boot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/JDK-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="JDK">
</p>

<p align="center">
  一个面向学生群体的仿小红书社交平台，支持图文笔记发布、互动交流、AI 智能总结、语音搜索等功能。<br>
  已适配移动端，支持 PC 端和手机端响应式布局。
</p>

---

## 功能特性

### 核心功能

- **用户系统** - 注册登录、个人资料管理
- **图文笔记** - 发布/编辑/删除笔记，支持图片和视频
- **互动交流** - 点赞、收藏、评论（支持二级回复）
- **搜索系统** - 笔记搜索
- **话题标签** - 分类标签系统

### AI 能力

- **AI 智能总结** - 基于 DeepSeek V4 Flash 大模型，一键生成笔记摘要
- **语音搜索** - 基于 Web Speech API 的语音识别搜索

### 移动端适配

- **响应式布局** - PC 端和移动端自适应
- **底部导航栏** - 移动端专属 TabBar
- **双列瀑布流** - 移动端 2 列自适应卡片
- **全屏详情页** - 移动端帖子详情全屏展示

---

## 技术栈

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3 | 前端框架，Composition API |
| Vite 5 | 构建工具 |
| Vant 4 | 移动端 UI 组件库 |
| Web Speech API | 语音识别 |

### 后端

| 技术 | 说明 |
|------|------|
| Spring Boot 3.2 | 后端框架 |
| Spring Data JPA | ORM 框架 |
| Spring Security | 安全框架 |
| MySQL 8.0 | 数据库 |
| JWT | 身份认证 |
| BCrypt | 密码加密 |
| DeepSeek API | AI 总结能力 |

---

## 项目结构

```
Essencity/
├── frontend/                    # Vue 3 前端
│   └── src/
│       ├── api/                 # API 接口封装
│       ├── components/          # Vue 组件
│       │   ├── MobileBottomBar.vue  # 移动端底部导航
│       │   ├── TheSidebar.vue       # 桌面端侧边栏
│       │   ├── MasonryGrid.vue      # 瀑布流布局
│       │   ├── PostCard.vue         # 帖子卡片
│       │   ├── PostDetailModal.vue  # 帖子详情弹窗
│       │   └── ...
│       ├── composables/         # 组合式函数
│       ├── App.vue              # 主组件
│       └── main.js              # 入口文件
│
├── backend/                     # Spring Boot 后端
│   └── src/main/java/com/xiaohongshu/
│       ├── config/              # 配置类
│       ├── controller/          # 控制器
│       ├── service/             # 服务层
│       ├── repository/          # 数据访问层
│       ├── entity/              # 实体类
│       └── DataInitializer.java # 测试数据初始化
│
└── docs/                        # 项目文档
```

---

## 快速开始

### 环境要求

- **JDK** 17+
- **Node.js** 18+
- **MySQL** 8.0

### 1. 克隆项目

```bash
git clone https://github.com/yclin30/Essencity.git
cd Essencity
```

### 2. 数据库配置

```sql
CREATE DATABASE xiaohongshu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置后端

创建 `backend/src/main/resources/application-secrets.properties`：

```properties
spring.datasource.password=你的MySQL密码
deepseek.api-key=你的DeepSeek API Key
jwt.secret=你的JWT密钥（至少64字符）
```

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动，首次启动会自动创建表结构并插入测试数据。

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端将在 http://localhost:5173 启动。

---

## 测试账号

首次启动后，系统会自动创建以下测试账号（密码均为 `123456`）：

| 用户名 | 昵称 | 内容方向 |
|--------|------|----------|
| xiaohong | 小红爱生活 | 生活/家居 |
| meishi | 美食家小王 | 美食 |
| travel | 背包客小李 | 旅行 |
| fashion | 时尚达人Lucy | 穿搭 |
| fitness | 健身教练阿杰 | 健身 |

---

## API 文档

启动后端服务后，访问 Swagger 文档：

```
http://localhost:8080/api/swagger-ui.html
```

---

## 移动端预览

在浏览器中按 F12 打开开发者工具，切换到手机模式（375px 宽度）即可预览移动端效果。

---

## 开源协议

本项目采用 MIT License 开源协议。
