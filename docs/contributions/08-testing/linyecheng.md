# 林烨澄 - 后端 Controller 层测试贡献说明

## 基本信息

- **姓名**: 林烨澄
- **日期**: 2026-04-28

---

## 贡献概述

本次贡献为后端 **Controller 层实现了完整的单元测试覆盖**，将 Controller 层测试覆盖率从 **0% 提升至 91%**（行覆盖率），新增 **75 个测试用例**，覆盖全部 5 个 Controller、38 个 API 端点。

---

## 贡献内容

### 1. NotificationControllerTest（2 个测试，100% 行覆盖率）

**测试文件**：`backend/src/test/java/com/xiaohongshu/controller/NotificationControllerTest.java`

**技术方案**：`@WebMvcTest` + `@AutoConfigureMockMvc(addFilters = false)` + `@MockBean NotificationService`

| 测试用例                                     | 覆盖场景     |
| ---------------------------------------- | -------- |
| `getNotifications_ShouldReturnList`      | 正常返回通知列表 |
| `getNotifications_ShouldReturnEmptyList` | 返回空列表    |

### 2. FileControllerTest（7 个测试，72.1% 行覆盖率）

**测试文件**：`backend/src/test/java/com/xiaohongshu/controller/FileControllerTest.java`

**技术方案**：`@WebMvcTest` + 无 MockBean（纯文件系统操作），使用真实文件系统测试下载/上传逻辑。

| 测试用例                                                  | 覆盖场景                      |
| ----------------------------------------------------- | ------------------------- |
| `test_ShouldReturnWorkingMessage`                     | 健康检查端点                    |
| `debugPath_ShouldReturnPathInfo`                      | 调试路径端点                    |
| `downloadFile_FileNotFound_ShouldReturn404`           | 文件不存在                     |
| `downloadFile_FileExists_ShouldReturnWithContentType` | 下载 JPG 文件，验证 Content-Type |
| `downloadFile_PngFile_ShouldReturnPngContentType`     | 下载 PNG 文件，验证 Content-Type |
| `uploadFile_ShouldReturnUrl`                          | 上传文件返回 URL                |
| `uploadFile_EmptyFile_ShouldReturn400`                | 上传空文件返回 400               |

### 3. AuthControllerTest（23 个测试，90.7% 行覆盖率）

**测试文件**：`backend/src/test/java/com/xiaohongshu/controller/AuthControllerTest.java`

**技术方案**：`@WebMvcTest` + `@MockBean UserService`，覆盖全部 10 个端点的成功和异常路径。

| 测试类别 | 测试数 | 覆盖端点                                           |
| ---- | --- | ---------------------------------------------- |
| 注册   | 3   | `POST /auth/register` — 成功、默认头像、重复用户名          |
| 登录   | 3   | `POST /auth/login` — 成功、错误密码、用户不存在             |
| 更新资料 | 3   | `PUT /auth/profile` — 成功、部分更新、用户不存在            |
| 关注   | 2   | `POST /auth/follow` — 成功、自己关注自己                |
| 取关   | 2   | `POST /auth/unfollow` — 成功、异常处理                |
| 关注状态 | 3   | `GET /auth/following-status` — true/false/异常降级 |
| 粉丝列表 | 3   | `GET /auth/followers/{id}` — 有结果/空/异常降级        |
| 关注列表 | 1   | `GET /auth/following/{id}` — 正常返回              |
| 粉丝数  | 2   | `GET /auth/followers-count/{id}` — 有结果/异常降级    |
| 关注数  | 1   | `GET /auth/following-count/{id}` — 正常返回        |

### 4. AIControllerTest（6 个测试，100% 行覆盖率）

**测试文件**：`backend/src/test/java/com/xiaohongshu/controller/AIControllerTest.java`

**技术方案**：由于 `AIController` 使用 `@PersistenceContext EntityManager`，而 `@WebMvcTest` 不加载 JPA 上下文，采用 **独立 MockMvc 配置**（`MockMvcBuilders.standaloneSetup` + `MockitoExtension`），手动注入全部依赖。

