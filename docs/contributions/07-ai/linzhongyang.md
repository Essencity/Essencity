# 林忠阳 - AI 智能总结功能开发

## 基本信息

- **姓名**: 林忠阳
- **学号**: 2212190528
- **日期**: 2026-04-20

---

## 贡献内容

### AI 智能总结功能

#### 功能描述

为小红书笔记平台开发 AI 智能总结功能，帮助用户快速了解帖子核心内容。用户点击帖子详情页的 AI 按钮，系统自动生成一段简洁的摘要（100字以内），突出笔记的重点和亮点。

#### 功能特点

- **一键生成**：用户只需点击 AI 按钮，系统自动分析标题和内容生成摘要
- **智能复用**：已生成的总结会持久化存储，避免重复调用 AI 节省成本
- **友好体验**：加载过程显示动画提示，错误时支持重试
- **轻量输出**：限制摘要长度在 100 字以内，适合快速浏览

#### 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                        用户交互                              │
│              PostDetailModal 点击 AI 按钮                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    AiSummary 组件 (前端)                      │
│        getAiSummary() → generateAiSummary()                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 AIController (后端)                          │
│        GET /ai/summary/{postId}                              │
│        POST /ai/summary                                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    AIService (后端)                          │
│            generateSummary(title, content)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     MiniMax API                              │
│            https://api.minimax.chat                          │
│            /v1/text/chatcompletion_v2                        │
└─────────────────────────────────────────────────────────────┘
```

#### API 接口

| 方法 | 路径 | 说明 |
| ---- | ---- | ---- |
| POST | /ai/summary | 生成并保存 AI 总结 |
| GET | /ai/summary/{postId} | 获取指定帖子的 AI 总结 |

#### 请求/响应格式

**POST /ai/summary**

请求体：
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

**GET /ai/summary/{postId}**

响应：
```json
{
  "ai_summary": "已生成的总结内容..."
}
```

#### 核心代码

**AIService.java** ([backend/src/main/java/com/xiaohongshu/service/AIService.java](../../backend/src/main/java/com/xiaohongshu/service/AIService.java))

核心方法 `generateSummary(title, content)`：

```java
public String generateSummary(String title, String content) {
    String url = baseUrl + "/v1/text/chatcompletion_v2";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);

    // 构建提示词
    String prompt = buildPrompt(title, content);

    // 构建请求体 - MiniMax API 格式
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", model);
    requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
    requestBody.put("temperature", 0.7);
    requestBody.put("max_tokens", 500);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

    return parseResponse(response.getBody());
}
```

**提示词构建**：

```java
private String buildPrompt(String title, String content) {
    String actualContent = (content == null || content.trim().isEmpty())
        ? "（无正文内容）" : content;
    return "请根据以下小红书笔记的标题和内容，用一段简洁的文字（100字以内）总结其核心内容。\n\n" +
           "标题：" + title + "\n\n" +
           "内容：" + actualContent;
}
```

**响应解析**：

```java
private String parseResponse(Map responseBody) {
    List<?> choices = (List<?>) responseBody.get("choices");
    if (choices != null && !choices.isEmpty()) {
        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");
        if (message != null) {
            return (String) message.get("content");
        }
    }
    throw new RuntimeException("无法解析AI响应格式");
}
```

**AIController.java** ([backend/src/main/java/com/xiaohongshu/controller/AIController.java](../../backend/src/main/java/com/xiaohongshu/controller/AIController.java))

```java
@PostMapping("/summary")
@Transactional
public ResponseEntity<?> generateAiSummary(@RequestBody Map<String, Object> request) {
    Long postId = ((Number) request.get("postId")).longValue();
    String title = (String) request.get("title");
    String content = (String) request.get("content");

    Post post = postService.getPostById(postId);
    if (post == null) {
        return ResponseEntity.notFound().build();
    }

    // 调用 AI 生成总结
    String summary = aiService.generateSummary(title, content);

    // 使用 EntityManager 直接更新，保留当前事务
    post.setAiSummary(summary);
    entityManager.merge(post);

    return ResponseEntity.ok(Map.of("ai_summary", summary));
}
```

#### 前端组件

**ai.js** ([frontend/src/api/ai.js](../../frontend/src/api/ai.js))

```javascript
// 获取已有总结
export async function getAiSummary(postId) {
    const response = await fetch(`${API_BASE}/ai/summary/${postId}`)
    if (!response.ok) throw new Error('获取AI总结失败')
    return response.json()
}

// 生成新总结
export async function generateAiSummary(postId, title, content) {
    const response = await fetch(`${API_BASE}/ai/summary`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ postId, title, content })
    })
    if (!response.ok) throw new Error('生成AI总结失败')
    return response.json()
}
```

**AiSummary.vue** ([frontend/src/components/AiSummary.vue](../../frontend/src/components/AiSummary.vue))

组件接收三个 props：
- `postId`: 帖子 ID
- `title`: 帖子标题
- `content`: 帖子正文

核心逻辑：优先获取已有总结，无总结时自动触发生成流程。

**PostDetailModal.vue** 集成：

```vue
<!-- AI 按钮入口 -->
<div class="action-item" @click="showAiSummary = true">
  <svg viewBox="...">...</svg>
  <span>AI</span>
</div>

<!-- AI 总结弹窗 -->
<AiSummary
  v-if="showAiSummary"
  :post-id="post.id"
  :title="post.title"
  :content="post.description"
  @close="showAiSummary = false"
