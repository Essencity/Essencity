# Essencity - 仿小红书项目技术规格说明书

> **版本**: v1.0  
> **日期**: 2026-03-11  
> **项目周期**: 15 周  
> **技术栈**: Vue 3 + Spring Boot 3 + MySQL 8.0  

---

## 1. 项目概述

### 1.1 项目简介
Essencity 是一个面向学生群体的仿小红书社交平台，支持图文笔记发布、互动交流、内容搜索等核心功能，并集成 AI 智能总结和语音搜索等创新特性。

### 1.2 项目目标
- 完成一个功能完整的全栈项目，涵盖前后端开发全流程
- 掌握 Vue 3 + Spring Boot 3 技术栈的实战应用
- 实践 AI 能力接入（豆包大模型）和 Web Speech API

### 1.3 目标用户
- 在校学生
- 学习/练手项目，本地开发为主

---

## 2. 功能需求清单

### 2.1 MVP 核心功能

| 模块 | 功能点 | 优先级 | 说明 |
|------|--------|--------|------|
| **用户系统** | 用户注册 | P0 | 用户名 + 密码，BCrypt 加密 |
| | 用户登录 | P0 | JWT Token 认证 |
| | 修改密码 | P1 | 需验证原密码 |
| | 个人资料编辑 | P0 | 头像、昵称、简介、性别、学校 |
| | 个人主页 | P0 | 展示用户信息、发布的笔记 |
| | 我的收藏 | P0 | 收藏的笔记列表 |
| | 我的点赞 | P0 | 点赞的笔记列表 |
| **笔记系统** | 图文笔记发布 | P0 | 最多 9 张图片，最多 1000 字 |
| | 笔记编辑 | P0 | 支持发布后修改 |
| | 草稿保存 | P0 | 本地/服务端草稿 |
| | 笔记删除 | P0 | 软删除 |
| | 笔记详情 | P0 | 图文展示、浏览量统计 |
| | 图片预览 | P1 | 点击图片放大查看 |
| | 话题标签 | P0 | #话题# 支持预设 + 自由创建 |
| **互动系统** | 点赞笔记 | P0 | 点赞/取消点赞 |
| | 收藏笔记 | P0 | 收藏/取消收藏 |
| | 评论笔记 | P0 | 支持二级评论（楼中楼） |
| | 评论点赞 | P0 | 点赞/取消点赞 |
| | 评论删除 | P0 | 用户删自己的，作者删笔记下的 |
| | 评论排序 | P0 | 按时间 / 按热度切换 |
| **搜索系统** | 笔记搜索 | P0 | 搜索标题 + 内容 |
| | 用户搜索 | P0 | 搜索用户昵称 |
| | 话题搜索 | P0 | 搜索话题标签 |
| | 语音搜索 | P1 | Web Speech API 语音转文字 |
| **AI 功能** | AI 总结 | P0 | 用户点击触发，100 字摘要 |
| | 总结缓存 | P0 | 避免重复调用 API |
| **其他** | 笔记分享 | P1 | 生成分享链接 |

### 2.2 后期迭代功能

| 模块 | 功能点 | 说明 |
|------|--------|------|
| 用户系统 | 关注/粉丝系统 | 关注用户、粉丝列表 |
| | 忘记密码/找回密码 | 邮箱验证找回 |
| 笔记系统 | 视频笔记发布 | 支持短视频上传 |
| 推荐系统 | 首页信息流推荐 | 基于兴趣的个性化推荐 |
| 消息系统 | 私信功能 | 用户间私信 |
| | 消息通知 | 点赞、评论、关注通知 |

---

## 3. 技术栈详情

### 3.1 前端技术栈

