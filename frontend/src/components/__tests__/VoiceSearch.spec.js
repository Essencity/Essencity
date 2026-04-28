import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import VoiceSearch from '../VoiceSearch.vue'
import { useSpeech } from '@/composables/useSpeech.js'

vi.mock('@/composables/useSpeech.js', () => ({
  useSpeech: vi.fn(() => ({
    isListening: { value: false },
    isSupported: { value: true },
    transcript: { value: '' },
    error: { value: null },
    checkSupport: vi.fn(),
    startListening: vi.fn(),
    stopListening: vi.fn(),
    resetTranscript: vi.fn()
  }))
}))

describe('VoiceSearch.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(VoiceSearch, {
      props: { visible: false, ...props },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  describe('初始状态', () => {
    it('visible 为 false 时不显示弹窗', () => {
      const wrapper = createWrapper({ visible: false })
      expect(wrapper.find('.voice-search-overlay').exists()).toBe(false)
    })

    it('visible 为 true 时显示弹窗', () => {
      const wrapper = createWrapper({ visible: true })
      expect(wrapper.find('.voice-search-overlay').exists()).toBe(true)
    })
  })

  describe('组件交互', () => {
    it('点击关闭按钮触发 close 事件', async () => {
      const wrapper = createWrapper({ visible: true })
      await wrapper.find('.close-btn').trigger('click')
      expect(wrapper.emitted('close')).toBeTruthy()
    })

    it('点击遮罩层触发 close 事件', async () => {
      const wrapper = createWrapper({ visible: true })
      await wrapper.find('.voice-search-overlay').trigger('click')
      expect(wrapper.emitted('close')).toBeTruthy()
    })

    it('按下回车键触发搜索', async () => {
      const wrapper = createWrapper({ visible: true })
      const input = wrapper.find('.search-input')
      await input.setValue('测试搜索')
      await wrapper.find('.search-input').trigger('keyup.enter')
      expect(wrapper.emitted('search')).toBeTruthy()
    })
  })

  describe('Props', () => {
    it('visible 为 true 时显示组件', () => {
      const wrapper = createWrapper({ visible: true })
      expect(wrapper.isVisible()).toBe(true)
    })

    it('visible 为 false 时隐藏组件', () => {
      const wrapper = createWrapper({ visible: false })
      expect(wrapper.find('.voice-search-overlay').exists()).toBe(false)
    })
  })
})