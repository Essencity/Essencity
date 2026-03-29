# AI Summary 组件测试规范

## 测试文件位置
`frontend/src/components/__tests__/AiSummary.spec.js`

## 测试框架
Vitest + @vue/test-utils

## 测试用例

### 1. AiSummary.vue 组件测试

```javascript
import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import AiSummary from '../AiSummary.vue'

// Mock fetch
global.fetch = vi.fn()

describe('AiSummary Component', () => {

  beforeEach(() => {
    fetch.mockClear()
  })

  describe('Props 验证', () => {
    it('应该正确接收 postId prop', () => {
      const wrapper = mount(AiSummary, {
        props: {
          postId: 123,
          title: '测试标题',
          content: '测试内容'
        }
      })
      expect(wrapper.props('postId')).toBe(123)
    })

    it('postId 为必填项', () => {
      expect(() => {
        mount(AiSummary, {
          props: { title: '测试' }
        })
      }).toThrow()
    })
  })

  describe('API 调用测试', () => {
    it('点击获取总结按钮应调用 API', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ ai_summary: '这是一段AI总结' })
      })

      const wrapper = mount(AiSummary, {
        props: { postId: 123 }
      })

      // 初始状态应显示空状态
      expect(wrapper.find('.empty-state').exists()).toBe(true)

      // 点击获取按钮
      await wrapper.find('.fetch-btn').trigger('click')

      // 验证 fetch 被调用
      expect(fetch).toHaveBeenCalledWith('/api/ai/summary/123')
    })

    it('API 返回总结时应显示总结内容', async () => {
      const mockSummary = '这是一段很有价值的AI总结内容'
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ ai_summary: mockSummary })
      })

      const wrapper = mount(AiSummary, {
        props: { postId: 123 }
      })

      await wrapper.find('.fetch-btn').trigger('click')

      // 等待状态更新
      await wrapper.vm.$nextTick()

      // 验证总结内容显示
      expect(wrapper.find('.summary-content').exists()).toBe(true)
      expect(wrapper.find('.summary-content p').text()).toBe(mockSummary)
    })

    it('API 失败时应显示错误状态', async () => {
      fetch.mockResolvedValueOnce({
        ok: false
      })

      const wrapper = mount(AiSummary, {
        props: { postId: 123 }
      })

      await wrapper.find('.fetch-btn').trigger('click')
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.error-state').exists()).toBe(true)
      expect(wrapper.find('.error-state p').text()).toBe('获取总结失败，请稍后再试')
    })
  })

  describe('加载状态测试', () => {
    it('API 请求中应显示加载动画', async () => {
      let resolvePromise
      fetch.mockResolvedValueOnce(new Promise(resolve => {
        resolvePromise = resolve
      }))

      const wrapper = mount(AiSummary, {
        props: { postId: 123 }
      })

      await wrapper.find('.fetch-btn').trigger('click')

      expect(wrapper.find('.loading-state').exists()).toBe(true)
      expect(wrapper.find('.loading-spinner').exists()).toBe(true)
    })
  })

  describe('交互测试', () => {
    it('点击关闭按钮应触发 close 事件', async () => {
      const wrapper = mount(AiSummary, {
        props: { postId: 123 },
        emits: ['close']
      })

      await wrapper.find('.close-btn').trigger('click')

      expect(wrapper.emitted('close')).toBeTruthy()
    })

    it('错误状态下点击重试应重新请求', async () => {
      fetch.mockResolvedValueOnce({ ok: false })
        .mockResolvedValueOnce({
          ok: true,
          json: () => Promise.resolve({ ai_summary: '重试后的总结' })
        })

      const wrapper = mount(AiSummary, {
        props: { postId: 123 }
      })

      await wrapper.find('.fetch-btn').trigger('click')
      await wrapper.vm.$nextTick()

      await wrapper.find('.retry-btn').trigger('click')
      await wrapper.vm.$nextTick()

      expect(fetch).toHaveBeenCalledTimes(2)
    })
  })

  describe('样式测试', () => {
    it('模态框应正确渲染', () => {
      const wrapper = mount(AiSummary, {
        props: { postId: 123 }
      })

      expect(wrapper.find('.ai-summary-overlay').exists()).toBe(true)
      expect(wrapper.find('.ai-summary-modal').exists()).toBe(true)
      expect(wrapper.find('.modal-header h3').text()).toBe('AI 总结')
    })
  })
})
```

### 2. AiLoading.vue 组件测试

```javascript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AiLoading from '../AiLoading.vue'

describe('AiLoading Component', () => {

  describe('Props 测试', () => {
    it('默认尺寸为 medium', () => {
      const wrapper = mount(AiLoading)
      expect(wrapper.props('size')).toBe('medium')
    })

    it('应该接受 small、medium、large 尺寸', () => {
      const sizes = ['small', 'medium', 'large']
      sizes.forEach(size => {
        const wrapper = mount(AiLoading, { props: { size } })
        expect(wrapper.classes()).toContain(size)
      })
    })

    it('默认显示加载文本', () => {
      const wrapper = mount(AiLoading)
      expect(wrapper.find('.loading-text').text()).toBe('加载中...')
    })

    it('可以自定义加载文本', () => {
      const wrapper = mount(AiLoading, {
        props: { text: '正在生成...' }
      })
      expect(wrapper.find('.loading-text').text()).toBe('正在生成...')
    })

    it('可以不显示文本', () => {
      const wrapper = mount(AiLoading, {
        props: { text: '' }
      })
      expect(wrapper.find('.loading-text').exists()).toBe(false)
    })
  })

  describe('动画测试', () => {
    it('应该包含三个动画点', () => {
      const wrapper = mount(AiLoading)
      const dots = wrapper.findAll('.dot')
      expect(dots.length).toBe(3)
    })
  })
})
```

## 运行测试命令

```bash
# 安装依赖
npm install vitest @vue/test-utils --save-dev

# 添加测试脚本到 package.json
# "test": "vitest"

# 运行测试
npm run test
```

## 手动测试清单

### AiSummary 组件
- [ ] 未登录用户点击 AI 按钮应弹出总结弹窗
- [ ] 弹窗显示初始状态（获取总结按钮）
- [ ] 点击获取总结显示加载动画
- [ ] API 返回成功显示总结内容
- [ ] API 返回失败显示错误信息和重试按钮
- [ ] 点击关闭按钮弹窗关闭
- [ ] 点击遮罩层弹窗关闭

### AiLoading 组件
- [ ] small 尺寸正确显示
- [ ] medium 尺寸正确显示
- [ ] large 尺寸正确显示
- [ ] 动画点正确跳动
- [ ] 自定义文本正确显示