| 类别 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| 框架 | Vue.js | 3.x | Composition API |
| 构建工具 | Vite | 5.x | 快速开发构建 |
| UI 组件库 | Element Plus | 2.x | 响应式布局适配移动端 |
| 路由 | Vue Router | 4.x | History 模式 |
| HTTP 客户端 | Axios | 1.x | 请求拦截、响应拦截 |
| 语音识别 | Web Speech API | - | 浏览器原生 API |
| 开发语言 | TypeScript | 5.x | 可选，建议使用 |
| 包管理器 | npm | - | Node.js 22.22.0 |
| IDE | VS Code | - | - |

### 3.2 后端技术栈

| 类别 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| 框架 | Spring Boot | 3.x | JDK 17 |
| ORM | Spring Data JPA | - | 数据持久层 |
| 数据库 | MySQL | 8.0 | 主数据存储 |
| 认证 | JWT | - | 无状态 Token 认证 |
| 加密 | BCrypt | - | 密码加密 |
| 文档 | Swagger/Knife4j | - | API 文档自动生成 |
| 日志 | Logback | - | Spring Boot 默认 |
| IDE | IntelliJ IDEA | - | - |

### 3.3 AI 能力

| 类别 | 技术选型 | 说明 |
|------|----------|------|
| 大模型 | 火山引擎 - 豆包 (Doubao) | AI 总结功能 |
| 语音识别 | Web Speech API | 浏览器端语音转文字 |

### 3.4 开发环境

| 环境 | 版本/配置 |
|------|----------|
| JDK | 17 |
| Node.js | 22.22.0 |
| MySQL | 8.0 |
| 包管理器 | npm |

---

## 4. 系统架构

### 4.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         客户端 (Browser)                         │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Vue 3 + Element Plus                  │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌───────────────┐  │   │
│  │  │ 用户模块 │ │ 笔记模块 │ │ 搜索模块 │ │ Web Speech API│  │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └───────────────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │ HTTP/REST
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Boot 3 后端服务                        │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                     Controller 层                        │   │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌───���────┐ ┌───────┐  │   │
│  │  │ User   │ │ Note   │ │Comment │ │ Search │ │  AI   │  │   │
│  │  └────────┘ └────────┘ └────────┘ └────────┘ └───────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                      Service 层                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Repository 层 (JPA)                   │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
         │                                        │
         ▼                                        ▼
┌─────────────────┐                    ┌─────────────────────┐
│    MySQL 8.0    │                    │   火山引擎 豆包 API  │
│   数据持久化     │                    │     AI 总结能力     │
└─────────────────┘                    └─────────────────────┘
         │
         ▼
