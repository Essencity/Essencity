# 个人贡献记录 - 监控与可观测性配置

## 贡献者信息

- **用户名**: linzhongyang (lzy11123)
- **日期**: 2026-05-27
- **任务**: 为 Essencity 后端服务添加基础监控和可观测性能力

## 变更概览

| 操作 | 文件 | 说明 |
|------|------|------|
| 修改 | `backend/pom.xml` | 添加 actuator、logstash-logback-encoder 依赖 |
| 修改 | `backend/src/main/resources/application.properties` | 暴露 actuator 端点，配置应用信息 |
| 新增 | `backend/src/main/resources/logback-spring.xml` | Logback 结构化日志配置 |
| 新增 | `backend/src/main/java/com/xiaohongshu/util/LogUtil.java` | 统一日志工具类 |
| 新增 | `backend/src/main/java/com/xiaohongshu/controller/HealthController.java` | 健康检查端点 |
| 新增 | `backend/src/main/java/com/xiaohongshu/config/MetricsFilter.java` | HTTP 请求指标收集过滤器 |
| 新增 | `docs/monitoring.md` | 监控配置说明文档 |

---

## 详细实现

### 1. 依赖管理（pom.xml）

新增两个依赖，均继承 Spring Boot 3.2.1 parent POM 的版本管理：

- **`spring-boot-starter-actuator`** — 提供 health、metrics、info 等运维端点，自动引入 Micrometer 1.12.1
- **`logstash-logback-encoder` 7.3** — 将 Logback 日志输出为 JSON 格式，便于日志采集系统（ELK、Loki 等）解析

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.3</version>
</dependency>
```

### 2. 结构化日志（logback-spring.xml）

配置了三个 Appender：

| Appender | 输出格式 | 用途 |
|----------|----------|------|
| `CONSOLE_PLAIN` | 纯文本（`%d %-5level %logger - %msg`） | 开发环境控制台，可读性优先 |
| `CONSOLE` | JSON（LogstashEncoder） | 生产环境控制台，对接日志采集 |
| `FILE` | JSON（LogstashEncoder） | 写入 `logs/essencity.json`，每天滚动，保留 7 天 |

Profile 策略：
- `default` / `dev` → CONSOLE_PLAIN + FILE
- `prod` → CONSOLE + FILE

额外抑制了 `org.springframework` 和 `org.hibernate` 的日志级别至 WARN，减少噪音。

### 3. 日志工具类（LogUtil.java）

路径：`com.xiaohongshu.util.LogUtil`

| 方法 | 日志格式 | 用途 |
|------|----------|------|
| `getLogger(Class<?>)` | — | 获取 SLF4J Logger 实例 |
| `audit(logger, action, userId, detail)` | `AUDIT \| action={} \| userId={} \| detail={}` | 关键操作审计（登录、发布、删除等） |
| `timing(logger, method, durationMs)` | `TIMING \| method={} \| durationMs={}` | 方法耗时记录 |

类声明为 `final`，构造器私有，仅提供静态方法。

### 4. 健康检查端点（HealthController.java）

- **端点**: `GET /api/health`
- **路径**: `com.xiaohongshu.controller.HealthController`
- **认证**: 无需认证（SecurityConfig 已 permitAll）

**响应字段实现**：

| 字段 | 实现方式 |
|------|----------|
| `status` | 固定返回 `"healthy"` |
| `timestamp` | `Instant.now()` 格式化为 ISO-8601，时区 Asia/Shanghai |
| `app` | 从 `info.app.name` 配置注入 |
| `version` | 从 `info.app.version` 配置注入 |
| `uptime` | `Duration.between(START_TIME, Instant.now())`，格式化为 `"Xh Xm"` / `"Xd Xh Xm"` |
| `database` | 通过 `DataSource.getConnection().isValid(3)` 检查，返回 `"connected"` 或 `"disconnected"` |
| `memory` | 通过 `ManagementFactory.getMemoryMXBean()` 获取堆内存，包含 `used`、`max`、`free` |

```json
{
  "status": "healthy",
  "timestamp": "2026-05-27T10:45:00+08:00",
  "app": "Essencity",
  "version": "0.0.1-SNAPSHOT",
  "uptime": "2h 30m",
  "database": "connected",
  "memory": { "used": "256MB", "max": "1024MB", "free": "768MB" }
}
```

### 5. 请求指标过滤器（MetricsFilter.java）

- **实现**: 继承 `OncePerRequestFilter`，标注 `@Component` + `@ConditionalOnBean(MeterRegistry.class)`
- **排除路径**: `/api/actuator/**`（通过 `shouldNotFilter` 方法）
- **@ConditionalOnBean 作用**: 避免在 `@WebMvcTest` 等测试切片中因缺少 `MeterRegistry` bean 导致上下文加载失败

记录的三个 Micrometer 指标：

| 指标名 | 类型 | 标签 | 触发条件 |
|--------|------|------|----------|
| `http.requests.total` | Counter | method, path, status | 每次请求 |
| `http.requests.duration` | Timer | method, path | 每次请求，单位 ms |
| `http.requests.error` | Counter | method, path, status | 响应状态码 >= 400 |

在 `finally` 块中记录指标，确保异常时也能正确计数。

### 6. Actuator 配置（application.properties）

```properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=always
management.metrics.enable.all=false
management.metrics.enable.jvm=true
management.metrics.enable.http=true
info.app.version=0.0.1-SNAPSHOT
info.app.name=Essencity
```

- 只暴露 health、metrics、info 三个端点（安全最小化原则）
- 健康检查始终展示详情（含组件状态）
- 禁用全量指标，只启用 JVM 和 HTTP 相关指标
- 通过 `info.app.*` 为 `/api/actuator/info` 端点提供应用信息

### 7. 监控文档（docs/monitoring.md）

编写了约 140 行的监控说明文档，覆盖：
- JSON 日志管理和查看命令（jq 过滤示例）
- 健康检查端点 URL、响应示例、字段说明
- Actuator 端点清单和 Micrometer 自定义指标说明
- LogUtil 工具类使用方法
- 可选扩展建议（Sentry 错误追踪、Prometheus 集成、告警规则）

---

## 遇到的问题与解决

### 测试失败：`NoSuchBeanDefinitionException: MeterRegistry`

**现象**: 提交后 `mvn test` 报 68 个错误，所有 `@WebMvcTest` 切片测试 ApplicationContext 加载失败。

**根因**: `MetricsFilter` 标注了 `@Component`，在 `@WebMvcTest` 切片上下文中被自动扫描并加载，但该切片不会自动配置 Micrometer，导致 `MeterRegistry` 类型 bean 缺失。

**解决**: 在 `MetricsFilter` 上添加 `@ConditionalOnBean(MeterRegistry.class)` 注解，使过滤器仅在 MeterRegistry 可用时激活。修复后全部 154 个测试通过。

---

## 验证结果

- `mvn clean compile` — BUILD SUCCESS
- `mvn test` — Tests run: 154, Failures: 0, Errors: 0, Skipped: 0
- 运行时访问:
  - `curl http://localhost:8080/api/health` → 健康状态 JSON
  - `curl http://localhost:8080/api/actuator/metrics` → 指标列表
  - `backend/logs/essencity.json` → 结构化日志文件
