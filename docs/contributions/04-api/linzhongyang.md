# 林忠阳 - API 文档与测试贡献说明

## 基本信息

- **姓名**: 林忠阳
- **学号**: 2212190528
- **日期**: 2026-03-25

---

## 贡献内容

### 1. OpenAPI 规范文档 (docs/api.yaml)

创建了完整的 OpenAPI 3.0.3 规范的 YAML 文件，包含：

- **认证模块**: 登录、注册、资料更新、关注/粉丝操作
- **帖子模块**: 帖子列表、创建、详情、统计数据
- **点赞/收藏**: 点赞、取消点赞、收藏、取消收藏、状态查询
- **评论模块**: 获取评论、添加评论、删除评论
- **通知模块**: 获取通知列表
- **上传模块**: 文件上传接口

**特性**:

- 完整的请求/响应示例
- 数据模型定义 (User, Post, Comment, Notification)
- 统一错误码说明
- 支持 Swagger UI 可视化

### 2. API 接口文档更新 (docs/api.md)

对原有的 API 文档进行了全面更新：

- **路径修正**: `/users/*` → `/auth/*`, `/notes/*` → `/posts/*`
- **新增接口**: 关注操作、帖子统计、收藏/点赞列表等
- **请求/响应格式**: 更新为实际使用的格式
- **移除未实现功能**: 话题、搜索、草稿、AI模块

### 3. 后端接口测试 (backend/src/test/)

创建了基于 Spring Boot Test 的接口测试：

- **AuthControllerTest.java** (11个测试)
  
  - 用户注册/登录
  - 更新用户资料
  - 获取关注状态/列表/数量

- **PostControllerTest.java** (7个测试)
  
  - 获取帖子列表
  - 文件上传
  - 帖子详情查询
  - 评论获取

**测试配置**:

- 使用 H2 内存数据库进行测试
- 配置独立测试环境 (application-test.properties)
- 使用 `@Transactional` 确保测试数据隔离

---

## 技术要点

### OpenAPI 规范

- 使用 OpenAPI 3.0.3 标准
- 定义了 components/schemas 用于复用数据模型
- 支持参数化查询、分页等常见场景

### 测试设计

- MockMvc 模拟 HTTP 请求
- H2 数据库实现测试隔离
- `@ActiveProfiles("test")` 切换测试配置

---

## 相关文件

| 文件路径                                                          | 说明           |
| ------------------------------------------------------------- | ------------ |
| docs/api.yaml                                                 | OpenAPI 规范文档 |
| docs/api.md                                                   | API 接口文档     |
| backend/src/test/java/com/xiaohongshu/AuthControllerTest.java | 认证模块测试       |
| backend/src/test/java/com/xiaohongshu/PostControllerTest.java | 帖子模块测试       |
| backend/src/test/resources/application-test.properties        | 测试配置         |

---

## 测试结果