┌─────────────────┐
│   本地文件系统   │
│   图片存储       │
└─────────────────┘
```

### 4.2 前端架构

```
┌────────────────────────────────────────────────────┐
│                    Vue 3 Application               │
├────────────────────────────────────────────────────┤
│  Views (页面)                                       │
│  ├── 登录/注册页                                    │
│  ├── 首页 (笔记列表)                                │
│  ├── 笔记详情页                                     │
│  ├── 发布/编辑笔记页                                │
│  ├── 搜索结果页                                     │
│  ├── 个人主页                                       │
│  ├── 话题详情页                                     │
│  └── 设置页                                        │
├────────────────────────────────────────────────────┤
│  Components (组件)                                  │
│  ├── 公共组件 (Header, Footer, Loading...)         │
│  ├── 笔记卡片组件                                   │
│  ├── 评论组件 (支持二级)                            │
│  ├── 图片上传组件                                   │
│  ├── 语音搜索组件                                   │
│  └── AI 总结组件                                    │
├────────────────────────────────────────────────────┤
│  Composables (组合式函数)                           │
│  ├── useUser (用户状态)                             │
│  ├── useNote (笔记操作)                             │
│  ├── useSearch (搜索逻辑)                           │
│  └── useSpeech (语音识别)                           │
├────────────────────────────────────────────────────┤
│  API (接口封装)                                     │
│  └── Axios 实例 + 拦截器                            │
└─────────────────���──────────────────────────────────┘
```

---

## 5. 数据库设计

### 5.1 ER 关系图

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    User     │       │    Note     │       │   Comment   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id (PK)     │◄──┐   │ id (PK)     │◄──┐   │ id (PK)     │
│ username    │   │   │ user_id(FK) │───┘   │ note_id(FK) │───┐
│ password    │   │   │ title       │       │ user_id(FK) │───┤
│ avatar      │   │   │ content     │       │ parent_id   │   │
│ nickname    │   │   │ status      │       │ root_id     │   │
│ bio         │   │   │ view_count  │       │ content     │   │
│ gender      │   │   │ like_count  │       │ like_count  │   │
│ school      │   │   │ collect_cnt │       │ created_at  │   │
│ created_at  │   │   │ comment_cnt │       └─────────────┘   │
│ updated_at  │   │   │ ai_summary  │                         │
└─────────────┘   │   │ created_at  │       ┌─────────────┐   │
                  │   │ updated_at  │       │ CommentLike │   │
┌─────────────┐   │   └─────────────┘       ├─────────────┤   │
│  NoteLike   │   │          │              │ id (PK)     │   │
├─────────────┤   │          │              │ comment_id  │───┘
│ id (PK)     │   │          ▼              │ user_id(FK) │
│ user_id(FK) │───┤   ┌─────────────┐       └─────────────┘
│ note_id(FK) │   │   │  NoteImage  │
└─────────────┘   │   ├─────────────┤       ┌─────────────┐
                  │   │ id (PK)     │       │    Topic    │
┌─────────────┐   │   │ note_id(FK) │       ├─────────────┤
│ NoteCollect │   │   │ image_url   │       │ id (PK)     │
├─────────────┤   │   │ sort_order  │       │ name        │
│ id (PK)     │   │   └─────────────┘       │ is_preset   │
│ user_id(FK) │───┤                         │ note_count  │
│ note_id(FK) │   │   ┌─────────────┐       │ created_at  │
└─────────────┘   │   │  NoteTopic  │       └─────────────┘
                  │   ├─────────────┤              ▲
                  │   │ note_id(FK) │──────────────┤
                  │   │ topic_id(FK)│──────────────┘
                  │   └─────────────┘
                  │
                  │   ┌─────────────┐
                  │   │    Draft    │
                  │   ├─────────────┤
                  │   │ id (PK)     │
                  └───│ user_id(FK) │
                      │ title       │
                      │ content     │
                      │ images_json │
                      │ topics_json │
                      │ updated_at  │
                      └─────────────┘
```

### 5.2 数据表详细设计

#### 5.2.1 用户表 (user)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 用户 ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码（BCrypt） |
| avatar | VARCHAR(255) | | 头像 URL |
| nickname | VARCHAR(50) | NOT NULL | 昵称 |
| bio | VARCHAR(200) | | 个人简介 |
| gender | TINYINT | | 性别：0未知/1男/2女 |
| school | VARCHAR(100) | | 学校 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

#### 5.2.2 笔记表 (note)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 笔记 ID |
| user_id | BIGINT | FK, NOT NULL | 作者 ID |
| title | VARCHAR(100) | NOT NULL | 标题 |
| content | TEXT | | 内容（最多 1000 字） |
| status | TINYINT | NOT NULL | 状态：0草稿/1已发布/2已删除 |
| view_count | INT | DEFAULT 0 | 浏览量 |
| like_count | INT | DEFAULT 0 | 点赞数 |
| collect_count | INT | DEFAULT 0 | 收藏数 |
| comment_count | INT | DEFAULT 0 | 评论数 |
| ai_summary | VARCHAR(500) | | AI 总结缓存 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

#### 5.2.3 笔记图片表 (note_image)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 图片 ID |
| note_id | BIGINT | FK, NOT NULL | 笔记 ID |
| image_url | VARCHAR(255) | NOT NULL | 图片 URL |
| sort_order | INT | NOT NULL | 排序序号 |

