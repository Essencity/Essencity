# 林忠阳 - 后端开发贡献说明

## 基本信息

- **姓名**: 林忠阳
- **学号**: 2212190528
- **日期**: 2026-04-01

---

## 贡献内容

### 1. 文件上传配置优化

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

### 2. OpenAPI 规范文档

创建了完整的 OpenAPI 3.0.3 规范文档，包含：
- 认证模块 (登录、注册、资料更新、关注操作)
- 帖子模块 (CRUD、文件上传、点赞收藏)
- 评论模块 (获取、添加、删除)
- 通知模块

详见 [docs/contributions/04-api/linzhongyang.md](../docs/contributions/04-api/linzhongyang.md)

---

### 3. 后端接口测试

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