```
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 工作过程

### 1. 分析现有代码

首先阅读了前端代码，分析 API 调用情况：

- **App.vue**: 帖子列表获取、搜索、详情查看
- **AuthModal.vue**: 用户登录/注册
- **CreationPage.vue**: 创建帖子、文件上传
- **PostDetailModal.vue**: 点赞、收藏、评论
- **ProfilePage.vue**: 用户资料、关注操作
- **NotificationPage.vue**: 通知获取

### 2. 编写 OpenAPI 规范

根据前端调用情况，编写了完整的 OpenAPI 3.0.3 规范，包括：

- 定义了 5 个主要标签 (tags)
- 40+ 个 API 端点
- 4 个数据模型 (schemas)

### 3. 更新 API 文档

对照实际代码，修正了原有文档中的：

- 路径错误 (notes → posts, users → auth)
- 字段名称 (content → description)
- 数据类型 (video/image/article)

### 4. 编写测试用例

遇到的主要问题及解决方案：

- **问题1**: MySQL 数据库约束导致测试数据冲突
  
  - **解决**: 使用 H2 内存数据库，配置独立测试环境

- **问题2**: 外键关联导致查询失败
  
  - **解决**: 更新数据库中旧数据的 author_id

- **问题3**: 测试间数据污染
  
  - **解决**: 使用 `@Transactional` 注解确保事务回滚

---

## API 端点详细列表

### 认证模块 (/auth)

| 方法   | 端点                         | 说明   |
| ---- | -------------------------- | ---- |
| POST | /auth/register             | 用户注册 |
| POST | /auth/login                | 用户登录 |
| PUT  | /auth/profile              | 更新资料 |
| POST | /auth/follow               | 关注用户 |
| POST | /auth/unfollow             | 取消关注 |
| GET  | /auth/following-status     | 关注状态 |
| GET  | /auth/followers/{id}       | 粉丝列表 |
| GET  | /auth/following/{id}       | 关注列表 |
| GET  | /auth/followers-count/{id} | 粉丝数  |
| GET  | /auth/following-count/{id} | 关注数  |

### 帖子模块 (/posts)

| 方法     | 端点                           | 说明   |
| ------ | ---------------------------- | ---- |
| GET    | /posts                       | 帖子列表 |
| GET    | /posts/{id}                  | 帖子详情 |
| POST   | /posts                       | 创建帖子 |
| POST   | /posts/upload                | 上传文件 |
| DELETE | /posts/{id}                  | 删除帖子 |
| POST   | /posts/{id}/like             | 点赞   |
| POST   | /posts/{id}/unlike           | 取消点赞 |
| GET    | /posts/{id}/like/status      | 点赞状态 |
| POST   | /posts/{id}/collect          | 收藏   |
| POST   | /posts/{id}/uncollect        | 取消收藏 |
| GET    | /posts/{id}/collect/status   | 收藏状态 |
| GET    | /posts/{id}/comments         | 评论列表 |
| POST   | /posts/{id}/comments         | 添加评论 |
| DELETE | /posts/comments/{id}         | 删除评论 |
| GET    | /posts/user/{id}/stats       | 用户统计 |
| GET    | /posts/user/{id}/collections | 收藏列表 |
| GET    | /posts/user/{id}/likes       | 点赞列表 |

---

## 测试用例详细说明

### AuthControllerTest

| 测试方法                           | 测试内容   | 预期结果    |
| ------------------------------ | ------ | ------- |
| testRegister_Success           | 新用户注册  | 返回用户信息  |
| testRegister_DuplicateUsername | 重复用户名  | 返回400错误 |
| testLogin_Success              | 正确登录   | 返回用户信息  |
| testLogin_WrongPassword        | 错误密码   | 返回400错误 |
| testLogin_UserNotFound         | 用户不存在  | 返回400错误 |
| testGetFollowingStatus         | 查询关注状态 | 返回状态    |
| testGetFollowers               | 获取粉丝列表 | 返回空数组   |
| testGetFollowing               | 获取关注列表 | 返回空数组   |
| testGetFollowersCount          | 获取粉丝数  | 返回数量    |
| testGetFollowingCount          | 获取关注数  | 返回数量    |
| testUpdateProfile              | 更新用户资料 | 返回更新后信息 |

### PostControllerTest

| 测试方法                            | 测试内容    | 预期结果    |
| ------------------------------- | ------- | ------- |
| testGetAllPosts                 | 获取帖子列表  | 返回数组    |
| testGetAllPosts_WithTitleSearch | 标题搜索    | 返回筛选结果  |
| testGetAllPosts_WithTagFilter   | 标签筛选    | 返回筛选结果  |
| testGetPostById_NotFound        | 查询不存在帖子 | 返回400错误 |
| testUploadFile_Image            | 上传图片    | 返回文件URL |
| testUploadFile_Video            | 上传视频    | 返回文件URL |
| testGetComments                 | 获取评论列表  | 返回数组    |