#### 5.2.4 话题表 (topic)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 话题 ID |
| name | VARCHAR(50) | UNIQUE, NOT NULL | 话题名称 |
| is_preset | TINYINT | DEFAULT 0 | 是否预设：0否/1是 |
| note_count | INT | DEFAULT 0 | 关联笔记数 |
| created_at | DATETIME | NOT NULL | 创建时间 |

#### 5.2.5 笔记话题关联表 (note_topic)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | ID |
| note_id | BIGINT | FK, NOT NULL | 笔记 ID |
| topic_id | BIGINT | FK, NOT NULL | 话题 ID |

#### 5.2.6 点赞表 (note_like)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | ID |
| user_id | BIGINT | FK, NOT NULL | 用户 ID |
| note_id | BIGINT | FK, NOT NULL | 笔记 ID |
| created_at | DATETIME | NOT NULL | 点赞时间 |

> 唯一索引：(user_id, note_id)

#### 5.2.7 收藏表 (note_collect)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | ID |
| user_id | BIGINT | FK, NOT NULL | 用户 ID |
| note_id | BIGINT | FK, NOT NULL | 笔记 ID |
| created_at | DATETIME | NOT NULL | 收藏时间 |

> 唯一索引：(user_id, note_id)

#### 5.2.8 评论表 (comment)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 评论 ID |
| note_id | BIGINT | FK, NOT NULL | 笔记 ID |
| user_id | BIGINT | FK, NOT NULL | 评论者 ID |
| parent_id | BIGINT | | 父评论 ID（一级为 NULL） |
| root_id | BIGINT | | 根评论 ID（一级为自己） |
| reply_to_user_id | BIGINT | | 被回复的用户 ID |
| content | VARCHAR(200) | NOT NULL | 评论内容 |
| like_count | INT | DEFAULT 0 | 点赞数 |
| is_deleted | TINYINT | DEFAULT 0 | 是否删除 |
| created_at | DATETIME | NOT NULL | 创建时间 |

#### 5.2.9 评论点赞表 (comment_like)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | ID |
| user_id | BIGINT | FK, NOT NULL | 用户 ID |
| comment_id | BIGINT | FK, NOT NULL | 评论 ID |
| created_at | DATETIME | NOT NULL | 点赞时间 |

> 唯一索引：(user_id, comment_id)

#### 5.2.10 草稿表 (draft)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 草稿 ID |
| user_id | BIGINT | FK, NOT NULL | 用户 ID |
| title | VARCHAR(100) | | 标题 |
| content | TEXT | | 内容 |
| images_json | TEXT | | 图片列表 JSON |
| topics_json | VARCHAR(500) | | 话题列表 JSON |
| updated_at | DATETIME | NOT NULL | 更新时间 |

---

## 6. API 接口设计

### 6.1 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

**错误码规范：**

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录/Token 失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 6.2 接口清单

#### 6.2.1 用户模块 `/api/user`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /register | 用户注册 | 否 |
| POST | /login | 用户登录 | 否 |
| GET | /profile | 获取当前用户信息 | 是 |
| PUT | /profile | 更新用户资料 | 是 |
| PUT | /password | 修改密码 | 是 |
| POST | /avatar | 上传头像 | 是 |
| GET | /{userId} | 获取用户主页信息 | 否 |
| GET | /{userId}/notes | 获取用户发布的笔记 | 否 |
| GET | /my/likes | 我的点赞列表 | 是 |
| GET | /my/collects | 我的收藏列表 | 是 |

#### 6.2.2 笔记模块 `/api/note`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | / | 发布笔记 | 是 |
| GET | /{noteId} | 获取笔记详情 | 否 |
| PUT | /{noteId} | 更新笔记 | 是 |
| DELETE | /{noteId} | 删除笔记（软删除） | 是 |
| GET | /list | 获取笔记列表（首页） | 否 |
| POST | /image/upload | 上传笔记图片 | 是 |
| POST | /{noteId}/like | 点赞/取消点赞 | 是 |
| POST | /{noteId}/collect | 收藏/取消收藏 | 是 |
| GET | /{noteId}/share | 获取分享链接 | 否 |

