# 监控配置说明

## 日志管理

### JSON 结构化日志

项目使用 Logback + logstash-logback-encoder 输出结构化日志，默认输出到控制台和文件。

- **日志文件位置**: `backend/logs/essencity.json`（当前日期）
- **滚动策略**: 每天滚动，保留最近 7 天
- **日志格式**: JSON（包含 `timestamp`、`level`、`message`、`logger`、`thread`）
- **Profile 区分**:
  - `default` / `dev`: 控制台输出纯文本（可读性好），文件输出 JSON
  - `prod`: 控制台和文件均输出 JSON

### 查看日志

```bash
# 查看最新日志
tail -f backend/logs/essencity.json | jq .

# 按级别过滤
cat backend/logs/essencity.json | jq 'select(.level == "ERROR")'

# 按日志器过滤
cat backend/logs/essencity.json | jq 'select(.logger_name | startswith("com.xiaohongshu"))'
```

---

## 健康检查端点

### URL

```
GET http://localhost:8080/api/health
```

无需认证，可公开访问。

### 响应示例

```json
{
  "status": "healthy",
  "timestamp": "2026-05-27T12:00:00+08:00",
  "app": "Essencity",
  "version": "0.0.1-SNAPSHOT",
  "uptime": "2h 30m",
  "database": "connected",
  "memory": {
    "used": "256MB",
    "max": "1024MB",
    "free": "768MB"
  }
}
```

### 字段说明

| 字段 | 说明 |
|------|------|
| `status` | 服务状态 (`healthy` / `degraded`) |
| `timestamp` | 检查时间（Asia/Shanghai） |
| `app` | 应用名称 |
| `version` | 应用版本 |
| `uptime` | 运行时长 |
| `database` | 数据库连接状态 (`connected` / `disconnected`) |
| `memory` | JVM 堆内存使用情况 |

---

## 指标收集 (Micrometer)

### Actuator 端点

| 端点 | 说明 |
|------|------|
| `/api/actuator/health` | 标准健康检查（含组件详情） |
| `/api/actuator/metrics` | 可用指标列表 |
| `/api/actuator/metrics/{metric}` | 查看具体指标 |
| `/api/actuator/info` | 应用信息 |

### 自定义指标

通过 `MetricsFilter` 自动收集每个 HTTP 请求的指标：

| 指标名 | 类型 | 标签 | 说明 |
|--------|------|------|------|
| `http.requests.total` | Counter | method, path, status | 请求总数 |
| `http.requests.duration` | Timer | method, path | 响应时间 |
| `http.requests.error` | Counter | method, path, status | 错误请求数 (status >= 400) |

### 查看指标示例

```bash
# 查看所有可用指标
curl http://localhost:8080/api/actuator/metrics

# 查看请求总数
curl http://localhost:8080/api/actuator/metrics/http.requests.total

# 查看 JVM 内存
curl http://localhost:8080/api/actuator/metrics/jvm.memory.used
```

---

## LogUtil 工具类

位于 `com.xiaohongshu.util.LogUtil`，提供统一的结构化日志输出入口：

- `LogUtil.getLogger(Class)` - 获取 Logger 实例
- `LogUtil.audit(logger, action, userId, detail)` - 记录审计日志
- `LogUtil.timing(logger, method, durationMs)` - 记录耗时日志

---

## 可选扩展

### Sentry 错误追踪

添加依赖后可在 `application.properties` 中配置 DSN：

```xml
<dependency>
    <groupId>io.sentry</groupId>
    <artifactId>sentry-spring-boot-starter</artifactId>
    <version>7.x</version>
</dependency>
```

### 告警建议

- **4xx 错误率突增**: 可能表明 API 变更不兼容或恶意扫描
- **5xx 错误**: 需立即关注，通常为服务内部异常
- **数据库断连**: 检查 MySQL 服务及网络连接
- **堆内存使用 > 80%**: 可能存在内存泄漏，需分析 dump

### Prometheus 集成

添加 `micrometer-registry-prometheus` 依赖并暴露 `/api/actuator/prometheus` 端点即可对接 Prometheus + Grafana。