/>
```

#### 数据库设计

在 `posts` 表中添加 `ai_summary` 字段 ([backend/src/main/resources/schema.sql](../../backend/src/main/resources/schema.sql))：

```sql
CREATE TABLE IF NOT EXISTS posts (
    ...
    ai_summary TEXT,              -- AI 总结内容
    ...
);
```

Post 实体类添加字段 ([backend/src/main/java/com/xiaohongshu/entity/Post.java](../../backend/src/main/java/com/xiaohongshu/entity/Post.java))：

```java
@Column(name = "ai_summary", columnDefinition = "TEXT")
private String aiSummary;

public String getAiSummary() { return aiSummary; }
public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
```

#### 配置参数

MiniMax API 配置 ([backend/src/main/resources/application.properties](../../backend/src/main/resources/application.properties))：

```properties
# MiniMax AI Configuration
minimax.base-url=https://api.minimax.chat
minimax.model=MiniMax-M2.1
```

密钥配置 ([backend/src/main/resources/application-secrets.properties.example](../../backend/src/main/resources/application-secrets.properties.example))：

```properties
# MiniMax AI API 密钥
minimax.api-key=YOUR_API_KEY
```

| 参数 | 说明 | 默认值 |
| ---- | ---- | ------ |
| minimax.base-url | API 基础地址 | https://api.minimax.chat |
| minimax.model | 模型名称 | MiniMax-M2.1 |
| temperature | 生成随机度 | 0.7 |
| max_tokens | 最大 token 数 | 500 |

#### 相关文件

| 文件路径 | 说明 |
| ------- | ---- |
| [AIService.java](../../backend/src/main/java/com/xiaohongshu/service/AIService.java) | AI 服务层，调用 MiniMax API |
| [AIController.java](../../backend/src/main/java/com/xiaohongshu/controller/AIController.java) | REST API 控制器 |
| [Post.java](../../backend/src/main/java/com/xiaohongshu/entity/Post.java) | 实体类添加 aiSummary 字段 |
| [ai.js](../../frontend/src/api/ai.js) | 前端 API 封装 |
| [AiSummary.vue](../../frontend/src/components/AiSummary.vue) | AI 总结弹窗组件 |
| [AiLoading.vue](../../frontend/src/components/AiLoading.vue) | AI 加载动画组件 |
| [PostDetailModal.vue](../../frontend/src/components/PostDetailModal.vue) | 帖子详情弹窗（含 AI 入口） |
| [schema.sql](../../backend/src/main/resources/schema.sql) | 数据库表结构 |
| [application.properties](../../backend/src/main/resources/application.properties) | API 配置 |

#### 技术要点

- **API 调用**：使用 Spring RestTemplate 发送 HTTP POST 请求到 MiniMax API
- **提示词工程**：精心设计提示词，引导 AI 生成简洁的小红书风格总结（100字以内）
- **响应解析**：从 MiniMax 返回的 `choices[0].message.content` 提取总结内容
- **缓存策略**：已生成的总结保存到数据库 `posts.ai_summary` 字段，避免重复调用
- **事务处理**：生成总结后直接使用 EntityManager 更新实体，保留当前事务
- **前端交互**：组件优先查询已有总结，无总结时自动触发生成流程

---

## 技术要点总结

### MiniMax API 调用

- 使用 RestTemplate 发送带 Bearer Token 认证的请求
- 请求体包含 model、messages、temperature、max_tokens 等参数
- 响应体为 JSON 格式，通过 `choices[0].message.content` 获取结果

### 前后端协作

- 前端通过 Vite 代理访问后端 API (`/api/ai/*` → `localhost:8080/ai/*`)
- 加载状态使用独立的 AiLoading 组件，统一视觉风格
- 错误处理覆盖网络异常、404、API 失败等多种场景

### 安全与配置

- API 密钥存储在独立配置文件，不提交到 Git
- 数据库字段使用 TEXT 类型，支持长总结内容
- 跨域配置允许前端凭证传递

---

## 工作过程

### 1. 需求分析

收到为帖子添加 AI 总结功能的需求后，首先调研了火山引擎和 MiniMax 两家 AI 服务商，最终选择 MiniMax 因其：
- 中文理解能力强，适合小红书场景
- API 文档清晰，响应格式简单
- 成本适中，适合学生项目

### 2. 后端实现

1. 在 Post 实体类添加 `ai_summary` 字段
2. 创建 AIController 处理两个 API 端点
3. 实现 AIService 封装 MiniMax API 调用
4. 配置 API 密钥和基础 URL

### 3. 前端实现

1. 创建 `ai.js` API 模块封装调用方法
2. 开发 AiSummary 弹窗组件，包含加载状态和错误处理
3. 开发 AiLoading 动画组件，提供三档尺寸
4. 在 PostDetailModal 详情页添加入口按钮

### 4. 调试优化

- 解决 EntityManager 在事务中更新实体的持久化问题
- 调整提示词使生成的总结更符合小红书风格
- 优化加载动画，提升用户体验

### 5. 测试验证

- 验证不同长度内容的总结效果
- 确认已有关闭不会重复生成
- 检查网络异常时的错误提示

---

## 后续优化方向

- 支持流式输出，边生成边展示
- 添加总结生成失败自动重试机制
- 支持用户手动刷新总结
- 记录总结生成次数，便于成本统计
