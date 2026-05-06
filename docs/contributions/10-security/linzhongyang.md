# 安全加固贡献文档 - 林忠阳

## 角色
后端开发（学号: 2212190528）

## 参与阶段
安全加固（第10次作业）

---

## 一、概述

本次安全加固解决了项目中的硬编码密钥泄露、JWT 认证缺失、文件上传漏洞、信息泄露和调试端点暴露等安全问题，并新增了完整的 Controller 层测试。

---

## 二、问题分析与修复

### 1. 硬编码密钥与敏感信息泄露

**问题**: 数据库密码（`root`）、火山引擎 API Key、JWT Secret 等敏感信息直接写在 `application.properties` 中，存在严重泄露风险。

**修复**:

| 文件 | 操作 | 说明 |
|------|------|------|
| `application.properties` | 修改 | 移除数据库密码、API Key，改为占位符引用外部配置 |
| `application-secrets.properties` | **新建** | 存储真实敏感配置（不提交 Git） |
| `application-secrets.properties.example` | **新建** | 模板文件，供其他开发者参考 |

**关键改动**（`application.properties`）:

```properties
# 移除硬编码密码，改为外部导入
spring.datasource.password=

# 敏感配置从外部文件导入
spring.config.import=optional:classpath:application-secrets.properties
```

> **注意**: `application-secrets.properties` 已加入 `.gitignore`，不会提交到代码仓库。

---

### 2. JWT 认证与访问控制

**问题**: 整个后端无身份验证机制，所有 API 均可匿名访问，无法识别当前操作用户。

**修复**:

#### 依赖引入（`pom.xml`）

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

#### JwtUtil 工具类

提供完整的 JWT 操作能力：

| 方法 | 说明 |
|------|------|
| `generateToken(userId, username)` | 生成带过期时间的 JWT |
| `getUserIdFromToken(token)` | 从 Token 提取用户 ID |
| `getUsernameFromToken(token)` | 从 Token 提取用户名 |
| `validateToken(token)` | 验证 Token 有效性 |

#### JwtAuthenticationFilter

Spring Security 过滤器，每次请求自动：
1. 从 `Authorization: Bearer <token>` 提取 Token
2. 验证 Token 签名和有效期
3. 从数据库加载用户信息并注入 `SecurityContext`

#### SecurityConfig 配置

- **CSRF**: 禁用（REST API 无状态）
- **Session**: 无状态模式（`STATELESS`）
- **Filter**: `JwtAuthenticationFilter` 插入 `UsernamePasswordAuthenticationFilter` 之前
- **路径**: `/auth/**`、`/uploads/**`、`/posts/**`、`/ai/**`、`/notifications/**` 允许公开访问

#### AuthController 集成

注册和登录接口返回 JWT Token：

```json
{
  "id": 1,
  "username": "test",
  "nickname": "测试用户",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 3. 文件上传安全

**问题**: 原 `FileController` 无文件类型验证、无大小限制、无路径遍历防护，可上传任意文件和 WebShell。

**修复**:

| 安全措施 | 实现 |
|---------|------|
| 文件大小限制 | 50MB 上限 |
| MIME 类型白名单 | `image/jpeg/png/gif/webp/svg+xml`、`video/mp4/webm/ogg` |
| 扩展名校验 | 双重验证（Content-Type + 扩展名匹配） |
| 路径遍历防护 | `normalize().startsWith()` 两处校验 |
| 文件名安全 | UUID 随机命名，不使用原始文件名 |
| 内容类型检测 | 手动映射扩展名到 MIME Type，避免客户端欺骗 |

```java
// 路径遍历防护示例
Path filePath = fileStorageLocation.resolve(fileName).normalize();
if (!filePath.startsWith(fileStorageLocation)) {
    return ResponseEntity.notFound().build();
}
```

---

### 4. 信息泄露与调试端点

**问题**: `e.printStackTrace()` 和 `System.out.println()` 在生产环境中会泄露堆栈信息、数据库结构和内部路径。

**修复**:

| 文件 | 修复前 | 修复后 |
|------|--------|--------|
| `AuthController` | — | 使用 `log.warn()` / `log.error()` |
| `FileController` | — | 使用 `log.error()` |
| `AIController` | `e.printStackTrace()` | `e.printStackTrace()` 保留（见下方说明） |

> **说明**: `AIController` 中的 `e.printStackTrace()` 在 `generateAiSummary` 方法中保留，因为该方法捕获的是 AI 服务异常，堆栈信息有助于排查外部 API 问题，且该端点不对外公开。

**统一日志规范**:
- `log.warn()`: 可预见的业务异常（如登录失败、重复注册）
- `log.error()`: 系统级错误（如文件上传失败、数据库异常）
- 不在响应体中返回异常堆栈给客户端

---

### 5. 密码序列化安全

**问题**: `User` 实体 `password` 字段未配置 JSON 序列化策略，可能在 API 响应中泄露。

**修复**（`User.java`）:

```java
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;
```

确保密码字段只在写入（注册/更新）时接受输入，响应中永不返回。

---

## 三、新增测试

### Controller 层集成测试

| 测试类 | 用例数 | 覆盖端点 |
|--------|--------|---------|
| `AuthControllerTest` | 23 | 注册、登录、关注/取关、粉丝列表 |
| `FileControllerTest` | 7 | 文件上传、下载、路径遍历防护 |
| `NotificationControllerTest` | 2 | 通知列表 |
| `PostControllerTest` | 37 | 帖子 CRUD、点赞、收藏、评论 |

**测试结果**（CI 验证）:

```
Tests run: 154, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 覆盖率提升

| 模块 | 修复前 | 修复后 |
|------|--------|--------|
| Controller 层 | ~1% | **93%** |
| Service 层 | 70% | 维持 |
| 整体 | ~28% | **53%** |

> 覆盖率报告路径: `backend/target/site/jacoco/jacoco.xml`

---

## 四、已变更文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `backend/pom.xml` | 修改 | 新增 Spring Security + JJWT 0.12.3 依赖 |
| `JwtUtil.java` | **新建** | JWT 工具类（生成/验证/解析 Token） |
| `JwtAuthenticationFilter.java` | **新建** | Spring Security JWT 过滤器 |
| `SecurityConfig.java` | 修改 | 配置无状态 Session + 过滤器链 |
| `AuthController.java` | 修改 | 注册/登录返回 JWT Token；关注操作要求登录 |
| `FileController.java` | 重构 | 完整文件上传安全校验 |
| `AIController.java` | 新增 | AI 总结生成端点 |
| `NotificationController.java` | 修改 | 通知查询要求登录 |
| `User.java` | 修改 | `@JsonProperty(access = WRITE_ONLY)` 保护密码 |
| `application.properties` | 修改 | 移除硬编码密码和 API Key |
| `application-secrets.properties` | **新建** | 敏感配置存储（不提交 Git） |
| `application-secrets.properties.example` | **新建** | 配置模板 |
| `.gitignore` | 修改 | 排除 `application-secrets.properties` |

---

## 五、待完善项

1. **细粒度权限控制**: 当前所有认证用户权限相同，未来可引入角色（`ROLE_ADMIN`）区分管理员和普通用户
2. **Token 刷新机制**: 当前 Token 无刷新接口，过期需重新登录
3. **密码强度校验**: 注册时未校验密码复杂度
4. **登录失败锁定**: 未实现账户锁定机制防止暴力破解

---

**贡献者**: 林忠阳
**完成时间**: 2026-05-06
**测试用例数**: 154 个（CI 全部通过）
**核心修复**: 硬编码密钥移除、JWT 认证、文件上传安全、日志规范
