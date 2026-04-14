# 林忠阳 - 后端开发贡献说明

## 基本信息

- **姓名**: 林忠阳
- **学号**: 2212190528
- **日期**: 2026-04-01

---

## 贡献内容

### 1. 后端AI总结模块开发

#### 功能描述

为小红书笔记提供AI智能总结功能，调用 MiniMax API 生成简洁的笔记摘要。

#### API 接口

| 方法 | 路径 | 说明 |
| ---- | ---- | ---- |
| POST | /ai/summary | 生成并保存AI总结 |
| GET | /ai/summary/{postId} | 获取指定帖子的AI总结 |

#### 请求/响应格式

**POST /ai/summary** 请求体：
```json
{
  "postId": 1,
  "title": "笔记标题",
  "content": "笔记正文内容"
}
```

响应：
```json
{
  "ai_summary": "这是一篇关于...的笔记，核心要点包括..."
}
```

#### 技术实现

**AIService.java** ([backend/src/main/java/com/xiaohongshu/service/AIService.java](../../backend/src/main/java/com/xiaohongshu/service/AIService.java)):
- 使用 RestTemplate 调用 MiniMax Chat API
- 构建提示词：请为以下小红书笔记生成一个简洁的AI总结，突出重点内容和亮点
- 参数配置：temperature=0.7，max_tokens=500
- 解析 MiniMax 响应格式：`choices[0].messages[0].content`

**AIController.java** ([backend/src/main/java/com/xiaohongshu/controller/AIController.java](../../backend/src/main/java/com/xiaohongshu/controller/AIController.java)):
- `POST /ai/summary`：接收 postId、title、content，调用 AI 服务生成总结并保存到 Post 记录
- `GET /ai/summary/{postId}`：根据帖子 ID 返回已生成的 AI 总结

#### 配置参数

MiniMax API 配置（[application.properties](../../backend/src/main/resources/application.properties#L27-L29)）：
```properties
minimax.api-key=your_api_key
minimax.base-url=https://api.minimax.chat
minimax.model=MiniMax-Text-01
```

#### 相关文件

| 文件路径 | 说明 |
| ------- | ---- |
| [AIService.java](../../backend/src/main/java/com/xiaohongshu/service/AIService.java) | AI 服务层，调用 MiniMax API |
| [AIController.java](../../backend/src/main/java/com/xiaohongshu/controller/AIController.java) | REST API 控制器 |
| [Post.java:40-41,122-126](../../backend/src/main/java/com/xiaohongshu/entity/Post.java) | 实体类添加 aiSummary 字段 |
| [application.properties:27-29](../../backend/src/main/resources/application.properties) | MiniMax API 配置 |

#### 技术要点

- **API 调用**：使用 Spring RestTemplate 发送 HTTP POST 请求
- **提示词构建**：引导 AI 生成简洁的小红书风格总结（100字以内）
- **响应解析**：MiniMax 返回格式为 `choices[0].messages[0].content`
- **错误处理**：捕获异常并返回友好的错误信息

---

### 2. 文件上传配置优化

#### 问题描述

前端图片上传功能修复过程中，发现后端文件存储路径配置存在问题：
- 开发环境中，头像上传和笔记图片上传使用了不同的目录
- 相对路径 `./uploads` 导致文件存储位置不统一
- 前端无法正确访问已上传的文件（404）

#### 解决方案

修改 [application.properties](../backend/src/main/resources/application.properties):

```properties
# 修改前
file.upload-dir=./uploads

# 修改后
file.upload-dir=D:/1/Essencity/uploads
```

#### 相关文件

| 文件路径 | 说明 |
| ------- | ---- |
| [backend/src/main/resources/application.properties:24](../backend/src/main/resources/application.properties#L24) | 上传目录配置 |

---

### 3. OpenAPI 规范文档

创建了完整的 OpenAPI 3.0.3 规范文档，包含：
- 认证模块 (登录、注册、资料更新、关注操作)
- 帖子模块 (CRUD、文件上传、点赞收藏)
- 评论模块 (获取、添加、删除)
- 通知模块

详见 [docs/contributions/04-api/linzhongyang.md](../docs/contributions/04-api/linzhongyang.md)

---

### 4. 后端接口测试

创建了基于 Spring Boot Test 的接口测试：
- **AuthControllerTest.java**: 11个测试用例
- **PostControllerTest.java**: 7个测试用例

详见 [docs/contributions/04-api/linzhongyang.md](../docs/contributions/04-api/linzhongyang.md)

---

## 技术要点

### 文件存储配置

- 建议使用绝对路径确保目录一致性
- Windows 和 Linux 路径格式不同
- 确保应用有读写权限

### 跨域配置

- `@CrossOrigin` 注解配置前端访问
- `allowCredentials = "true"` 支持认证信息

---

## 工作过程

### 1. 问题排查

1. 用户反馈上传的图片无法显示（404）
2. 检查后端日志发现文件上传成功
3. 对比两个上传接口的路径配置
4. 发现 FileController 和 PostController 使用相同的配置但目录创建在不同位置

### 2. 解决措施

1. 修改配置为绝对路径
2. 同步历史文件到统一目录
3. 配合前端配置 Vite 代理和 Nginx 反向代理

### 3. 测试验证

- 后端接口返回正确的文件 URL
- 前端通过代理正确访问文件
- 图片可以正常显示