| 测试用例                                                       | 覆盖场景       |
| ---------------------------------------------------------- | ---------- |
| `getAiSummary_ExistingSummary_ShouldReturnIt`              | 获取已有 AI 摘要 |
| `getAiSummary_NoSummary_ShouldReturnEmptyString`           | 摘要为空的降级处理  |
| `getAiSummary_PostNotFound_ShouldReturn404`                | 帖子不存在      |
| `generateAiSummary_Success`                                | 生成 AI 摘要成功 |
| `generateAiSummary_PostNotFound_ShouldReturn404`           | 目标帖子不存在    |
| `generateAiSummary_ServiceThrowsException_ShouldReturn400` | AI 服务异常    |

### 5. PostControllerTest（37 个测试，94.8% 行覆盖率）

**测试文件**：`backend/src/test/java/com/xiaohongshu/controller/PostControllerTest.java`

**技术方案**：`@WebMvcTest` + `@MockBean PostService, UserService`，覆盖全部 18 个端点和 4 种帖子列表过滤模式。

| 测试类别    | 测试数 | 覆盖端点                                                        |
| ------- | --- | ----------------------------------------------------------- |
| 帖子列表    | 7   | `GET /posts` — 全部/按标签/按标题/标签+标题/"推荐"标签回退/空列表/视频类型           |
| 帖子详情    | 3   | `GET /posts/{id}` — 成功/404/异常                               |
| 删除帖子    | 2   | `DELETE /posts/{id}` — 成功/异常                                |
| 创建帖子    | 2   | `POST /posts` — 成功/异常                                       |
| 更新帖子    | 2   | `PUT /posts/{id}` — 成功/异常                                   |
| 文件上传    | 1   | `POST /posts/upload` — 成功                                   |
| 点赞状态/切换 | 4   | `GET /like/status` + `POST /like` + `POST /unlike` + 异常     |
| 收藏状态/切换 | 3   | `GET /collect/status` + `POST /collect` + `POST /uncollect` |
| 评论      | 6   | `GET /comments` + `POST /comments`（含回复）+ `DELETE /comments` |
| 用户统计    | 1   | `GET /user/{id}/stats`                                      |
| 用户收藏    | 2   | `GET /user/{id}/collections` — 有结果/空                        |
| 用户点赞    | 1   | `GET /user/{id}/likes`                                      |

### 6. JaCoCo 配置优化

**修改文件**：`backend/pom.xml`

- 新增 `com.xiaohongshu.controller.*` 包的覆盖率检查规则（最低 60% 行覆盖率）
- 修复 JaCoCo 在中文路径下 exec 文件无法生成的问题（`destFile` 重定向至 `${java.io.tmpdir}`）

---

## 技术要点

### @WebMvcTest 切片测试

采用 Spring Boot 的 `@WebMvcTest` 注解进行切片测试，只加载 Controller 层相关 Bean，不启动完整 Spring 上下文：

```java
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)  // 跳过 Security 过滤器
@ActiveProfiles("test")                      // 加载 H2 测试配置
class PostControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private PostService postService;
    @MockBean private UserService userService;
}
```

### 独立 MockMvc 配置（AIController）

当 Controller 包含 `@PersistenceContext` 注解时，`@WebMvcTest` 无法加载 JPA 上下文，改用独立模式：

```java
@ExtendWith(MockitoExtension.class)
class AIControllerTest {
    @Mock private AIService aiService;
    // ...

    @BeforeEach
    void setUp() {
        AIController controller = new AIController();
        ReflectionTestUtils.setField(controller, "aiService", aiService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
}
```

### 四种过滤模式覆盖（PostController.getAllPosts）

```java
// 无参数 → getAllPosts()
// 仅 tag → getPostsByTag()
// 仅 title → searchPosts()
// tag + title → searchPostsByTag()
// tag = "推荐" → 回退到 getAllPosts()
```

### 文件上传测试

```java
MockMultipartFile file = new MockMultipartFile(
    "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

mockMvc.perform(multipart("/posts/upload").file(file))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.url").value(startsWith("/uploads/")));
```

---

## 测试覆盖率

### Controller 层覆盖率

| Controller             | 已覆盖行    | 总行数     | 覆盖率       | 测试数    |
| ---------------------- | ------- | ------- | --------- | ------ |
| NotificationController | 2       | 2       | 100%      | 2      |
| AIController           | 18      | 18      | 100%      | 6      |
| PostController         | 163     | 172     | 94.8%     | 37     |
| AuthController         | 49      | 54      | 90.7%     | 23     |
| FileController         | 31      | 43      | 72.1%     | 7      |
| **合计**                 | **263** | **289** | **91.0%** | **75** |

