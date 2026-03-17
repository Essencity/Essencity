# 🌸 Essencity

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.x-4FC08D?style=flat-square&logo=vue.js" alt="Vue">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=spring-boot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/JDK-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="JDK">
  <img src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" alt="License">
</p>

<p align="center">
  一个面向学生群体的仿小红书社交平台，支持图文笔记发布、互动交流、AI 智能总结、语音搜索等功能。
</p>

---

## ✨ 功能特性

### 核心功能

- 🔐 **用户系统** - 注册登录、个人资料管理、密码修改
- 📝 **图文笔记** - 发布/编辑/删除笔记，支持最多 9 张图片
- 💬 **互动交流** - 点赞、收藏、评论（支持楼中楼二级回复）
- 🔍 **搜索系统** - 笔记/用户/话题搜索
- 🏷️ **话题标签** - 预设话题 + 自由创建，话题详情页
- 📄 **草稿箱** - 笔记草稿保存与管理

### AI 能力

- 🤖 **AI 智能总结** - 基于豆包大模型，一键生成笔记摘要
- 🎤 **语音搜索** - 基于 Web Speech API 的语音识别搜索

### 其他特性

- 📱 **响应式设计** - 同时适配 PC 端和移动端
- 👁️ **浏览量统计** - 笔记浏览量实时统计
- 🔗 **笔记分享** - 生成分享链接
- 🖼️ **图片预览** - 点击图片放大查看

---

## Figma 链接



https://www.figma.com/make/gGyRINQwNbDa6o9D0Xvkvk/Prototype-with-Voice-Recognition?t=BEqKjdr9VD98IIo7-20&fullscreen=1

---

## 团队分工

| 姓名  | 学号         | 分工  |
| --- | ---------- | --- |
| 陈熠恒 | 2312190613 | 前端  |
| 林忠阳 | 2212190528 | 后端  |
| 林烨澄 | 2312190630 | 前端  |

---



## 🛠️ 技术栈

### 前端

| 技术             | 说明                   |
| -------------- | -------------------- |
| Vue 3          | 前端框架，Composition API |
| Vite           | 构建工具                 |
| Element Plus   | UI 组件库               |
| Vue Router     | 路由管理，History 模式      |
| Axios          | HTTP 请求              |
| Web Speech API | 语音识别                 |

### 后端

| 技术              | 说明     |
| --------------- | ------ |
| Spring Boot 3   | 后端框架   |
| Spring Data JPA | ORM 框架 |
| MySQL 8.0       | 数据库    |
| JWT             | 身份认证   |
| BCrypt          | 密码加密   |
| Swagger         | API 文档 |

### AI 能力

| 技术        | 说明      |
| --------- | ------- |
| 火山引擎 - 豆包 | AI 总结能力 |

---

## 📁 项目结构

```
Essencity/
├── essencity-frontend/          # 前端项目
│   ├── src/
│   │   ├── api/                 # API 接口封装
│   │   ├── assets/              # 静态资源
│   │   ├── components/          # 公共组件
│   │   ├── composables/         # 组合式函数
│   │   ├── router/              # 路由配置
│   │   ├── views/               # 页面视图
│   │   └── utils/               # 工具函数
│   ├── package.json
│   └── vite.config.js
│
├── essencity-backend/           # 后端项目
│   ├── src/main/java/com/xiaohongshu/
│   │   ├── config/              # 配置类
│   │   ├── controller/          # 控制器
│   │   ├── service/             # 服务层
│   │   ├── repository/          # 数据访问层
│   │   ├── entity/              # 实体类
│   │   ├── dto/                 # 数据传输对象
│   │   ├── common/              # 公共模块
│   │   ├── exception/           # 异常处理
│   │   ├── security/            # 安全模块
│   │   └── util/                # 工具类
│   ├── src/main/resources/
│   │   └── application.yml      # 配置文件
│   └── pom.xml
│
├── docs/                        # 项目文档
│   └── spec.md                  # 技术规格说明书
│
└── README.md
```