#### 6.2.3 评论模块 `/api/comment`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | / | 发表评论 | 是 |
| DELETE | /{commentId} | 删除评论 | 是 |
| GET | /note/{noteId} | 获取笔记评论列表 | 否 |
| POST | /{commentId}/like | 点赞/取消点赞 | 是 |

#### 6.2.4 话题模块 `/api/topic`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /list | 获取话题列表（含预设） | 否 |
| GET | /hot | 获取热门话题 | 否 |
| GET | /{topicId} | 话题详情 | 否 |
| GET | /{topicId}/notes | 话题下的笔记列表 | 否 |
| POST | / | 创建话题 | 是 |

#### 6.2.5 搜索模块 `/api/search`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /note | 搜索笔记 | 否 |
| GET | /user | 搜索用户 | 否 |
| GET | /topic | 搜索话题 | 否 |
| GET | /all | 综合搜索 | 否 |

#### 6.2.6 草稿模块 `/api/draft`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | / | 保存草稿 | 是 |
| GET | /list | 获取草稿列表 | 是 |
| GET | /{draftId} | 获取草稿详情 | 是 |
| DELETE | /{draftId} | 删除草稿 | 是 |

#### 6.2.7 AI 模块 `/api/ai`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /summary/{noteId} | 生成 AI 总结 | 是 |

---

## 7. AI 功能设计

### 7.1 AI 总结功能

**调用流程：**

```
用户点击「AI 总结」按钮
        │
        ▼
  检查是否有缓存 ──────────────────┐
        │                        │
        │ 无缓存                  │ 有缓存
        ▼                        ▼
  调用豆包 API ───────────► 返回缓存结果
        │
        ▼
  保存到 ai_summary 字段
        │
        ▼
  返回总结结果
```

### 7.2 豆包 API 调用示例

```java
// Prompt 模板
String prompt = """
    请用简洁的语言总结以下笔记内容，要求：
    1. 总结控制在 100 字以内
    2. 提取核心观点和关键信息
    3. 语言通顺、易于理解
    
    笔记标题：{title}
    笔记内容：{content}
    """;
```

### 7.3 错误处理

| 场景 | 处理方式 |
|------|----------|
| API 调用超时 | 返回「总结失败，请稍后再试」 |
| API 返回错误 | 返回「总结失败，请稍后再试」 |
| 内容过短 | 提示「内容太短，无需总结」 |

### 7.4 语音搜索功能

**前端实现（Web Speech API）：**

```javascript
// 语音搜索 Composable
export function useSpeech() {
  const isListening = ref(false)
  const transcript = ref('')
  
  const recognition = new webkitSpeechRecognition()
  recognition.lang = 'zh-CN'
  recognition.continuous = false
  
  const startListening = () => {
    isListening.value = true
    recognition.start()
  }
  
  recognition.onresult = (event) => {
    transcript.value = event.results[0][0].transcript
    // 触发搜索
  }
  
  return { isListening, transcript, startListening }
}
```

---

## 8. 项目目录结构

### 8.1 后端目录结构

