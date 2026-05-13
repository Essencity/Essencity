# Essencity Docker 容器化部署指南

## 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- MySQL 8.0（宿主机或容器运行均可）

## 目录结构

```
Essencity/
├── docker-compose.yml          # 容器编排配置
├── backend/
│   ├── Dockerfile              # 后端构建配置
│   └── ...
├── frontend/
│   ├── Dockerfile              # 前端构建配置
│   ├── nginx.conf              # Nginx 反向代理配置
│   └── ...
└── docs/                       # 项目文档
```

## 快速开始

### 1. 配置数据库

确保宿主机上 MySQL 服务运行正常，并创建数据库：

```sql
CREATE DATABASE xiaohongshu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 修改数据库配置

编辑 `docker-compose.yml` 中的数据库连接信息：

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://host.docker.internal:3306/xiaohongshu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  SPRING_DATASOURCE_USERNAME: root
  SPRING_DATASOURCE_PASSWORD: your_password   # 修改为你的密码
```

> **Windows**: `host.docker.internal` 可直接访问宿主机数据库
>
> **Linux**: 需替换为宿主机实际 IP 或使用网络模式 `network_mode: host`

### 3. 敏感配置（可选）

如需使用 AI 功能，创建 `backend/src/main/resources/application-secrets.properties`：

```properties
jwt.secret=your-256-bit-secret-key-here
minimax.api-key=your-minimax-api-key
minimax.group-id=your-minimax-group-id
```

### 4. 构建并启动

```bash
# 构建镜像并启动容器
docker-compose up -d --build

# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

### 5. 访问服务

- 前端：http://localhost
- 后端 API：http://localhost/api
- Swagger 文档：http://localhost/api/swagger-ui.html

## 容器说明

| 容器名 | 镜像 | 端口 | 说明 |
|--------|------|------|------|
| essencity-backend | maven:3.9-eclipse-temurin-21 | 8080 | Spring Boot 后端服务 |
| essencity-frontend | nginx:alpine | 80 | Vue 3 前端 + Nginx 反向代理 |

## 前端 Nginx 配置

`frontend/nginx.conf` 配置了反向代理：

- `/api` → 后端 `http://backend:8080`
- `/uploads` → 后端 `http://backend:8080`（文件访问）

> 注意：容器间通信依赖 Docker 网络，`backend` 为主机名。

## 数据持久化

上传文件存储在 Docker volume 中：

```bash
# 查看 volume
docker volume ls | grep essencity

# 备份上传文件
docker run --rm -v essencity_uploads:/data -v $(pwd):/backup alpine tar czf /backup/uploads-backup.tar.gz /data
```

## 常见问题

### 1. 容器内无法连接数据库

- 检查宿主机 MySQL 是否允许远程连接
- 确认防火墙开放 3306 端口
- Linux 环境将 `host.docker.internal` 替换为宿主机 IP

### 2. 文件上传失败

- 检查 volume 挂载是否正确
- 确认 `/app/uploads` 目录权限

### 3. 前端资源加载异常

- 确认 nginx.conf 中 `proxy_pass` 地址正确
- 检查容器网络是否互通

## 停止服务

```bash
# 停止并移除容器
docker-compose down

# 同时移除镜像
docker-compose down --rmi local
```

## 生产环境建议

1. 使用 Docker Secret 存储敏感信息
2. 配置 Nginx SSL/TLS 证书
3. 使用 Docker Swarm 或 Kubernetes 进行编排
4. 配置日志收集（ELK/Loki）
5. 使用私有镜像仓库管理镜像
