# 个人贡献记录 - 监控配置

## 贡献者信息

- **用户名**: linzhongyang (lzy11123)
- **日期**: 2026-05-27

## 贡献内容

### 1. 新增依赖
- 在 `pom.xml` 中添加 `spring-boot-starter-actuator` 和 `logstash-logback-encoder` 7.3 依赖

### 2. 结构化日志配置
- 新增 `logback-spring.xml`，配置 JSON 格式日志输出
- 支持 Console + File 双输出，滚动策略保留 7 天
- 开发环境控制台输出可读文本，生产环境输出 JSON

### 3. 日志工具类
- 新增 `LogUtil.java`，封装统一的结构化日志输出方法
- 提供 `audit()` 审计日志和 `timing()` 耗时日志工具方法

### 4. 健康检查端点
- 新增 `HealthController.java`，提供 `GET /api/health` 端点
- 返回服务状态、运行时长、数据库连接状态、内存使用等信息
- 无需认证，便于负载均衡和监控系统使用

### 5. 请求指标收集
- 新增 `MetricsFilter.java`，基于 Micrometer 收集 HTTP 请求指标
- 记录请求计数、响应时间、错误计数，按 method/path/status 打标签
- 自动排除 `/api/actuator/**` 端点避免自监控

### 6. Actuator 配置
- 在 `application.properties` 中暴露 `health`、`metrics`、`info` 端点
- 启用 JVM 指标和 HTTP 指标收集

### 7. 监控文档
- 新增 `docs/monitoring.md`，说明日志查看、健康检查、指标收集的使用方法
- 包含 Sentry 集成和 Prometheus 对接的扩展建议