```
essencity-backend/
├── src/
│   ├── main/
│   │   ├── java/com/xiaohongshu/
│   │   │   ├── EssencityApplication.java        # 启动类
│   │   │   ├── config/                          # 配置类
│   │   │   │   ├── SecurityConfig.java          # 安全配置
│   │   │   │   ├── WebConfig.java               # Web 配置
│   │   │   │   ├── SwaggerConfig.java           # Swagger 配置
│   │   │   │   └── JwtConfig.java               # JWT 配置
│   │   │   ├── controller/                      # 控制器层
│   │   │   │   ├── UserController.java
│   │   │   │   ├── NoteController.java
│   │   │   │   ├── CommentController.java
│   │   │   │   ├── TopicController.java
│   │   │   │   ├── SearchController.java
│   │   │   │   ├── DraftController.java
│   │   │   │   └── AiController.java
│   │   │   ├── service/                         # 服务层
│   │   │   │   ├── UserService.java
│   │   │   │   ├── NoteService.java
│   │   │   │   ├── CommentService.java
│   │   │   │   ├── TopicService.java
│   │   │   │   ├── SearchService.java
│   │   │   │   ├── DraftService.java
│   │   │   │   ├── FileService.java
│   │   │   │   └── AiService.java
│   │   │   ├── repository/                      # 数据访问层
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── NoteRepository.java
│   │   │   │   ├── CommentRepository.java
│   │   │   │   └── ...
│   │   │   ├── entity/                          # 实体类
│   │   │   │   ├── User.java
│   │   │   │   ├── Note.java
│   │   │   │   ├── Comment.java
│   │   │   │   └── ...
│   │   │   ├── dto/                             # 数据传输对象
│   │   │   │   ├── request/                     # 请求 DTO
│   │   │   │   └── response/                    # 响应 DTO
│   │   │   ├── common/                          # 公共模块
│   │   │   │   ├── Result.java                  # 统一响应
│   │   │   │   ├── ResultCode.java              # 响应码枚举
│   │   │   │   └── PageResult.java              # 分页响应
│   │   │   ├── exception/                       # 异常处理
│   │   │   │   ├── BusinessException.java       # 业务异常
│   │   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   ├── security/                        # 安全模块
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   └── util/                            # 工具类
│   │   │       ├── FileUtil.java
│   │   │       └── StringUtil.java
│   │   └── resources/
│   │       ├── application.yml                  # 主配置文件
│   │       ├── application-dev.yml              # 开发环境配置
│   │       └── logback-spring.xml               # 日志配置
│   └── test/                                    # 测试目录
├── uploads/                                     # 上传文件目录
├── pom.xml                                      # Maven 配置
└── README.md
```

### 8.2 前端目录结构

```
essencity-frontend/
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                          # API 接口
│   │   ├── index.js                  # Axios 实例
│   │   ├── user.js
│   │   ├── note.js
│   │   ├── comment.js
│   │   ├── topic.js
│   │   ├── search.js
│   │   └── ai.js
│   ├── assets/                       # 静态资源
│   │   ├── images/
│   │   └── styles/
│   │       ├── variables.scss        # 变量定义
│   │       ├── mixins.scss           # 混入
│   │       └── global.scss           # 全局样式
│   ├── components/                   # 公共组件
│   │   ├── common/
│   │   │   ├── AppHeader.vue
│   │   │   ├── AppFooter.vue
│   │   │   ├── LoadingSpinner.vue
│   │   │   └── EmptyState.vue
│   │   ├── note/
│   │   │   ├── NoteCard.vue          # 笔记卡片
│   │   │   ├── NoteList.vue          # 笔记列表
│   │   │   └── ImageUploader.vue     # 图片上传
│   │   ├── comment/
│   │   │   ├── CommentItem.vue       # 评论项
│   │   │   ├── CommentList.vue       # 评论列表
│   │   │   └── CommentInput.vue      # 评论输入
│   │   ├── search/
│   │   │   ├── SearchBar.vue         # 搜索栏
│   │   │   └── VoiceSearch.vue       # 语音搜索
│   │   └── ai/
│   │       └── AiSummary.vue         # AI 总结组件
│   ├── composables/                  # 组合式函数
│   │   ├── useUser.js                # 用户状态管理
│   │   ├── useNote.js                # 笔记操作
│   │   ├── useComment.js             # 评论操作
│   │   ├── useSearch.js              # 搜索逻辑
│   │   ├── useSpeech.js              # 语音识别
│   │   └── useInfiniteScroll.js      # 无限滚动
│   ├── router/                       # 路由配置
│   │   └── index.js
│   ├── views/                        # 页面视图
│   │   ├── auth/
│   │   │   ├── LoginView.vue
│   │   │   └── RegisterView.vue
│   │   ├── home/
│   │   │   └── HomeView.vue
│   │   ├── note/
│   │   │   ├── NoteDetailView.vue
│   │   │   ├── NoteEditView.vue
│   │   │   └── NotePublishView.vue
│   │   ├── search/
│   │   │   └── SearchResultView.vue
│   │   ├── topic/
│   │   │   └── TopicDetailView.vue
│   │   ├── user/
│   │   │   ├── ProfileView.vue
│   │   │   ├── MyLikesView.vue
│   │   │   ├── MyCollectsView.vue
│   │   │   └── SettingsView.vue
│   │   └── draft/
│   │       └── DraftListView.vue
│   ├── utils/                        # 工具函数
│   │   ├── request.js                # 请求封装
│   │   ├── storage.js                # 本地存储
│   │   ├── format.js                 # 格式化工具
│   │   └── validate.js               # 验证工具
│   ├── App.vue                       # 根组件
│   └── main.js                       # 入口文件
├── index.html
├── vite.config.js                    # Vite 配置
├── package.json
└── README.md
```

