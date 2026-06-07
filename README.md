# Essencity

[![CI](https://github.com/Essencity/Essencity/actions/workflows/ci.yml/badge.svg)](https://github.com/Essencity/Essencity/actions)
[![Backend Coverage](https://codecov.io/gh/Essencity/Essencity/branch/main/graph/badge.svg?token=7CZVSCDQ4K&flag=backend)](https://codecov.io/gh/Essencity/Essencity)
[![Frontend Coverage](https://codecov.io/gh/Essencity/Essencity/branch/main/graph/badge.svg?token=7CZVSCDQ4K&flag=frontend)](https://codecov.io/gh/Essencity/Essencity)

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.x-4FC08D?style=flat-square&logo=vue.js" alt="Vue">
  <img src="https://img.shields.io/badge/Vite-5.x-646CFF?style=flat-square&logo=vite&logoColor=white" alt="Vite">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=flat-square&logo=spring-boot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/JDK-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="JDK">
</p>

Essencity 是一个仿小红书的社交笔记分享平台。前端使用 Vue 3 + Vite 5 + Vant 4 样式资源，后端使用 Spring Boot 3.2 + JPA + MySQL 8.0，支持笔记发布、瀑布流浏览、点赞收藏、评论回复、关注、通知聚合、语音搜索和 DeepSeek AI 辅助能力。

## 链接

- 线上地址：[https://essencity.vercel.app/](https://essencity.vercel.app/)
- Figma 设计稿：[小红书界面设计](https://www.figma.com/make/i82rRWkpVxI0IEjWxtNzYG/%E5%B0%8F%E7%BA%A2%E4%B9%A6%E7%95%8C%E9%9D%A2%E8%AE%BE%E8%AE%A1?t=n28shp7ylUu9ZAnO-1)
- API 文档：[docs/api.md](docs/api.md) / [docs/api.yaml](docs/api.yaml)
- 项目报告：[docs/report.md](docs/report.md)

## 功能概览

| 模块    | 当前实现                                                        |
| ----- | ----------------------------------------------------------- |
| 用户系统  | 注册、登录、JWT 认证、个人资料编辑、头像上传、关注/取关、粉丝与关注列表                      |
| 笔记系统  | 图文/视频笔记发布、编辑、删除、详情查看、多图上传与轮播、分类标签                           |
| 内容浏览  | 搜索、分类筛选、瀑布流布局、桌面侧边栏、移动端底部导航                                 |
| 互动系统  | 点赞/取消点赞、收藏/取消收藏、评论、二级回复、评论删除                                |
| 通知系统  | 基于点赞、收藏、评论、回复、关注数据聚合通知，不使用独立 notifications 表                |
| AI 能力 | AI 总结、标签推荐、创作助手、帖子问答，后端统一接入 DeepSeek API                    |
| 语音搜索  | `useSpeech.js` 封装 Web Speech API，`VoiceSearch.vue` 提供语音搜索弹窗 |
| 响应式   | 桌面端多列瀑布流，移动端双列瀑布流和全屏详情体验                                    |

## 技术栈

### 前端

| 技术                       | 用途          |
| ------------------------ | ----------- |
| Vue 3                    | 单页应用和组件系统   |
| Vite 5                   | 开发服务器和生产构建  |
| Vant 4                   | 移动端样式资源     |
| Vitest + @vue/test-utils | 前端单元测试和组件测试 |
| ESLint 10                | 前端代码检查      |
| Web Speech API           | 浏览器语音识别     |

### 后端

| 技术                     | 用途             |
| ---------------------- | -------------- |
| Spring Boot 3.2        | REST API 服务    |
| Spring Data JPA        | ORM 和数据访问      |
| Spring Security        | 安全配置和认证链路      |
| JJWT                   | JWT 生成与解析      |
| BCrypt                 | 密码哈希           |
| MySQL 8.0 / TiDB Cloud | 本地与云端主数据存储     |
| H2                     | 后端测试数据库        |
| Actuator + Micrometer  | 健康检查和基础指标      |
| Logback JSON           | 结构化日志          |
| DeepSeek API           | AI 总结、推荐、创作和问答 |

### 工程与部署

| 技术                      | 用途                         |
| ----------------------- | -------------------------- |
| GitHub Actions          | 后端测试、前端测试、覆盖率上传            |
| Codecov                 | 覆盖率报告                      |
| gitleaks                | 敏感信息扫描                     |
| Docker / Docker Compose | 本地容器化运行                    |
| Vercel                  | 前端静态站点托管和 `/api/*` rewrite |
| Render                  | Spring Boot 后端云端部署         |
| TiDB Cloud Serverless   | MySQL 兼容云数据库               |

## 团队分工

| 成员  | 学号         | GitHub          | 主要角色                 | 主要贡献                                                                                                      |
| --- | ---------- | --------------- | -------------------- | --------------------------------------------------------------------------------------------------------- |
| 林忠阳 | 2212190528 | `lzy11123`      | 项目经理 / 后端开发 / AI 集成  | 后端分层架构、用户认证、JWT、安全配置、文件上传、Post/AI/Notification 服务、DeepSeek 接入、AI 总结持久化、Docker/Render/TiDB Cloud 部署配置、项目文档 |
| 林烨澄 | 2212190318 | `yclin30`       | 前端开发 / 测试 / 安全       | 前端页面与组件开发、瀑布流布局、发布页、多图上传、详情弹窗、个人主页、响应式适配、文件上传安全校验配合、Vitest 组件测试                                           |
| 陈熠恒 | 2312190613 | `chenyiheng111` | 前端开发 / CI/CD / UI 设计 | UI 交互设计、移动端体验、语音搜索前端实现、GitHub Actions、ESLint 配置、Codecov 覆盖率集成、gitleaks 安全扫描、前端测试覆盖率提升                     |

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
│   │   ├── application-render.properties
│   │   ├── application-secrets.properties.example
│   │   ├── schema.sql
│   │   └── logback-spring.xml
│   ├── Dockerfile
│   └── pom.xml
├── docs/
│   ├── api.md
│   ├── api.yaml
│   └── report.md
├── docker-compose.yml
└── README.md
```

## 本地运行

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0

### 1. 克隆项目

```bash
git clone https://github.com/Essencity/Essencity.git
cd Essencity
```

### 2. 创建数据库

```sql
CREATE DATABASE xiaohongshu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置后端敏感信息

复制模板文件：

```powershell
Copy-Item backend\src\main\resources\application-secrets.properties.example backend\src\main\resources\application-secrets.properties
```

填写本地 MySQL 密码、JWT 密钥和 DeepSeek API Key：

```properties
spring.datasource.password=你的 MySQL 密码
jwt.secret=至少 32 位的 JWT 密钥
deepseek.api-key=你的 DeepSeek API Key
```

DeepSeek API Key 可为空。为空时普通浏览和互动功能不受影响，但需要调用 DeepSeek 的 AI 接口会返回错误提示。

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080/api`。首次启动且用户表为空时，`DataInitializer` 会创建测试账号和测试笔记。

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`，Vite 会将 `/api` 和 `/uploads` 代理到后端。

## Docker 本地运行

Docker Compose 会启动后端和前端 Nginx 服务，数据库仍连接宿主机 MySQL。

```bash
docker compose up --build
```

- 前端：`http://localhost`
- 后端：`http://localhost:8080/api`
- 后端容器数据库地址：`host.docker.internal:3306/xiaohongshu`

## 云端部署

当前线上方案为 Vercel + Render + TiDB Cloud：

- Vercel 托管前端，并通过 `frontend/vercel.json` 将 `/api/*` 转发到 `https://essencity-backend.onrender.com/api/*`
- Render 托管 Spring Boot 后端，使用 `backend/src/main/resources/application-render.properties`
- TiDB Cloud Serverless 提供 MySQL 兼容数据库
- 线上入口：[https://essencity.vercel.app/](https://essencity.vercel.app/)

Render 需要配置的主要环境变量：

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://.../xiaohongshu?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
JWT_SECRET=...
DEEPSEEK_API_KEY=...
CORS_ALLOWED_ORIGINS=https://essencity.vercel.app
```

## 测试

### 前端

```bash
cd frontend
npm run test
npm run lint
```

当前前端包含 16 个 `*.spec.js` 测试文件，覆盖 API、组件和 `useSpeech` 组合式函数。

### 后端

```bash
cd backend
mvn test
```

当前后端包含 9 个测试文件，覆盖 Controller 和 Service 层，测试环境使用 H2 数据库。

## 测试账号

首次启动后会自动创建以下账号，密码均为 `123456`。

| 用户名        | 昵称       | 内容方向         |
| ---------- | -------- | ------------ |
| `xiaohong` | 小红爱生活    | 生活 / 美食 / 旅行 |
| `meishi`   | 美食家小王    | 美食           |
| `travel`   | 背包客小李    | 旅行           |
| `fashion`  | 时尚达人Lucy | 穿搭           |
| `fitness`  | 健身教练阿杰   | 健身           |

## API 入口

后端统一使用 `/api` context path，主要接口模块如下：

| 模块                   | 路径                      |
| -------------------- | ----------------------- |
| 用户认证与关注              | `/api/auth/**`          |
| 帖子、点赞、收藏、评论          | `/api/posts/**`         |
| AI 总结、标签推荐、创作助手、帖子问答 | `/api/ai/**`            |
| 文件上传                 | `/api/files/**`         |
| 通知聚合                 | `/api/notifications/**` |
| 健康检查                 | `/api/health`           |

完整接口说明见 [docs/api.md](docs/api.md) 和 [docs/api.yaml](docs/api.yaml)。
