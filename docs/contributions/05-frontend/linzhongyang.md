# 林忠阳 - 前端开发贡献说明

## 基本信息

- **姓名**: 林忠阳
- **学号**: 2212190528
- **日期**: 2026-04-01

---

## 贡献内容

### 1. 前端图片上传功能修复

#### 问题描述

用户反馈发布笔记时图片无法正常上传，表现为：
- 点击上传图片后图片可以预览
- 但无法跳转到填写标题的页面
- 点击发布按钮没有任何反应

#### 原因分析

通过代码审查发现问题：
1. **变量名错误**: [CreationPage.vue:577](src/components/CreationPage.vue#L577) 中使用 `!imageFile` 判断，但实际定义的变量是 `imageFiles`（复数形式）
2. **上传目录配置**: 后端 `file.upload-dir` 使用相对路径 `./uploads`，导致文件存储位置不一致
3. **静态资源代理缺失**: Vite 配置中未配置 `/uploads` 路径代理

#### 修复方案

1. **修正变量名**: 将 `v-if="!imageFile"` 改为 `v-if="imageFiles.length === 0"`

2. **更新后端配置** ([application.properties](../backend/src/main/resources/application.properties)):
   ```properties
   file.upload-dir=D:/1/Essencity/uploads
   ```

3. **配置 Vite 代理** ([vite.config.js](../vite.config.js)):
   ```javascript
   '/uploads': {
     target: 'http://localhost:8080',
     changeOrigin: true
   }
   ```

4. **更新 Nginx 配置** ([nginx.conf](../nginx.conf)):
   ```nginx
   location /uploads {
       proxy_pass http://backend:8080;
       proxy_set_header Host $host;
   }
   ```

5. **复制历史文件**: 将 `backend/uploads/` 目录下的文件复制到 `D:/1/Essencity/uploads/`

#### 相关文件

| 文件路径 | 说明 |
| ------- | ---- |
| [src/components/CreationPage.vue:577](../src/components/CreationPage.vue#L577) | 修复变量名 |
| [vite.config.js:19-22](../vite.config.js#L19-L22) | 添加 uploads 代理 |
| [nginx.conf:18-23](../nginx.conf#L18-L23) | 添加 uploads 反向代理 |
| [backend/src/main/resources/application.properties](../backend/src/main/resources/application.properties) | 更新上传目录配置 |

---

## 技术要点

### Vue 模板条件渲染

- 变量名必须与 script 中定义的 ref 保持一致
- 使用 `length` 检查数组是否为空

### Vite 代理配置

- 开发环境通过代理解决跨域问题
- `/api` 和 `/uploads` 都需要配置代理

### 反向代理

- 生产环境 Nginx 需要配置 `/uploads` 路径转发
- 确保前端请求能正确到达后端静态文件服务

---

## 测试结果

修复后，用户可以：
1. 正常选择图片并预览
2. 切换到填写标题和内容的页面
3. 点击发布按钮提交笔记

---

## 工作过程

### 1. 问题定位

- 用户反馈：上传图片后没有反应
- 检查代码：发现变量名不一致
- 实际变量：`imageFiles` (数组)
- 错误引用：`imageFile` (未定义)

### 2. 配置排查

- 检查上传目录配置
- 测试后端文件访问
- 发现相对路径导致文件存储位置不一致

### 3. 修复实施

- 修改变量名
- 配置绝对路径
- 添加代理配置
- 复制历史文件到正确目录