---

## 9. 开发里程碑（15 周排期）

### 阶段一：项目初始化与基础搭建（第 1-2 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W1 | 环境搭建、项目初始化 | 前后端项目骨架 |
| W1 | 数据库设计、建表 | SQL 脚本 |
| W1 | Git 仓库初始化 | 仓库 + 分支策略 |
| W2 | 后端基础配置（统一响应、异常处理、Swagger） | 基础框架 |
| W2 | 前端基础配置（路由、Axios 封装、全局样式） | 基础框架 |

### 阶段二：用户系统（第 3-4 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W3 | 用户注册、登录接口 | API + 测试 |
| W3 | JWT 认证实现 | 安全模块 |
| W3 | 登录、注册页面 | 前端页面 |
| W4 | 用户资料编辑、头像上传 | API + 页面 |
| W4 | 修改密码功能 | API + 页面 |
| W4 | 个人主页（基础版） | 页面 |

### 阶段三：笔记核心功能（第 5-7 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W5 | 图片上传接口 | 文件服务 |
| W5 | 笔记发布接口 | API |
| W5 | 笔记发布页面（图片上传组件） | 页面 |
| W6 | 笔记列表、详情接口 | API |
| W6 | 首页笔记列表、详情页 | 页面 |
| W6 | 图片预览功能 | 组件 |
| W7 | 笔记编辑、删除 | API + 页面 |
| W7 | 草稿功能 | API + 页面 |
| W7 | 浏览量统计 | 功能 |

### 阶段四：互动系统（第 8-9 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W8 | 点赞、收藏接口 | API |
| W8 | 点赞、收藏前端交互 | 组件 |
| W8 | 我的点赞、我的收藏页面 | 页面 |
| W9 | 评论接口（支持二级） | API |
| W9 | 评论组件（楼中楼） | 组件 |
| W9 | 评论点赞、删除 | 功能 |
| W9 | 评论排序切换 | 功能 |

### 阶段五：话题与搜索（第 10-11 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W10 | 话题系统接口 | API |
| W10 | 话题选择组件、话题详情页 | 页面 |
| W10 | 笔记关联话题 | 功能 |
| W11 | 搜索接口（笔记、用户、话题） | API |
| W11 | 搜索页面 | 页面 |
| W11 | 语音搜索功能 | 组件 |

### 阶段六：AI 功能（第 12-13 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W12 | 火山引擎 API 接入 | 服务 |
| W12 | AI 总结接口 | API |
| W12 | AI 总结前端组件 | 组件 |
| W13 | 总结缓存机制 | 优化 |
| W13 | 错误处理完善 | 优化 |
| W13 | 笔记分享功能 | 功能 |

