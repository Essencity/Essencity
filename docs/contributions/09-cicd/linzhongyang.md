# CI/CD 贡献记录 - 林忠阳

## 角色
后端开发 (学号: 2212190528)

## 参与阶段
CI/CD 流水线搭建（第09次作业）

---

## 主要贡献

### 1. CI 工作流创建
- 创建 `.github/workflows/ci.yml`，设计后端+前端并行作业结构
- 配置 GitHub Actions 触发器：`push` 到 `main/develop` 分支 + `PR` 到 `main`
- 后端作业：JDK 17 + Maven + JaCoCo 覆盖率 + Codecov 上传
- 前端作业：Node 20 + npm ci + ESLint + Vitest 测试 + Codecov 上传

### 2. 后端 CI 适配
- 基于 JDK 17 + Maven 配置后端测试作业（`mvn test -B`）
- 利用 JaCoCo 插件生成 XML 格式覆盖率报告给 Codecov
- 后端 Service 层测试使用 Mockito Mock Repository，无需在 CI 中启动数据库
- 覆盖标志位：`flags: backend`

### 3. 前端 CI 配置
- 配置 Node 20 环境 + `npm ci` 依赖安装
- Vitest 使用 `vitest run --coverage` 非交互模式，避免 CI 中 watch 模式卡死
- `setupTests.js` 已配置 `jsdom` 环境 + `window.matchMedia` / `SpeechRecognition` Mock
- CI 环境注入 `VITE_API_URL: http://localhost:3000` 测试用地址

### 4. ESLint 配置
- 添加 `eslint` + `eslint-plugin-vue` 到 devDependencies
- 创建 `eslint.config.js`（flat config 格式，Vue essential 规则集）
- `lint` 命令使用 `--max-warnings 0` 确保零警告通过

### 5. 覆盖率集成
- 后端 JaCoCo XML：`backend/target/site/jacoco/jacoco.xml`
- 前端 lcov.info：`frontend/coverage/lcov.info`
- Codecov 项目：https://codecov.io/gh/Essencity/Essencity
- `fail_ci_if_error: false`：Codecov 上传失败不阻塞 CI

### 6. README 状态徽章
- CI workflow 状态徽章：`github.com/Essencity/Essencity/actions/workflows/ci.yml/badge.svg`
- 后端 Codecov 覆盖率徽章（flags: backend）
- 前端 Codecov 覆盖率徽章（flags: frontend）

---

## 技术决策记录

| 决策 | 理由 |
|------|------|
| Maven `mvn test` 而非 Gradle | 项目使用 Spring Boot + Maven |
| JaCoCo XML 格式 | Codecov 优先解析 XML 格式覆盖率报告 |
| Mockito 隔离数据库 | Service 层测试无需真实数据库，CI 环境可直接运行 |
| `vitest run` 非 watch 模式 | CI 终端非交互式，watch 模式会导致卡死 |
| ESLint flat config | ESLint v9 推荐格式，兼容 Vue 3 |
| `fail_ci_if_error: false` | Codecov Token 问题不应阻塞流水线 |

---

## 本地验证检查清单

### 后端
- [ ] `mvn test` 全部通过
- [ ] JaCoCo 覆盖率报告生成（`target/site/jacoco/jacoco.xml`）
- [ ] 核心覆盖率 > 60%

### 前端
- [ ] `npm run lint` 零错误
- [ ] `npm test` 全部通过
- [ ] `coverage/lcov.info` 文件生成
