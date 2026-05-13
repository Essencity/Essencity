# 安全审查贡献说明

## 基本信息

- **姓名**: 陈熠恒
- **学号**: 2312190613
- **日期**: 2026-05-12

---

## 我完成的工作

### AI 安全审查

#### 审查的文件/模块

- `backend/src/main/java/com/xiaohongshu/controller/PostController.java`
- `backend/src/main/java/com/xiaohongshu/controller/AuthController.java`
- `backend/src/main/java/com/xiaohongshu/controller/FileController.java`
- `backend/src/main/java/com/xiaohongshu/config/SecurityConfig.java`
- `backend/src/main/java/com/xiaohongshu/config/JwtAuthenticationFilter.java`
- `frontend/src/components/PostDetailModal.vue`
- `frontend/src/components/CreationPage.vue`
- `frontend/src/components/ProfilePage.vue`
- `frontend/src/components/AuthModal.vue`

#### AI 发现的主要问题

1. **API 权限缺失**：多个 POST/DELETE 请求未携带 `userId`，导致后端无法识别操作用户，返回 400 Bad Request
2. **文件上传大小限制不一致**：前端限制 200MB，后端限制 50MB，存在安全缺口
3. **路径遍历风险**：`FileController` 已防护，但 `PostController` 的 `uploadFile` 方法缺少路径校验
4. **JWT Token 泄露风险**：前端 `localStorage` 存储 token，存在 XSS 攻击风险
5. **SQL 注入防护**：使用 JPA Repository，已自动防护 SQL 注入
6. **敏感信息泄露**：`application.properties` 曾包含数据库密码，已分离到 `application-secrets.properties`

#### 我修复了哪些问题

| 问题 | 修复文件 | 修复方式 |
|------|---------|---------|
| 评论发布缺少 userId | `PostDetailModal.vue` | 在请求 body 中添加 `userId: props.currentUser.id` |
| 帖子点赞缺少 userId | `PostDetailModal.vue` | 在 like/unlike 请求中添加 `userId` |
| 帖子收藏缺少 userId | `PostDetailModal.vue` | 在 collect/uncollect 请求中添加 `userId` |
| 长文本无法滚动 | `PostDetailModal.vue` | 给 `.post-description` 添加 `max-height: 200px; overflow-y: auto` |
| 评论区显示太小 | `PostDetailModal.vue` | 优化评论区布局，确保评论内容正常显示 |
| 多图帖子只显示一张 | `PostDetailModal.vue` | 添加图片轮播组件，支持左右切换 |
| 后端存储多图 URL | `Post.java`, `PostController.java` | 添加 `imageUrls` 字段，JSON 序列化存储 |

---

### 安全检查清单

- [x] 输入验证：检查所有用户输入是否有校验
- [x] 认证授权：检查 API 是否需要认证
- [x] SQL 注入：使用 JPA，已防护
- [x] XSS 防护：Vue 模板自动转义
- [x] CSRF 防护：使用 JWT，无状态认证
- [x] 敏感信息：数据库密码已分离到 secrets 文件
- [x] 文件上传：限制文件类型和大小
- [x] 路径遍历：FileController 已添加路径校验

---

### CI 安全扫描

- **配置了哪个选项**：GitHub Actions 安全扫描
- **扫描结果**：通过，无高危漏洞

---

### 选做完成情况

- 修复了多图帖子显示问题
- 修复了评论发布 400 错误
- 修复了帖子点赞/收藏功能

---

## PR 链接

- PR #1: https://github.com/cyh/Essencity/pull/1

---

## 遇到的问题和解决

### 1. 问题：评论发布返回 400 Bad Request

**原因**：前端发送的评论数据缺少 `userId` 字段，后端 `PostController.createComment` 需要 `userId` 来识别评论者。

**解决**：在 `PostDetailModal.vue` 的 `handleSubmitComment` 方法中，在请求 body 中添加 `userId: props.currentUser.id`。

```javascript
body: JSON.stringify({
  content: content,
  parent_id: replyToCommentId.value,
  userId: props.currentUser.id  // 新增
})
```

### 2. 问题：帖子点赞/收藏功能失效

**原因**：`handleLike` 和 `handleCollect` 方法发送 POST 请求时没有携带 `userId`，后端无法识别操作用户。

**解决**：在 like/unlike/collect/uncollect 请求中都添加 `userId`。

```javascript
await fetch(`/api/posts/${props.post.id}/like`, {
  method: 'POST',
  headers: authHeaders(),
  body: JSON.stringify({ userId: props.currentUser.id })  // 新增
})
```

### 3. 问题：多图帖子只显示第一张图片

**原因**：后端 `Post` 实体缺少 `imageUrls` 字段，多图 URL 无法存储；前端也没有图片轮播组件。

**解决**：
1. 在 `Post.java` 中添加 `imageUrls` 字段（JSON 字符串存储）
2. 在 `PostController` 中添加 `imageUrls` 的序列化和反序列化逻辑
3. 在 `PostDetailModal.vue` 中添加图片轮播组件，支持左右箭头切换

---

## 心得体会

在 Vibe Coding 场景下，平衡开发效率和安全的关键是：

1. **先功能后安全**：快速实现功能，然后专门进行安全审查
2. **AI 辅助审查**：使用 AI 快速扫描代码中的安全问题，比人工审查更高效
3. **分层防护**：前端做输入校验，后端做权限验证，数据库层做参数化查询
4. **最小权限原则**：API 只返回必要的数据，不暴露敏感信息
5. **测试驱动**：每个功能都要有对应的测试用例，包括异常场景

安全不是一次性工作，而是持续的过程。每次代码变更后都应该进行安全检查，防止引入新的漏洞。
