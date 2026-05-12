# 安全加固贡献说明

## 基本信息

- **姓名**: 林烨澄
- **学号**: 2312190630
- **日期**: 2026-05-12

---

## 贡献内容

### 1. 移动端适配（Vant 4）

为 Essencity 引入 Vant 4 移动端 UI 框架，实现响应式布局。桌面端保留原有样式，移动端（≤768px）切换为 Vant 组件。

#### 修改文件清单

| 文件                                             | 操作     | 说明                                                            |
| ---------------------------------------------- | ------ | ------------------------------------------------------------- |
| `frontend/package.json`                        | 修改     | 新增 vant、@vant/auto-import-resolver、unplugin-vue-components 依赖 |
| `frontend/vite.config.js`                      | 修改     | 添加 VantResolver 插件实现按需引入                                      |
| `frontend/index.html`                          | 修改     | 添加 viewport-fit=cover 支持刘海屏安全区域                               |
| `frontend/src/main.js`                         | 修改     | 引入 Vant CSS                                                   |
| `frontend/src/style.css`                       | 修改     | 添加 Vant 主题覆盖、移动端 CSS 变量、Safe Area 支持                          |
| `frontend/src/App.vue`                         | 修改     | 引入 MobileBottomBar，移动端布局调整                                    |
| `frontend/src/components/MobileBottomBar.vue`  | **新建** | 移动端底部导航栏（首页/发布/消息/我）                                          |
| `frontend/src/components/TheSidebar.vue`       | 修改     | 移动端隐藏侧边栏                                                      |
| `frontend/src/components/TheHeader.vue`        | 修改     | 移动端简化搜索栏                                                      |
| `frontend/src/components/MasonryGrid.vue`      | 修改     | 移动端固定 2 列瀑布流                                                  |
| `frontend/src/components/PostCard.vue`         | 修改     | 移动端卡片自适应宽度                                                    |
| `frontend/src/components/PostDetailModal.vue`  | 修改     | 移动端帖子详情全屏展示                                                   |
| `frontend/src/components/CategoryTabs.vue`     | 修改     | 移动端 padding 缩小                                                |
| `frontend/src/components/ProfilePage.vue`      | 修改     | 移动端头像和布局缩小                                                    |
| `frontend/src/components/CreationPage.vue`     | 修改     | 移动端表单布局调整                                                     |
| `frontend/src/components/NotificationPage.vue` | 修改     | 移动端 padding 缩小                                                |

#### 移动端特性

- 底部 TabBar 导航（4 个 Tab）
- 双列瀑布流自适应卡片
- 帖子详情全屏展示
- 简化搜索栏
- 刘海屏 Safe Area 适配

---

### 2. 测试数据初始化

修改 `backend/src/main/java/com/xiaohongshu/DataInitializer.java`，在数据库为空时自动插入测试数据。

#### 测试数据统计

| 数据类型 | 数量   | 说明                                     |
| ---- | ---- | -------------------------------------- |
| 用户   | 5 个  | xiaohong、meishi、travel、fashion、fitness |
| 帖子   | 15 个 | 美食/穿搭/旅行/健身/生活各 3 篇                    |
| 评论   | 82 条 | 包含二级回复                                 |
| 点赞   | 38 个 | 随机分布                                   |
| 收藏   | 30 个 | 随机分布                                   |
| 关注   | 14 个 | 用户间互相关注                                |

所有用户密码均为 `123456`，使用 BCrypt 加密存储。

---

### 3. 测试图片下载

从 Unsplash 下载 15 张测试图片，存储在 `backend/uploads/test/` 目录。

#### 图片清单

- `food-1.jpg` ~ `food-3.jpg`（美食）
- `fashion-1.jpg` ~ `fashion-3.jpg`（穿搭）
- `travel-1.jpg` ~ `travel-3.jpg`（旅行）
- `fitness-1.jpg` ~ `fitness-3.jpg`（健身）
- `life-1.jpg` ~ `life-3.jpg`（生活）
- `default-avatar.png`（默认头像）

---

### 4. 文档更新

更新以下项目文档以反映当前状态：

| 文件                     | 更新内容                               |
| ---------------------- | ---------------------------------- |
| `CLAUDE.md`            | Vant 4、移动端适配、测试数据、数据库密码、MiniMax AI |
| `README.md`            | 完整重写：功能特性、移动端预览、测试账号               |
| `docs/frontend.md`     | Vant 4 集成、断点设计、CSS 变量系统            |
| `docs/architecture.md` | 架构图、API 设计、安全设计、移动端适配              |

---

### 5. Bug 修复

