# 林烨澄 - 后端功能迭代贡献说明

## 基本信息

- **姓名**: 林烨澄
- **学号**: 2412190630
- **日期**: 2026-04-12

---

## 贡献内容

### 1. 帖子编辑功能后端实现

**新增接口**：`PUT /posts/{id}` - 更新帖子

**修改文件**：

- `backend/src/main/java/com/xiaohongshu/service/PostService.java`
  - 新增 `updatePost(Long id, Post updatedPost)` 方法
  - 使用 `@Transactional` 保证数据一致性
  - 只更新非空字段，保留原有数据

- `backend/src/main/java/com/xiaohongshu/controller/PostController.java`
  - 新增 `@PutMapping("/{id}")` 端点
  - 解析请求体中的 title、description、type、url、cover_url、tag 字段
  - 调用 PostService 更新帖子

**PostService.updatePost 核心代码**：

```java
@Transactional
public Post updatePost(Long id, Post updatedPost) {
    Post post = getPostById(id);
    if (updatedPost.getTitle() != null) {
        post.setTitle(updatedPost.getTitle());
    }
    if (updatedPost.getDescription() != null) {
        post.setDescription(updatedPost.getDescription());
    }
    if (updatedPost.getType() != null) {
        post.setType(updatedPost.getType());
    }
    if (updatedPost.getUrl() != null) {
        post.setUrl(updatedPost.getUrl());
    }
    if (updatedPost.getCoverUrl() != null) {
        post.setCoverUrl(updatedPost.getCoverUrl());
    }
    if (updatedPost.getTag() != null) {
        post.setTag(updatedPost.getTag());
    }
    return postRepository.save(post);
}
```

### 2. AI 总结功能修复

**问题**：MiniMax API 返回格式与代码预期不符，导致 `parseResponse` 解析失败。

**原因**：代码期望 `choices[0].messages[0].content`，实际返回 `choices[0].message.content`（`message` 是单个对象，不是数组）。

**修复**：修改 `AIService.java` 的 `parseResponse` 方法：

```java
// 修复前
List<?> messages = (List<?>) choice.get("messages");
if (messages != null && !messages.isEmpty()) {
    Map<?, ?> message = (Map<?, ?>) messages.get(0);
    return (String) message.get("content");
}

// 修复后
Map<?, ?> message = (Map<?, ?>) choice.get("message");
if (message != null) {
    return (String) message.get("content");
}
```

**其他优化**：
- 添加日志输出完整 AI 响应，便于调试
- 修复 `buildPrompt` 方法，处理空内容的边界情况

```java
private String buildPrompt(String title, String content) {
    String actualContent = (content == null || content.trim().isEmpty())
        ? "（无正文内容）"
        : content;
    return "请根据以下小红书笔记的标题和内容，用一段简洁的文字（100字以内）总结其核心内容。\n\n" +
           "标题：" + title + "\n\n" +
           "内容：" + actualContent;
}
```

### 3. CORS 配置优化

**问题**：前端 PUT 请求返回 405 Method Not Allowed。

**解决**：在 `PostController` 的 `@CrossOrigin` 注解中显式指定允许的方法：

```java
@CrossOrigin(
    origins = "http://localhost:5173",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true"
)
```

---

## 技术要点

### RESTful API 设计

编辑功能使用 `PUT` 方法更新帖子资源，符合 RESTful 设计规范：

- `POST /posts` - 创建帖子
- `PUT /posts/{id}` - 更新帖子
- `DELETE /posts/{id}` - 删除帖子

### Spring Data JPA 事务管理

使用 `@Transactional` 注解确保更新操作的原子性，避免部分更新导致的数据不一致。

### API 响应格式

统一使用 `Map.of()` 构建响应结构：

```java
return ResponseEntity.ok(Map.of(
    "success", true,
    "message", "Post deleted successfully"
));
```

### CORS 跨域配置

在 `@CrossOrigin` 注解中显式指定 `methods` 数组，确保浏览器预检请求（OPTIONS）能够正确处理。

---

## 相关文件

| 文件路径 | 说明 |
| --- | --- |
| backend/src/main/java/com/xiaohongshu/service/PostService.java | 新增 updatePost 方法 |
| backend/src/main/java/com/xiaohongshu/controller/PostController.java | 新增 PUT 端点和 CORS 配置 |
| backend/src/main/java/com/xiaohongshu/service/AIService.java | 修复 parseResponse 和 buildPrompt |

---

## API 接口文档

### 更新帖子

```
PUT /posts/{id}
Content-Type: application/json

{
    "title": "帖子标题",
    "description": "帖子描述",
    "type": "image",
    "url": "/uploads/xxx.jpg",
    "cover_url": "/uploads/xxx.jpg",
    "tag": "美食"
}
```

**响应**：

- 成功：返回更新后的 Post 对象
- 失败：返回 `{"message": "Failed to update post"}`

### 常见错误

| 错误码 | 原因 | 解决方案 |
| --- | --- | --- |
| 405 | CORS 未允许 PUT 方法 | 检查 @CrossOrigin 注解配置 |
| 400 | 请求体格式错误 | 检查 JSON 格式和字段类型 |

---

## 遇到的问题和解决

### 问题一：AI 响应解析失败

**现象**：`java.lang.RuntimeException: 无法解析AI响应格式`

**排查**：添加日志打印完整响应，发现实际返回格式与代码预期不同。

**解决**：修改 `parseResponse` 方法使用 `choice.get("message")` 而不是 `choice.get("messages")`。

### 问题二：PUT 请求 405 错误

**现象**：前端发送 PUT 请求时被浏览器拦截。

**排查**：检查 Spring Security 配置发现已放行所有请求，问题出在 CORS 配置。

**解决**：在 `@CrossOrigin` 中显式指定所有允许的 HTTP 方法。

---

## 心得体会

1. **API 版本兼容性**：外部 API（如 MiniMax）的响应格式可能随版本变化，需要保持日志输出以便快速定位问题。

2. **接口设计的一致性**：新增接口时遵循项目现有的命名规范和响应格式，保持整体一致性。

3. **前后端协同**：编辑功能涉及前后端多个文件的修改，需要保持接口契约一致。