### 阶段七：优化与收尾（第 14-15 周）

| 周次 | 任务 | 交付物 |
|------|------|--------|
| W14 | 响应式适配优化 | 移动端体验 |
| W14 | 性能优化（图片懒加载、无限滚动） | 优化 |
| W14 | Bug 修复 | 稳定性 |
| W15 | 代码整理、注释完善 | 代码质量 |
| W15 | README 文档 | 文档 |
| W15 | 项目演示准备 | 演示 |

---

## 10. Git 分支策略

```
main (生产分支)
  │
  └── develop (开发分支)
        │
        ├── feature/user-auth        # 用户认证
        ├── feature/note-publish     # 笔记发布
        ├── feature/comment          # 评论功能
        ├── feature/search           # 搜索功能
        ├── feature/ai-summary       # AI 总结
        └── feature/xxx              # 其他功能
```

**分支规范：**
- `main`：稳定版本，只接受 develop 合并
- `develop`：开发主分支，功能完成后合并到此
- `feature/*`：功能分支，从 develop 创建，完成后合并回 develop

**Commit 规范：**
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

## 11. 图片存储方案

### 11.1 存储路径

```
uploads/
├── avatar/                    # 头像
│   └── {userId}/
│       └── {timestamp}_{random}.jpg
└── note/                      # 笔记图片
    └── {userId}/
        └── {timestamp}_{random}.jpg
```

### 11.2 文件命名规则

```
{timestamp}_{random}.{ext}
例如：1710144000000_a1b2c3.jpg
```

### 11.3 上传限制

| 配置项 | 值 |
|--------|-----|
| 单张图片最大 | 5MB |
| 支持格式 | jpg, jpeg, png, gif, webp |
| 单篇笔记最多 | 9 张 |

---

## 12. 安全设计

### 12.1 认证流程

```
登录请求 ──► 验证用户名密码 ──► 生成 JWT Token ──► 返回 Token
                                    │
后续请求 ──► 携带 Authorization Header ──► JWT Filter 验证
                                              │
                                    ┌─────────┴─────────┐
                                    ▼                   ▼
                               验证通过              验证失败
                                    │                   │
                                    ▼                   ▼
                              放行请求            返回 401
```

### 12.2 JWT Token 结构

```json
{
  "sub": "userId",
  "username": "xxx",
  "iat": 1710144000,
  "exp": 1710230400
}
```

**Token 有效期**：24 小时

---

## 13. 环境配置示例

### 13.1 后端配置 (application-dev.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/essencity?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# JWT 配置
jwt:
  secret: your-secret-key-at-least-256-bits
  expiration: 86400000  # 24小时

# 文件上传配置
upload:
  path: ./uploads
  max-size: 5242880  # 5MB
  allowed-types: jpg,jpeg,png,gif,webp

# 火山引擎配置
volcengine:
  api-key: your-api-key
  endpoint: https://ark.cn-beijing.volces.com/api/v3
  model: doubao-pro-4k
```

### 13.2 前端配置 (vite.config.js)

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@': '/src'
    }
  }
})
```

---

## 14. 附录

### 14.1 预设话题列表（初始数据）

| 话题名称 | 分类 |
|---------|------|
| #学习打卡# | 学习 |
| #考研# | 学习 |
| #四六级# | 学习 |
| #校园生活# | 生活 |
| #美食探店# | 生活 |
| #穿搭分享# | 时尚 |
| #好物推荐# | 购物 |
| #旅行日记# | 旅行 |
| #读书笔记# | 阅读 |
| #求职经验# | 职场 |

### 14.2 参考资源

- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus 官方文档](https://element-plus.org/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [火山引擎豆包 API 文档](https://www.volcengine.com/docs/82379)
- [Web Speech API MDN](https://developer.mozilla.org/en-US/docs/Web/API/Web_Speech_API)

---

**文档版本历史**

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2026-03-11 | 初始版本 | Copilot & yclin30 |