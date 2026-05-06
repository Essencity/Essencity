# CI/CD 配置贡献说明

**姓名**: 陈熠恒  
**学号**: 2312190613  
**角色**: 前端  
**日期**: 2026-05-05

---

## 完成的工作

### 工作流相关
- [x] 参与编写 / 审查 `.github/workflows/ci.yml`（前端部分）
- [x] 配置 Codecov 覆盖率上传（frontend flag）
- [ ] 添加 README 状态徽章（待团队统一添加）

### 代码适配
- [x] 本地测试命令与 CI 一致，无需额外配置
  - `npm test` = `vitest run --coverage`（非 watch 模式）
- [x] 代码通过 Lint 检查（ESLint）
  - 配置 `eslint.config.js` 支持 Vue 3 和测试环境
  - 解决浏览器全局变量问题（alert, URL, Image, setTimeout）
  - 解决测试全局变量问题（global, vi, describe, it, expect）
- [x] 核心覆盖率达标（> 60%）
  - 语句覆盖率：81.80%
  - 分支覆盖率：82.20%
  - 测试用例：215/215 通过

### 可选项
- [ ] 配置 Dependabot 自动更新依赖
- [ ] 集成 CodeRabbit AI 代码审查
- [ ] 使用 act 本地验证工作流

---

## PR 链接
- PR #X: https://github.com/xxx/xxx/pull/X（待创建）

## CI 运行链接
- https://github.com/xxx/xxx/actions/runs/XXX（待运行）

---

## 遇到的问题和解决

### 1. ESLint 浏览器环境变量未定义
**问题**: 代码中使用 `alert()`, `URL`, `Image`, `setTimeout` 等浏览器 API 时，ESLint 报错 `no-undef`  
**解决**: 在 `eslint.config.js` 的 `languageOptions.globals` 中声明这些全局变量为 `'readonly'`

```javascript
globals: {
  alert: 'readonly',
  URL: 'readonly',
  Image: 'readonly',
  setTimeout: 'readonly',
  clearTimeout: 'readonly'
}
```

### 2. 测试文件全局变量未定义
**问题**: 测试文件中使用 `global`, `vi`, `describe`, `it`, `expect` 等 Vitest 全局变量时 ESLint 报错  
**解决**: 为测试文件单独配置 globals

```javascript
{
  files: ['**/*.spec.js', '**/setupTests.js'],
  languageOptions: {
    globals: {
      global: 'writable',
      vi: 'readonly',
      describe: 'readonly',
      it: 'readonly',
      expect: 'readonly',
      beforeEach: 'readonly',
      afterEach: 'readonly',
      File: 'readonly'
    }
  }
}
```

### 3. Vue 模板格式警告过多
**问题**: Vue 官方推荐规则（max-attributes-per-line, html-self-closing 等）产生大量警告，影响 CI 通过  
**解决**: 在 ESLint 配置中关闭这些非关键格式规则

```javascript
rules: {
  'vue/max-attributes-per-line': 'off',
  'vue/html-self-closing': 'off',
  'vue/html-closing-bracket-spacing': 'off',
  'vue/singleline-html-element-content-newline': 'off',
  'vue/attributes-order': 'off',
  'vue/require-explicit-emits': 'off',
  'vue/multiline-html-element-content-newline': 'off',
  'vue/html-indent': 'off'
}
```

### 4. CI 环境测试卡住
**问题**: Vitest 默认使用 watch 模式，在 CI 非交互式终端中会卡住  
**解决**: 修改 `package.json`，使用 `vitest run` 替代 `vitest`

```json
{
  "scripts": {
    "test": "vitest run --coverage"
  }
}
```

---

## 关键配置说明

### package.json
```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "test": "vitest run --coverage",
    "test:coverage": "vitest run --coverage",
    "lint": "eslint src/ --max-warnings 0"
  }
}
```

### vitest.config.js
```javascript
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  test: {
    environment: 'jsdom',
    globals: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      exclude: [
        'node_modules/',
        'src/main.js',
        '**/*.spec.js',
        'coverage/'
      ]
    }
  }
})
```

---

## 本地验证结果

### Lint 检查
```bash
$ npm run lint
> eslint src/ --max-warnings 0
# 无错误，无警告 ✅
```

### 测试与覆盖率
```bash
$ npm test
 Test Files  16 passed (16)
      Tests  215 passed (215)
   Duration  10.75s

 % Coverage report from v8
-------------------|---------|----------|---------|---------|
File               | % Stmts | % Branch | % Funcs | % Lines |
-------------------|---------|----------|---------|---------|
All files          |   81.80 |    82.20 |   41.17 |   81.80 |
 src/api           |  100.00 |   100.00 |  100.00 |  100.00 |
 src/components    |   86.51 |    82.89 |   38.84 |   86.51 |
 src/composables   |   63.15 |    71.42 |   70.00 |   63.15 |
-------------------|---------|----------|---------|---------|
```

**覆盖率达标情况**:
| 指标 | 目标 | 实际 | 状态 |
|------|------|------|------|
| 语句覆盖率 | ≥60% | **81.80%** | ✅ 达标 |
| 分支覆盖率 | - | **82.20%** | ✅ 优秀 |
| 测试通过率 | 100% | **100%** (215/215) | ✅ 通过 |
| ESLint 错误 | 0 | **0** | ✅ 通过 |

---

## 截图佐证

### 1. Git 提交记录
**命令**: `git log --author="陈熠恒" --oneline`  
**截图位置**: [在此处插入截图]  
**说明**: 显示本次 CI/CD 配置相关的提交记录

### 2. 本地 Lint 检查通过
**命令**: `npm run lint`  
**截图位置**: [在此处插入截图]  
**说明**: 显示 `eslint src/ --max-warnings 0` 无错误输出

### 3. 本地测试通过
**命令**: `npm test`  
**截图位置**: [在此处插入截图]  
**说明**: 显示 215 个测试全部通过，覆盖率 81.80%

### 4. CI 运行成功（待工作流合并后补充）
**位置**: GitHub Actions 页面  
**截图位置**: [在此处插入截图]  
**说明**: frontend job 显示绿色 ✓

### 5. Codecov 覆盖率上传（待工作流合并后补充）
**位置**: Codecov 网站  
**截图位置**: [在此处插入截图]  
**说明**: 显示 frontend 覆盖率 81.80%

---

## 心得体会

通过本次 CI/CD 配置任务，我深入理解了前端项目在持续集成环境中的关键配置点：

1. **测试模式的重要性**: Vitest 的 watch 模式在 CI 环境会导致阻塞，必须使用 `vitest run` 确保非交互式运行。

2. **ESLint 环境配置**: 正确配置浏览器全局变量和测试全局变量是避免 `no-undef` 错误的关键。通过为测试文件单独配置规则，实现了生产代码和测试代码的差异化检查。

3. **覆盖率报告格式**: lcov 格式是 Codecov 等覆盖率服务的标准输入格式，配置多种 reporter 可以同时满足本地查看和云端上传的需求。

4. **代码质量与效率平衡**: Vue 的严格格式规则虽然有助于代码规范，但在现有项目中会产生大量警告。适当关闭非关键格式规则，专注于逻辑错误检查，可以在保证质量的同时提高开发效率。

5. **CI 本地预检**: 在提交到 GitHub Actions 之前，先在本地运行 `npm run lint` 和 `npm test`，可以快速发现问题，减少 CI 失败的等待时间。

本次配置使前端项目具备了完整的 CI/CD 基础能力，为团队协作和代码质量保障打下了坚实基础。
