backend.md
==========

Essencity 后端说明文档
================

> 项目：Essencity  
> 技术栈：Spring Boot 3 + Spring Data JPA + MySQL 8.0  
> 文档版本：v1.0
> 日期：2026-03-11
> 项目周期：15周

* * *

1.后端系统概述
=========

Essencity 后端基于 **Spring Boot 3** 构建，采用典型的 **分层架构（Controller → Service → Repository）**，通过 RESTful API 为前端提供服务。

后端主要负责：

* 用户认证与权限控制

* 笔记、评论、话题等业务逻辑处理

* 数据持久化管理

* 图片上传与管理

* AI 总结功能调用

* API 文档生成

* 异常处理与日志记录

系统采用 **JWT 无状态认证机制**，通过 **Spring Security + JWT Filter** 实现接口安全控制。

* * *

2.技术栈
======

| 类别     | 技术                | 说明          |
| ------ | ----------------- | ----------- |
| 框架     | Spring Boot 3     | 后端核心框架      |
| ORM    | Spring Data JPA   | 数据访问层       |
| 数据库    | MySQL 8.0         | 主数据存储       |
| 安全     | Spring Security   | 权限控制        |
| 认证     | JWT               | Token 身份认证  |
| 加密     | BCrypt            | 密码加密        |
| API 文档 | Swagger / Knife4j | 自动生成 API 文档 |
| 日志     | Logback           | 日志记录        |
| 构建工具   | Maven             | 项目构建        |

* * *

3.后端架构设计
=========

系统采用经典 **三层架构 + 安全模块**：
    Controller
        │
        ▼
    Service
        │
        ▼
    Repository (JPA)
        │
        ▼
    MySQL

同时包含：
    Security Module
    Global Exception Handler
    File Upload Service
    AI Service

整体架构：
    Client (Vue)
         │
         ▼
    REST API
         │
         ▼
    Spring Boot
     ├─ Controller
     ├─ Service
     ├─ Repository
     ├─ Security
     ├─ Exception
     └─ Util
         │
         ▼
    MySQL

* * *

4.项目目录结构
=========

后端核心目录：
    essencity-backend
    ├── config
    ├── controller
    ├── service
    ├── repository
    ├── entity
    ├── dto
    ├── security
    ├── exception
    ├── common
    ├── util
    └── resources

### 4.1 config

配置类：

| 类              | 说明                 |
| -------------- | ------------------ |
| SecurityConfig | Spring Security 配置 |
| WebConfig      | Web 配置             |
| SwaggerConfig  | Swagger 文档配置       |
| JwtConfig      | JWT 参数配置           |

* * *

### 4.2 controller

控制器层负责：

* 接收 HTTP 请求

* 参数校验

* 调用 Service

* 返回统一响应格式

主要控制器：

| Controller        | 功能    |
| ----------------- | ----- |
| UserController    | 用户模块  |
| NoteController    | 笔记模块  |
| CommentController | 评论模块  |
| TopicController   | 话题模块  |
| SearchController  | 搜索模块  |
| DraftController   | 草稿模块  |
| AiController      | AI 总结 |

* * *

### 4.3 service

Service 层负责：

* 业务逻辑处理

* 数据校验

* 调用 Repository

* 调用外部服务

核心服务：

| Service        | 功能    |
| -------------- | ----- |
| UserService    | 用户业务  |
| NoteService    | 笔记业务  |
| CommentService | 评论业务  |
| TopicService   | 话题业务  |
| SearchService  | 搜索业务  |
| DraftService   | 草稿业务  |
| FileService    | 文件上传  |
| AiService      | AI 总结 |

* * *

### 4.4 repository

Repository 层负责数据库操作。

基于 **Spring Data JPA** 实现。

示例：
    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);
    }

特点：

* 自动生成 CRUD

* 支持方法命名查询

* 支持分页查询

* * *

### 4.5 entity

实体类用于映射数据库表。

示例：
    @Entity
    @Table(name = "user")
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String username;
        private String password;
    }

实体与数据库表一一对应。

* * *

### 4.6 dto

DTO 用于接口数据传输。

