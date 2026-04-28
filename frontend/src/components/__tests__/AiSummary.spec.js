import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import AiSummary from '../AiSummary.vue'

vi.mock('@/api/ai.js', () => ({
  getAiSummary: vi.fn(),
  generateAiSummary: vi.fn()
}))

import { getAiSummary, generateAiSummary } from '@/api/ai.js'

describe('AiSummary.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  const createWrapper = (props = {}) => {
    return mount(AiSummary, {
      props: {
        postId: 1,
        title: '测试标题',
        content: '测试内容',
        ...props
      },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  describe('初始状态', () => {
    it('初始状态应显示空状态和获取按钮', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.empty-state').exists()).toBe(true)
      expect(wrapper.find('.fetch-btn').exists()).toBe(true)
    })

    it('不应显示加载状态', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.loading-state').exists()).toBe(false)
    })

    it('不应显示错误状态', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.error-state').exists()).toBe(false)
    })

    it('不应显示总结内容', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.summary-content').exists()).toBe(false)
    })
  })

  describe('获取总结成功', () => {
    it('点击获取按钮后调用 getAiSummary', async () => {
      getAiSummary.mockResolvedValue({ ai_summary: '已有总结内容' })
      const wrapper = createWrapper()
      await wrapper.find('.fetch-btn').trigger('click')
      expect(getAiSummary).toHaveBeenCalledWith(1)
    })

    it('成功获取总结后显示总结内容', async () => {
      getAiSummary.mockResolvedValue({ ai_summary: '已有总结内容' })
      const wrapper = createWrapper()
      await wrapper.find('.fetch-btn').trigger('click')
      await wrapper.vm.$nextTick()
      expect(wrapper.find('.summary-content').exists()).toBe(true)
      expect(wrapper.find('.summary-content p').text()).toBe('已有总结内容')
    })

    it('成功获取总结后 hasSummary 为 true', async () => {
      getAiSummary.mockResolvedValue({ ai_summary: '已有总结内容' })
      const wrapper = createWrapper()
      await wrapper.find('.fetch-btn').trigger('click')
      await wrapper.vm.$nextTick()
      expect(wrapper.vm.hasSummary).toBe(true)
    })
  })

  describe('获取总结失败后自动生成', () => {
    it('获取失败时自动触发生成', async () => {
      getAiSummary.mockRejectedValue(new Error('获取AI总结失败'))
      generateAiSummary.mockResolvedValue({ ai_summary: '生成的新总结' })
      const wrapper = createWrapper()
      await wrapper.find('.fetch-btn').trigger('click')
      await wrapper.vm.$nextTick()
      expect(generateAiSummary).toHaveBeenCalled()
    })

    it('404错误时自动触发生成', async () => {
      getAiSummary.mockRejectedValue(new Error('404 Not Found'))
      generateAiSummary.mockResolvedValue({ ai_summary: '生成的新总结' })
      const wrapper = createWrapper()
      await wrapper.find('.fetch-btn').trigger('click')
      await wrapper.vm.$nextTick()
      expect(generateAiSummary).toHaveBeenCalled()
    })
  })

  describe('关闭功能', () => {
    it('点击关闭按钮应触发 close 事件', async () => {
      const wrapper = createWrapper()
      await wrapper.find('.close-btn').trigger('click')
      expect(wrapper.emitted('close')).toBeTruthy()
    })

    it('点击遮罩层应触发 close 事件', async () => {
      const wrapper = createWrapper()
      await wrapper.find('.ai-summary-overlay').trigger('click')
      expect(wrapper.emitted('close')).toBeTruthy()
    })
  })

  describe('Props 验证', () => {
    it('应正确接收 postId', () => {
      const wrapper = createWrapper({ postId: 123 })
      expect(wrapper.props('postId')).toBe(123)
    })

    it('应正确接收 title', () => {
      const wrapper = createWrapper({ title: '自定义标题' })
      expect(wrapper.props('title')).toBe('自定义标题')
    })

    it('应正确接收 content', () => {
      const wrapper = createWrapper({ content: '自定义内容' })
      expect(wrapper.props('content')).toBe('自定义内容')
    })
  })
})