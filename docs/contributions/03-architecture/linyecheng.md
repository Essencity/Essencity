# 个人贡献说明

姓名：林烨澄
学号：2312190630
日期：2026-03-24

1. 文档体系建设

---

### 1.1 CLAUDE.md 完善

对项目根目录下的 CLAUDE.md 进行了全面更新：

- **补充前端目录结构**：新增 `api/`、`router/`、`stores/`、`views/` 等实际存在的目录
- **新增安全规范章节**：JWT Secret 管理、文件上传校验、BCrypt 密码加密、SQL 注入防护
- **新增 AI 配置章节**：火山引擎豆包大模型的 API 配置说明
- **新增环境变量章节**：敏感信息不提交到 Git 的规范
- **修正既有信息**：schema.sql 路径归位、技术栈补全 Pinia、DataInitializer/DbConnectionTest 说明

### 1.2 架构设计文档

编写 `docs/architecture.md`，包含：

- **系统架构图**：使用 Mermaid 绘制前后端分层架构（UI组件层 → 路由层 → 状态层 → API层 / 控制器层 → 服务层 → 数据层）
- **技术栈详情**：前端 Vue 3 + Vite + Element Plus + Pinia + Axios；后端 Spring Boot 3 + JPA + MySQL + JWT
- **前端目录结构**：components、config、App.vue、main.js、style.css
- **后端目录结构**：config、controller、service、repository、entity、dto
- **API 架构**：按模块分类（认证、帖子、用户、通知、文件）
- **安全架构**：JWT 无状态认证、BCrypt 加密、CORS 限制、CSRF 禁用
- **数据流图**：使用 Mermaid Sequence Diagram 绘制用户操作的数据流转过程
- **部署架构**：Nginx 反向代理 + 前端静态资源 + 后端 API + MySQL
- **未来扩展点**：AI 笔记总结、语音搜索、话题标签、多图笔记

### 1.3 数据库设计文档

编写 `docs/database.md`，包含：

- **ER 图**：使用 Mermaid 绘制完整实体关系图，展示 7 张表及其关联
- **表结构详情**：users、posts、likes、collections、comments、follows、notifications 每张表的字段、类型、约束、说明及 CREATE TABLE SQL
- **索引设计**：唯一约束（防止重复点赞/收藏/关注）、普通索引（tag、author_id、post_id）
- **业务规则**：唯一性约束、级联关系、标签分类（美食/穿搭/彩妆/影视/职场/情感/家居/游戏/旅行/健身）
- **待完善项**：notifications 表在代码中存在但 schema.sql 缺失、AI 总结字段待增加

---

2. 核心工作内容

---

### 2.1 文档结构规划

按照项目 `docs/` 目录的既有组织方式，将文档放在正确位置：

```
docs/
├── architecture.md      # 架构设计文档
├── database.md          # 数据库设计文档
└── contributions/
    └── 03-architecture/
        ├── linzhongyang.md
        ├── chenyiheng.md
        └── linyecheng.md  # 本人贡献
```

### 2.2 架构图绘制

使用 Mermaid 语法绘制以下图表（确保渲染兼容性）：

- **系统架构图**：graph TB 分层展示前后端各层组件及数据流向
- **安全架构图**：graph LR 展示 JWT Token → 过滤器 → 认证服务 → 数据库的认证流程
- **数据流图**：sequenceDiagram 展示用户发评论的完整请求链路
- **ER 图**：erDiagram 展示 7 张表的字段和关联关系
- **表关系图**：graph LR 展示实体间的一对多/多对多关系

### 2.3 代码一致性核查

在编写文档前，系统性阅读了以下源代码确保文档准确：

**后端实体（6个）**：

- User.java、Post.java、Like.java、Collection.java、Comment.java、Follow.java

**后端控制器（4个）**：

- AuthController.java、PostController.java、FileController.java、NotificationController.java

**后端配置（2个）**：

- SecurityConfig.java（JWT + BCrypt + CSRF禁用）、WebConfig.java（CORS配置）

**前端组件（11个）**：

- TheHeader、TheSidebar、MasonryGrid、CategoryTabs、PostCard、PostDetailModal、CreationPage、ProfilePage、NotificationPage、AuthModal、CompleteProfileModal

### 2.4 发现问题并标注

在文档中记录了代码与 schema.sql 的不一致项：

- `notifications` 表在 NotificationController 和 NotificationService 中被使用，但 `schema.sql` 中无建表语句
- posts 表实际有 `tag` 字段（支持 10 个分类标签），但早期 schema.sql 未体现

---

3. 遇到的问题和解决

---

**问题一：**
代码结构与既有文档（CLAUDE.md）存在多处不一致，直接沿用旧文档会误导后续开发者。

**解决：**
系统性阅读源代码，确认实际目录结构和文件命名，在此基础上更新 CLAUDE.md，确保文档与代码一致。

**问题二：**
schema.sql 中缺少 notifications 表定义，但代码中 Controller 和 Service 已实现通知功能。

**解决：**
在 database.md 中补充该表的建表语句，并标注为"待完善项"，提醒开发者补全到 schema.sql 中。

**问题三：**
后端 API 路由风格不统一（有些带 `/api` 前缀，有些不带），需确认实际生效的路径。

**解决：**
通过阅读 PostController 的 `@RequestMapping("/posts")` 和前端 App.vue 中的 fetch 调用，确认实际 API 路径为 `/posts` 而非 `/api/posts`。

---

4. 心得体会

---

通过本次文档体系建设，我有以下几点收获：

1. **源码阅读能力**：系统性阅读 Spring Boot + Vue 3 的完整项目代码，理解了各层之间的调用关系，提升了快速掌握陌生代码库的能力。

2. **文档即代码**：文档不是附属品，而是项目基础设施的一部分。准确的架构文档和数据库文档能够显著降低团队协作成本。

3. **Mermaid 图表实践**：使用 Mermaid 语法替代截图式架构图，实现了文档与代码同仓库管理、版本追踪、文本合并冲突解决。

4. **发现问题的价值**：在编写文档过程中主动核对代码与文档一致性，发现了 notifications 表缺失等问题，体现了文档工作对项目质量的反哺作用。

5. **最小改动原则**：遵循 CLAUDE.md 中的规则，在不大幅重构目录的前提下更新文档，保持了项目的稳定性。
