# 陈熠恒 - 前端单元测试贡献说明

## 基本信息

- **姓名**: 陈熠恒
- **学号**: 2312190613
- **日期**: 2026-04-22

---

## 贡献概述

本次贡献为前端实现了完整的单元测试覆盖，将前端测试覆盖率从 **25% 提升至 82%**（行覆盖率），新增 **16 个测试文件**，覆盖全部核心组件和 API 模块，新增 **215 个测试用例**。

---

## 贡献内容

### 1. 测试框架搭建

**技术方案**：Vitest + @vue/test-utils + v8 coverage

**测试配置**：`vitest.config.js`

```javascript
export default defineConfig({
  test: {
    environment: 'jsdom',
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: ['node_modules/', 'src/main.js']
    }
  }
})
```

### 2. API 模块测试

**测试文件**：`src/api/__tests__/ai.spec.js`

| 测试用例 | 覆盖场景 |
|---------|---------|
| `getAiSummary - 成功获取AI总结` | GET 请求成功返回 |
| `getAiSummary - 获取失败时抛出错误` | 404 错误处理 |
| `generateAiSummary - 成功生成AI总结` | POST 请求成功 |
| `generateAiSummary - 生成失败时抛出错误` | 500 错误处理 |
| `generateAiSummary - 使用POST方法` | HTTP 方法验证 |
| `generateAiSummary - 设置正确的Content-Type` | 请求头验证 |
| `generateAiSummary - 正确传递请求体参数` | 请求参数验证 |

**行覆盖率**：100%

### 3. 组件测试

| 测试文件 | 测试数 | 行覆盖率 | 主要覆盖内容 |
|---------|-------|---------|-------------|
| `AuthModal.spec.js` | 20 | 99.34% | 登录/注册表单验证、模式切换、提交逻辑 |
| `CategoryTabs.spec.js` | 7 | 100% | 分类切换、事件触发 |
| `PostCard.spec.js` | 12 | 100% | 图片处理、点赞格式化、视频标识 |
| `TheHeader.spec.js` | 9 | 98.27% | 搜索功能、占位符文本、props 验证 |
| `TheSidebar.spec.js` | 12 | 99.19% | 菜单项、主题切换、头像 URL 处理 |
| `VoiceSearch.spec.js` | 7 | 95.59% | 浏览器兼容性检测、关闭功能 |
| `AiSummary.spec.js` | 14 | 96.41% | AI 总结获取/生成逻辑、状态管理 |
| `AiLoading.spec.js` | 11 | 100% | 加载动画显示/隐藏 |
| `CreationPage.spec.js` | 17 | 80.56% | 多图上传、话题标签、表单提交 |
| `MasonryGrid.spec.js` | 11 | 97.72% | 响应式列数计算、项目分发 |
| `NotificationPage.spec.js` | 22 | 99.26% | 通知列表、时间格式化、API 调用 |
| `CompleteProfileModal.spec.js` | 12 | 94.76% | 资料完善表单、头像上传 |
| `PostDetailModal.spec.js` | 21 | 75.88% | 点赞/收藏状态、评论功能、AI 总结 |
| `ProfilePage.spec.js` | 22 | 82.35% | 用户资料、帖子列表、关注功能 |

### 4. Composable 测试

**测试文件**：`src/composables/__tests__/useSpeech.spec.js`

| 测试用例 | 覆盖场景 |
|---------|---------|
| `checkSupport - 返回浏览器支持状态` | 兼容性检测 |
| `startListening - 启动时应清空 error` | 状态初始化 |
| `startListening - 重复启动应返回` | 防重复启动 |
| `stopListening - 应在 isListening 时调用 recognition.stop` | 停止监听 |
| `resetTranscript - 应清空 transcript 和 error` | 重置状态 |
| `isListening 初始值应为 false` | 初始状态 |
| `transcript 初始值应为空字符串` | 初始状态 |
| `error 初始值应为 null` | 初始状态 |

**行覆盖率**：63.15%

### 5. 测试覆盖率统计

| 指标 | 覆盖前 | 覆盖后 | 目标 |
|------|-------|-------|------|
| 语句覆盖率 | 25% | **82%** | >50% ✅ |
| 分支覆盖率 | 81.5% | **82.62%** | ✅ |

---

## 技术要点

### 组件测试模式

```javascript
describe('AuthModal.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(AuthModal, {
      props: { ...props },
      global: {
        stubs: { teleport: true }
      }
    })
  }

  beforeEach(() => {
    global.fetch = vi.fn()
  })

  it('表单验证失败时不提交', async () => {
    const wrapper = createWrapper()
    wrapper.vm.username = 'ab'  // 少于3位
    wrapper.vm.password = '123'
    await wrapper.vm.handleSubmit()
    expect(global.fetch).not.toHaveBeenCalled()
  })
})
```

### Mock API 响应

```javascript
it('登录成功触发 login-success 事件', async () => {
  global.fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => ({ id: 1, username: 'test' })
  })
  const wrapper = createWrapper()
  wrapper.vm.username = 'testuser'
  wrapper.vm.password = '123456'
  await wrapper.vm.handleSubmit()
  expect(wrapper.emitted('login-success')).toBeTruthy()
})
```

### 组件 Stub

```javascript
vi.mock('./AiSummary.vue', () => ({
  default: {
    name: 'AiSummary',
    props: ['postId'],
    template: '<div class="mock-ai-summary"></div>'
  }
}))
```

### 响应式布局测试

```javascript
it('窗口宽度 >= 1380 时列数为5', async () => {
  const wrapper = createWrapper()
  window.innerWidth = 1400
  wrapper.vm.updateColumnCount()
  expect(wrapper.vm.columnCount).toBe(5)
})
```

---

## 测试文件清单

```
src/
├── api/
│   └── __tests__/
│       └── ai.spec.js              ✅ 100%
├── components/
│   ├── __tests__/
│   │   ├── AiLoading.spec.js       ✅ 100%
│   │   ├── AiSummary.spec.js       ✅ 96%
│   │   ├── AuthModal.spec.js       ✅ 99%
│   │   ├── CategoryTabs.spec.js    ✅ 100%
│   │   ├── CompleteProfileModal.spec.js  ✅ 95%
│   │   ├── CreationPage.spec.js    ✅ 81%
│   │   ├── MasonryGrid.spec.js     ✅ 98%
│   │   ├── NotificationPage.spec.js ✅ 99%
│   │   ├── PostCard.spec.js        ✅ 100%
│   │   ├── PostDetailModal.spec.js ✅ 76%
│   │   ├── ProfilePage.spec.js     ✅ 82%
│   │   ├── TheHeader.spec.js       ✅ 98%
│   │   ├── TheSidebar.spec.js      ✅ 99%
│   │   └── VoiceSearch.spec.js     ✅ 96%
│   └── AiSummary.vue                ✅ 已覆盖
├── composables/
│   └── __tests__/
│       └── useSpeech.spec.js       ✅ 63%
```

---

## 运行测试

```bash
# 运行所有测试
npm run test

# 运行测试并生成覆盖率报告
npm run test:coverage

# 查看覆盖率报告
# 打开 coverage/index.html
```

---

## 后续优化方向

1. **提升 useSpeech.js 覆盖率**（当前 63%）- 添加更多边界情况测试
2. **补充 PostDetailModal.vue 交互测试** - 点赞、评论等用户交互
3. **添加 App.vue 主组件测试** - 路由和视图切换逻辑
4. **集成测试** - 组件间交互和数据流动