---

## 🚀 快速开始

### 环境要求

- **JDK** 17+
- **Node.js** 22.x
- **MySQL** 8.0
- **npm** 或 **pnpm**

### 1. 克隆项目

```bash
git clone https://github.com/yclin30/Essencity.git
cd Essencity
```

### 2. 数据库配置

```sql
-- 创建数据库
CREATE DATABASE essencity DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 启动后端

```bash
cd essencity-backend

# 修改配置文件
# src/main/resources/application-dev.yml
# 配置数据库连接、火山引擎 API Key 等

# 运行项目
./mvnw spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 4. 启动前端

```bash
cd essencity-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

---

## ⚙️ 配置说明

### 后端配置 (application-dev.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/essencity?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

# JWT 配置
jwt:
  secret: your-secret-key-at-least-256-bits
  expiration: 86400000  # 24小时

# 文件上传配置
upload:
  path: ./uploads
  max-size: 5242880  # 5MB

# 火山引擎配置
volcengine:
  api-key: your-api-key
  endpoint: https://ark.cn-beijing.volces.com/api/v3
  model: doubao-pro-4k
```

### 前端配置 (vite.config.js)

```javascript
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

## 📚 API 文档

启动后端服务后，访问 Swagger 文档：

```
http://localhost:8080/swagger-ui.html
```

### 主要接口模块

| 模块  | 路径前缀           | 说明          |
| --- | -------------- | ----------- |
| 用户  | `/api/user`    | 注册、登录、个人资料  |
| 笔记  | `/api/note`    | 发布、编辑、点赞、收藏 |
| 评论  | `/api/comment` | 评论、回复、点赞    |
| 话题  | `/api/topic`   | 话题管理        |
| 搜索  | `/api/search`  | 笔记/用户/话题搜索  |
| 草稿  | `/api/draft`   | 草稿管理        |
| AI  | `/api/ai`      | AI 总结       |

---

## 🗃️ 数据库设计

### ER 关系图

```
User ──┬── Note ──┬── NoteImage
       │          ├── NoteTopic ── Topic
       │          ├── NoteLike
       │          ├── NoteCollect
       │          └── Comment ── CommentLike
       │
       └── Draft
```

### 核心数据表

| 表名           | 说明      |
| ------------ | ------- |
| user         | 用户表     |
| note         | 笔记表     |
| note_image   | 笔记图片表   |
| topic        | 话题表     |
| note_topic   | 笔记话题关联表 |
| note_like    | 笔记点赞表   |
| note_collect | 笔记收藏表   |
| comment      | 评论表     |
| comment_like | 评论点赞表   |
| draft        | 草稿表     |

---

## 🔀 Git 分支策略

```
main                    # 稳定版本
└── develop             # 开发主分支
    ├── feature/xxx     # 功能分支
    └── ...
```

### Commit 规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具
```

---

## 📅 开发计划

| 阶段    | 周次     | 内容           |
| ----- | ------ | ------------ |
| 项目初始化 | W1-2   | 环境搭建、基础框架    |
| 用户系统  | W3-4   | 注册登录、个人资料    |
| 笔记功能  | W5-7   | 发布、编辑、草稿     |
| 互动系统  | W8-9   | 点赞、收藏、评论     |
| 话题与搜索 | W10-11 | 话题系统、搜索、语音搜索 |
| AI 功能 | W12-13 | AI 总结、分享功能   |
| 优化收尾  | W14-15 | 响应式适配、性能优化   |

---

## 📄 文档

- [技术规格说明书 (spec.md)](./docs/spec.md)

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📜 开源协议

本项目采用 [MIT License](./LICENSE) 开源协议。

---

## 👨‍💻 作者

**yclin30**

- GitHub: [@yclin30](https://github.com/yclin30)

---

<p align="center">
  如果这个项目对你有帮助，欢迎 ⭐ Star 支持！
</p>
