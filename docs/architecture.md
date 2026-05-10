# Essencity 架构设计文档

> 最后更新：2026-05-10

## 1. 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      浏览器 / 手机                            │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   前端 (Vue 3 + Vite)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 组件层    │  │ API 层   │  │ 状态管理  │  │ 工具函数  │   │
│  │ Vant 4   │  │ Fetch    │  │ (ref)    │  │ composables│  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────┬───────────────────────────────────┘
                          │ HTTP
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                后端 (Spring Boot 3.2)                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  Security Filter                      │  │
│  │              (JWT + BCrypt + CORS)                    │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │Controller│  │ Service  │  │Repository│  │  Entity  │   │
│  │  层      │→│  层       │→│  层       │←│  层       │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────┬───────────────────────────────────┘
                          │
          ┌───────────────┼───────────────┐
          ▼               ▼               ▼
    ┌──────────┐   ┌──────────┐   ┌──────────┐
    │  MySQL   │   │ 文件存储  │   │ MiniMax  │
    │  8.0     │   │ uploads/ │   │ AI API   │
    └──────────┘   └──────────┘   └──────────┘
```

---

## 2. 技术栈详情

### 前端
| 类别 | 技术 | 说明 |
|------|------|------|
| 框架 | Vue 3 (Composition API) | 渐进式前端框架 |
| 构建工具 | Vite 5 | 快速开发服务器 |
| 移动端 UI | Vant 4 | TabBar、Popup 等组件 |
| HTTP 客户端 | Fetch API | 原生 API |
| 语音识别 | Web Speech API | 浏览器原生 API |

### 后端
| 类别 | 技术 | 说明 |
|------|------|------|
| 框架 | Spring Boot 3.2 | Java 主流框架 |
| ORM | Spring Data JPA | 数据持久化 |
| 安全 | Spring Security | 认证授权 |
| 数据库 | MySQL 8.0 | 关系型数据库 |
| 认证 | JWT | 无状态认证 |
| 加密 | BCrypt | 密码加密 |
| AI | MiniMax API | AI 总结能力 |

---

## 3. 后端架构

### 3.1 包结构

```
com.xiaohongshu/
├── config/              # 配置类
│   ├── SecurityConfig   # Spring Security 配置
│   ├── JwtUtil          # JWT 工具类
│   ├── JwtAuthenticationFilter  # JWT 过滤器
│   └── WebConfig        # CORS + 静态资源配置
├── controller/          # 控制器
│   ├── AuthController   # 认证相关
│   ├── PostController   # 帖子相关
│   ├── FileController   # 文件上传
│   ├── AIController     # AI 总结
│   └── NotificationController  # 通知
├── service/             # 服务层
├── repository/          # 数据访问层
├── entity/              # 实体类
├── dto/                 # 数据传输对象
└── DataInitializer.java # 测试数据初始化
```

### 3.2 认证流程

```
用户登录 → 验证密码(BCrypt) → 生成 JWT → 返回 Token
    ↓
后续请求 → JWT Filter → 解析 Token → 验证身份 → 处理请求
```

---

## 4. 数据库设计

### 4.1 ER 图

```
users ──┬── posts ──┬── comments
        │          ├── likes
        │          └── collections
        │
        └── follows (自关联)
```

### 4.2 核心表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| users | 用户表 | id, username, password, nickname, avatar, bio |
| posts | 帖子表 | id, title, description, type, url, author_id, tag |
| comments | 评论表 | id, post_id, user_id, content, parent_id |
| likes | 点赞表 | id, user_id, post_id |
| collections | 收藏表 | id, user_id, post_id |
| follows | 关注表 | id, follower_id, following_id |

---

## 5. API 设计

### 5.1 认证相关

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/login | 用户登录 |
| GET | /api/auth/me | 获取当前用户 |

### 5.2 帖子相关

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/posts | 获取帖子列表 |
| POST | /api/posts | 创建帖子 |
| GET | /api/posts/{id} | 获取帖子详情 |
| PUT | /api/posts/{id} | 更新帖子 |
| DELETE | /api/posts/{id} | 删除帖子 |
| POST | /api/posts/{id}/like | 点赞/取消点赞 |
| POST | /api/posts/{id}/collect | 收藏/取消收藏 |

### 5.3 评论相关

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/posts/{id}/comments | 获取评论列表 |
| POST | /api/posts/{id}/comments | 发表评论 |
| DELETE | /api/comments/{id} | 删除评论 |

### 5.4 其他

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/upload | 文件上传 |
| POST | /api/ai/summarize | AI 总结 |
| GET | /api/notifications | 获取通知 |

---

## 6. 安全设计

### 6.1 认证授权

- JWT Token 认证，有效期 24 小时
- BCrypt 密码加密
- 无状态会话管理

### 6.2 安全防护

- CORS 跨域配置
- SQL 注入防护（JPA 参数化查询）
- XSS 防护（CSP 策略）
- 文件上传校验（类型、大小）

---

## 7. 移动端适配

### 7.1 响应式断点

- 移动端：≤ 768px
- 桌面端：> 768px

### 7.2 布局策略

| 组件 | 桌面端 | 移动端 |
|------|--------|--------|
| 导航 | 左侧边栏 | 底部 TabBar |
| 瀑布流 | 3-5 列 | 2 列自适应 |
| 详情页 | 弹窗 | 全屏 |
| 搜索栏 | 居中 + 操作链接 | 简化 |

---

## 8. 文件存储

### 8.1 存储位置

- 运行时：`backend/uploads/`
- 通过 `/api/uploads/**` 访问

### 8.2 文件命名

- UUID + 原始文件名
- 示例：`7ca432b6-d59e-4b84-9779-958f86618f65_笔记本主页面.png`