分为：
    dto/
     ├── request
     └── response

作用：

* 避免直接暴露 Entity

* 控制接口字段

* 简化参数结构

* * *

### 4.7 common

公共模块：

| 类          | 说明     |
| ---------- | ------ |
| Result     | 统一响应结构 |
| ResultCode | 响应码    |
| PageResult | 分页返回   |

统一返回格式：
    {
      "code": 200,
      "message": "success",
      "data": {}
    }

* * *

### 4.8 exception

异常处理模块。

包含：

| 类                      | 说明     |
| ---------------------- | ------ |
| BusinessException      | 业务异常   |
| GlobalExceptionHandler | 全局异常处理 |

示例：
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        return Result.error("服务器错误");
    }

* * *

### 4.9 security

安全模块基于 **Spring Security + JWT**。

核心组件：

| 类                       | 说明          |
| ----------------------- | ----------- |
| JwtTokenProvider        | Token 生成与解析 |
| JwtAuthenticationFilter | JWT 过滤器     |
| UserDetailsServiceImpl  | 用户认证        |

认证流程：
    登录
     ↓
    验证用户名密码
     ↓
    生成 JWT Token
     ↓
    客户端保存 Token
     ↓
    请求携带 Authorization Header
     ↓
    JWT Filter 验证
     ↓
    放行请求

* * *

5.文件上传模块
=========

图片存储在服务器本地：
    uploads/
    ├── avatar/
    └── note/

示例路径：
    uploads/avatar/{userId}/
    uploads/note/{userId}/

文件命名规则：
    timestamp_random.jpg

示例：
    1710144000000_a1b2c3.jpg

限制：

| 项目   | 限制               |
| ---- | ---------------- |
| 图片大小 | ≤5MB             |
| 图片数量 | ≤9               |
| 格式   | jpg/png/gif/webp |

* * *

6.AI 总结模块
==========

系统接入 **火山引擎豆包大模型** 实现 AI 总结功能。

调用流程：
    用户点击 AI 总结
           │
           ▼
    检查数据库是否已有 ai_summary
           │
           ├── 有 → 返回缓存
           │
           └── 无
                 │
                 ▼
            调用豆包 API
                 │
                 ▼
            保存总结结果
                 │
                 ▼
            返回前端

Prompt 示例：
    请用简洁的语言总结以下笔记内容：
    要求：
    1. 不超过100字
    2. 提取核心信息
    3. 语言简洁
    标题：{title}
    内容：{content}

* * *

7.数据库访问
========

系统使用 **Spring Data JPA**。

主要特性：

* 自动生成 CRUD

* JPQL 查询

* 分页查询

* 关联映射

示例：
    Page<Note> findByStatus(Integer status, Pageable pageable);

分页：
    PageRequest.of(page, size)

* * *

8.日志系统
=======

使用 **Logback**。

日志级别：
    DEBUG
    INFO
    WARN
    ERROR

配置文件：
    logback-spring.xml

* * *

9.API 文档
=========

系统使用 **Swagger** 自动生成接口文档。

启动后访问：
    http://localhost:8080/swagger-ui.html

Swagger 可用于：

* 查看接口列表

* 调试接口

* 查看参数格式

* * *

10.部署与运行
=========

### 10.1 环境要求

| 软件    | 版本   |
| ----- | ---- |
| JDK   | 17+  |
| MySQL | 8.0  |
| Maven | 3.8+ |

* * *

### 10.2 配置数据库

    CREATE DATABASE essencity
    DEFAULT CHARACTER SET utf8mb4;

* * *

### 10.3 修改配置文件

    application-dev.yml

配置：
    spring.datasource.url
    spring.datasource.username
    spring.datasource.password

* * *

### 10.4 启动项目

    cd essencity-backend
    
    mvn spring-boot:run

启动成功：
    http://localhost:8080

* * *

11.后端优化方向
==========

未来可优化：

* Redis 缓存（点赞、浏览量）

* Elasticsearch 搜索

* 对象存储 OSS

* 消息队列（通知系统）

* 推荐算法

* 微服务架构

* * *
