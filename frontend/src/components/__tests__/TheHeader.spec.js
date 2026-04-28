import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TheHeader from '../TheHeader.vue'

vi.mock('./VoiceSearch.vue', () => ({
  default: {
    name: 'VoiceSearch',
    props: ['visible'],
    emits: ['close', 'search'],
    template: '<div class="mock-voice-search"></div>'
  }
}))

describe('TheHeader.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(TheHeader, {
      props: { currentUser: null, ...props },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  describe('初始状态', () => {
    it('搜索框初始为空', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.searchQuery).toBe('')
    })

    it('语音搜索弹窗初始关闭', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.showVoiceSearch).toBe(false)
    })
  })

  describe('搜索功能', () => {
    it('输入搜索内容', async () => {
      const wrapper = createWrapper()
      await wrapper.find('.search-input').setValue('测试搜索')
      expect(wrapper.vm.searchQuery).toBe('测试搜索')
    })

    it('点击搜索按钮触发 search 事件', async () => {
      const wrapper = createWrapper()
      await wrapper.find('.search-input').setValue('测试')
      await wrapper.find('.search-btn').trigger('click')
      expect(wrapper.emitted('search')).toBeTruthy()
      expect(wrapper.emitted('search')[0]).toEqual(['测试'])
    })

    it('按下回车触发搜索', async () => {
      const wrapper = createWrapper()
      await wrapper.find('.search-input').setValue('测试')
      await wrapper.find('.search-input').trigger('keyup.enter')
      expect(wrapper.emitted('search')).toBeTruthy()
    })
  })

  describe('语音搜索', () => {
    it('点击麦克风按钮打开语音搜索', async () => {
      const wrapper = createWrapper()
      await wrapper.find('.voice-btn').trigger('click')
      expect(wrapper.vm.showVoiceSearch).toBe(true)
    })
  })

  describe('placeholder 文本', () => {
    it('未登录时显示"登录探索更多内容"', () => {
      const wrapper = createWrapper({ currentUser: null })
      expect(wrapper.vm.placeholderText).toBe('登录探索更多内容')
    })

    it('登录后显示"搜索趣生活"', () => {
      const wrapper = createWrapper({ currentUser: { id: 1, username: 'test' } })
      expect(wrapper.vm.placeholderText).toBe('搜索趣生活')
    })
  })

  describe('Props', () => {
    it('正确接收 currentUser', () => {
      const user = { id: 1, username: 'testuser' }
      const wrapper = createWrapper({ currentUser: user })
      expect(wrapper.props('currentUser')).toEqual(user)
    })
  })
})