### 全项目测试统计

| 层级           | 测试数        | 覆盖率         |
| ------------ | ---------- | ----------- |
| Service 层    | 80         | 70%+        |
| Controller 层 | **75**（新增） | **91%**（新增） |
| **合计**       | **155**    | —           |

---

## 相关文件

| 文件路径                                                                               | 说明                                   |
| ---------------------------------------------------------------------------------- | ------------------------------------ |
| `backend/src/test/java/com/xiaohongshu/controller/NotificationControllerTest.java` | 新增：通知接口测试                            |
| `backend/src/test/java/com/xiaohongshu/controller/FileControllerTest.java`         | 新增：文件接口测试                            |
| `backend/src/test/java/com/xiaohongshu/controller/AuthControllerTest.java`         | 新增：认证接口测试                            |
| `backend/src/test/java/com/xiaohongshu/controller/AIControllerTest.java`           | 新增：AI 接口测试                           |
| `backend/src/test/java/com/xiaohongshu/controller/PostControllerTest.java`         | 新增：帖子接口测试                            |
| `backend/pom.xml`                                                                  | 修改：新增 Controller 覆盖率规则 + JaCoCo 路径修复 |
| `backend/src/test/resources/application-test.properties`                           | 修改：新增 `server.servlet.context-path`  |

---

## 遇到的问题和解决

### 问题一：Spring Security 过滤器导致 401/403

**现象**：MockMvc 请求返回 401 或 403 状态码，而非预期的 200/400/404。

**排查**：`@WebMvcTest` 会自动加载 `SecurityFilterChain`，但不会自动加载项目自定义的 `SecurityConfig`（`permitAll()` 配置未能生效）。

**解决**：在所有 `@WebMvcTest` 测试类上添加 `@AutoConfigureMockMvc(addFilters = false)`，跳过 Security 过滤器。由于 Controller 测试目标为 HTTP 层逻辑而非安全配置，此方案不影响测试有效性。

### 问题二：AIController 的 @PersistenceContext 导致 ApplicationContext 加载失败

**现象**：`AIControllerTest` 启动时报 `Failed to load ApplicationContext`，根因是 `@WebMvcTest` 不包含 JPA 自动配置，无法创建 `EntityManager` Bean。

**排查**：即使使用了 `@MockBean EntityManager`，`@PersistenceContext` 注解仍需 JPA 基础设施支持。

**解决**：放弃 `@WebMvcTest`，改用 `MockMvcBuilders.standaloneSetup` 独立模式 + `MockitoExtension`，通过 `ReflectionTestUtils` 手动注入全部依赖。轻量且无需 Spring 上下文。

### 问题三：JaCoCo exec 文件在中文路径下无法生成

**现象**：`mvn clean test` 后执行 `jacoco:report` 报 "missing execution data file"。

**排查**：JaCoCo 通过 `-javaagent` 参数传递 `destfile` 路径，JVM 在解析含中文字符的命令行参数时编码异常，导致 agent 无法写入 exec 文件。

**解决**：将 `destFile` 配置为系统临时目录（`${java.io.tmpdir}/jacoco-essencity.exec`），路径仅含 ASCII 字符。`report` 和 `check` 目标同步配置 `dataFile` 路径。

```xml
<destFile>${java.io.tmpdir}/jacoco-essencity.exec</destFile>
```

---

## 心得体会

1. **切片测试的适用边界**：`@WebMvcTest` 适合纯 Web 层测试，但当 Controller 依赖 JPA 注解（如 `@PersistenceContext`）时需退回到更灵活的方案。理解每种测试方案的能力边界是选型关键。

2. **Mock 粒度决定测试价值**：Service 层的 Mock 确保了 Controller 测试聚焦于 HTTP 层逻辑（参数解析、路径映射、响应格式），而非重复验证业务逻辑。

3. **覆盖率是手段不是目的**：91% 的行覆盖率不代表 100% 的正确性。缺少的 9% 主要集中在 FileController 的分支未覆盖和部分异常路径，应在后续迭代中补充。

4. **中文路径是 Java 生态常见陷阱**：Maven 插件（JaCoCo、Surefire）的 JVM 参数传递对中文路径兼容性差，建议项目路径始终使用纯 ASCII 字符。
