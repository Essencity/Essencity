# 林烨澄 - Docker 容器化部署贡献说明

## 基本信息

- **姓名**: 林烨澄
- **学号**: 2412190630
- **日期**: 2026-03-31

---

## 贡献内容

### 1. Docker 环境配置文件创建

创建了完整的 Docker 容器化部署配置，包括：

**docker-compose.yml**

- 定义了 `backend` 和 `frontend` 两个服务
- 后端使用 Spring Boot 镜像，连接本地 MySQL
- 前端使用 Nginx 运行 Vue 生产版本
- 配置了 uploads 数据卷持久化上传文件

**backend/Dockerfile**

- 使用 Maven 3.9 + Eclipse Temurin 21 构建
- 多阶段构建：builder 阶段编译打包，runtime 阶段运行
- 优化构建体积，只保留 JRE 运行时

**frontend/Dockerfile**

- 使用 Node 20 Alpine 构建 Vue 应用
- 使用 Nginx Alpine 运行生产版本
- 复制 nginx.conf 配置代理

**frontend/nginx.conf**

- 配置 `/api` 反向代理到后端 `http://backend:8080`
- 支持前端路由的 SPA 回退
- 配置静态资源缓存

**backend/.dockerignore**

- 排除 target、.git、.env 等不必要的文件
- 减少构建上下文体积

### 2. 镜像加速与拉取

由于网络环境无法直接访问 Docker Hub，配置了 DaoCloud 镜像加速：

```bash
docker pull docker.m.daocloud.io/library/mysql:5.7
docker pull docker.m.daocloud.io/library/maven:3.9-eclipse-temurin-21
docker pull docker.m.daocloud.io/library/eclipse-temurin:21-jre-alpine
docker pull docker.m.daocloud.io/library/nginx:alpine
docker pull docker.m.daocloud.io/library/node:20-alpine
```

拉取完成后重命名为标准镜像标签：

```bash
docker tag docker.m.daocloud.io/library/xxx library/xxx
docker rmi docker.m.daocloud.io/library/xxx
```

---

## 技术要点

### 多阶段构建

```dockerfile
# builder 阶段
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# runtime 阶段
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Nginx 反向代理

```nginx
location /api {
    proxy_pass http://backend:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```

### Docker 网络配置

- 使用 `host.docker.internal` 连接本地 MySQL
- 容器间通过服务名通信（backend、mysql）
- 端口映射：8080（后端）、80（前端）、3306（MySQL）

---

## 相关文件

| 文件路径                        | 说明           |
| ----------------------------- | ------------ |
| docker-compose.yml            | 容器编排配置      |
| backend/Dockerfile            | 后端 Docker 构建文件 |
| frontend/Dockerfile           | 前端 Docker 构建文件 |
| frontend/nginx.conf           | Nginx 配置     |
| backend/.dockerignore         | 后端构建忽略文件   |
| frontend/.dockerignore        | 前端构建忽略文件   |

---

## 部署步骤

### 1. 拉取镜像（使用镜像加速）

```bash
docker pull docker.m.daocloud.io/library/mysql:5.7
docker pull docker.m.daocloud.io/library/maven:3.9-eclipse-temurin-21
docker pull docker.m.daocloud.io/library/eclipse-temurin:21-jre-alpine
docker pull docker.m.daocloud.io/library/nginx:alpine
docker pull docker.m.daocloud.io/library/node:20-alpine
```

### 2. 重命名镜像

```bash
docker tag docker.m.daocloud.io/library/mysql:5.7 library/mysql:5.7
docker tag docker.m.daocloud.io/library/maven:3.9-eclipse-temurin-21 library/maven:3.9-eclipse-temurin-21
docker tag docker.m.daocloud.io/library/eclipse-temurin:21-jre-alpine library/eclipse-temurin:21-jre-alpine
docker tag docker.m.daocloud.io/library/nginx:alpine library/nginx:alpine
docker tag docker.m.daocloud.io/library/node:20-alpine library/node:20-alpine
```

### 3. 启动服务

```bash
docker-compose up -d --build
```

### 4. 验证服务

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

---

## 服务地址

- 前端：http://localhost
- 后端 API：http://localhost:8080/api
- MySQL：host.docker.internal:3306（本地 MySQL）

---

## 遇到的问题和解决

### 问题一：Docker Hub 访问超时

**现象**：网络环境无法直接访问 `registry-1.docker.io`，拉取镜像超时。

**解决**：使用 DaoCloud 镜像加速服务 `docker.m.daocloud.io`，该镜像源在国内访问速度更快。

### 问题二：MySQL 容器与本地数据库冲突

**现象**：用户本地已安装 MySQL 5.7，无需在 Docker 中再运行一个 MySQL 容器。

**解决**：修改 docker-compose.yml，移除 MySQL 服务，改用 `host.docker.internal:3306` 连接本地 MySQL。

### 问题三：数据库密码不匹配

**现象**：本地 MySQL 密码是 `lyc`，而非默认的 `root`。

**解决**：更新 docker-compose.yml 中的环境变量：

```yaml
SPRING_DATASOURCE_USERNAME: root
SPRING_DATASOURCE_PASSWORD: lyc
```

### 问题四：前端 API 代理配置

**现象**：Vue 应用在 Docker 中运行时，无法访问后端 API。

**解决**：配置 Nginx 反向代理，将 `/api` 请求转发到后端服务：

```nginx
location /api {
    proxy_pass http://backend:8080;
}
```

---

## 心得体会

1. **镜像加速的必要性**：在国内网络环境下，直接访问 Docker Hub 通常不稳定，使用镜像加速服务是必需的。

2. **多阶段构建优化**：通过多阶段构建，可以显著减小最终镜像体积，只保留运行时必要的文件和依赖。

3. **数据持久化**：使用 Docker volumes 持久化上传文件，避免容器删除后数据丢失。

4. **本地开发与 Docker 结合**：保留本地 MySQL 开发环境，Docker 只用于运行后端和前端，可以提高开发效率。