- **数据库密码错误**：`application-secrets.properties` 中密码从 `root` 改为 `lyc`
- **图片 404**：测试图片从 `static/uploads/` 复制到 `uploads/` 目录
- **CSP 字体阻止**：`index.html` 中 font-src 添加 `data:` 和 `http://at.alicdn.com`
- **ESLint 错误**：`MobileBottomBar.vue` 中 `currentUser` 改为 `props.currentUser`

---

## 密码检查逻辑

### BCrypt 加密方式

项目使用 **BCrypt** 算法进行密码加密，由 Spring Security 的 `BCryptPasswordEncoder` 实现。

**核心代码（`UserService.java`）：**

```java
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 注册时加密密码
    public User register(String username, String password, String nickname) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));  // BCrypt 加密
        user.setNickname(nickname);
        return userRepository.save(user);
    }

    // 登录时验证密码
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;  // 密码匹配
        }
        return null;  // 密码不匹配或用户不存在
    }
}
```

### BCrypt 算法特点

| 特性       | 说明                                                             |
| -------- | -------------------------------------------------------------- |
| 盐值（Salt） | 自动生成随机盐，存储在哈希值中                                                |
| 哈希长度     | 60 字符（`$2a$10$...` 格式）                                         |
| 安全性      | 抗彩虹表攻击、抗暴力破解                                                   |
| 示例       | `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy` |

### 认证流程

```
用户注册
    │
    ▼
输入密码 → BCrypt.encode(密码) → 存储哈希值到数据库
    │
    ▼
用户登录
    │
    ▼
输入密码 + 用户名 → 查询数据库获取哈希值
    │
    ▼
BCrypt.matches(输入密码, 存储哈希值) → true/false
    │
    ▼
true → 生成 JWT Token → 返回给客户端
false → 返回错误信息
```

### 安全设计要点

1. **密码不落盘明文**：数据库中只存储 BCrypt 哈希值，永远不存储明文密码
2. **统一错误信息**：登录失败时返回通用错误信息"用户名或密码错误"，不区分用户不存在和密码错误
3. **无密码强度验证**：当前未对密码长度、复杂度进行校验（建议后续添加）
4. **JWT 无状态认证**：登录成功后生成 JWT Token，有效期 24 小时

### AuthController 接口

```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    // 返回通用错误信息，不泄露用户是否存在
    if (userService.findByUsername(request.getUsername()) != null) {
        return ResponseEntity.badRequest().body(Map.of("error", "用户名已存在"));
    }
    // ...
}

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    User user = userService.login(request.getUsername(), request.getPassword());
    if (user == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "用户名或密码错误"));
    }
    // 生成 JWT Token
    String token = jwtUtil.generateToken(user.getUsername());
    return ResponseEntity.ok(Map.of("token", token, "user", ...));
}
```

### 安全建议

| 问题           | 建议                               |
| ------------ | -------------------------------- |
| 无密码强度验证      | 添加最小长度、复杂度要求                     |
| 无登录失败限制      | 添加登录失败次数限制，防止暴力破解                |
| Token 过期时间较长 | 考虑缩短 JWT 有效期，添加 Refresh Token 机制 |
| 无 HTTPS 强制   | 生产环境应强制 HTTPS                    |

---

## 技术要点

### Vant 4 按需引入

通过 `unplugin-vue-components` 实现自动按需引入，无需手动导入组件：

```javascript
// vite.config.js
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from '@vant/auto-import-resolver'

export default defineConfig({
  plugins: [
    vue(),
    Components({ resolvers: [VantResolver()] })
  ]
})
```

### CSS 媒体查询断点

```css
/* 移动端 */
@media (max-width: 768px) { ... }

/* 桌面端 */
@media (min-width: 769px) { ... }
```

### Safe Area 适配

```css
:root {
  --safe-area-bottom: env(safe-area-inset-bottom, 0px);
}

.mobile-tabbar {
  padding-bottom: var(--safe-area-bottom);
}
```

---

## 测试结果

- `npm run test`：现有测试全部通过
- `npm run build`：构建成功
- 移动端预览：浏览器 F12 切换到手机模式（375px）可正常显示

---

## 工作过程

### 1. 需求分析

- 用户希望项目能在手机端正常浏览
- 选择 Vant 4 作为移动端 UI 框架
- 断点设为 768px

### 2. 实施步骤

- 安装 Vant 4 及相关依赖
- 配置 Vite 按需引入
- 修改全局样式添加移动端变量
- 新建 MobileBottomBar 组件
- 修改各组件的移动端样式

### 3. 测试数据

- 修改 DataInitializer 添加测试数据
- 从 Unsplash 下载测试图片
- 修复数据库密码配置

### 4. 文档更新

- 更新 CLAUDE.md、README.md
- 更新 docs/frontend.md、docs/architecture.md
