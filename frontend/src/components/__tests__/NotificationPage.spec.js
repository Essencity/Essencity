import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import NotificationPage from '../NotificationPage.vue'

describe('NotificationPage.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(NotificationPage, {
      props: { currentUser: null, ...props },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  const mockUser = { id: 1, username: 'testuser', nickname: 'Test User' }

  beforeEach(() => {
    global.fetch = vi.fn()
  })

  describe('初始状态', () => {
    it('默认标签为"评论和回复"', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.activeTab).toBe('comments')
    })

    it('通知列表初始为空', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.notifications).toEqual([])
    })

    it('加载状态初始为 false', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.loading).toBe(false)
    })
  })

  describe('标签列表', () => {
    it('包含三个标签', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.tabs).toHaveLength(3)
    })

    it('标签包含正确 id', () => {
      const wrapper = createWrapper()
      const ids = wrapper.vm.tabs.map(t => t.id)
      expect(ids).toContain('comments')
      expect(ids).toContain('likes')
      expect(ids).toContain('follows')
    })
  })

  describe('获取通知', () => {
    it('无用户时不获取', async () => {
      const wrapper = createWrapper({ currentUser: null })
      await wrapper.vm.fetchNotifications()
      expect(global.fetch).not.toHaveBeenCalled()
    })

    it('有用户时获取通知', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => [{ id: 1, type: 'comment', content: '测试' }]
      })
      const wrapper = createWrapper({ currentUser: mockUser })
      await wrapper.vm.fetchNotifications()
      expect(global.fetch).toHaveBeenCalled()
    })
  })

  describe('时间格式化', () => {
    it('小于1分钟显示1分钟前', () => {
      const now = new Date()
      const recent = new Date(now.getTime() - 30 * 1000)
      const wrapper = createWrapper()
      expect(wrapper.vm.formatTime(recent.toISOString())).toBe('1分钟前')
    })

    it('小于1小时显示分钟前', () => {
      const now = new Date()
      const mins30 = new Date(now.getTime() - 30 * 60 * 1000)
      const wrapper = createWrapper()
      expect(wrapper.vm.formatTime(mins30.toISOString())).toBe('30分钟前')
    })

    it('小于24小时显示小时前', () => {
      const now = new Date()
      const hours5 = new Date(now.getTime() - 5 * 60 * 60 * 1000)
      const wrapper = createWrapper()
      expect(wrapper.vm.formatTime(hours5.toISOString())).toBe('5小时前')
    })

    it('超过24小时显示月-日', () => {
      const date = new Date()
      date.setDate(date.getDate() - 2)
      const wrapper = createWrapper()
      const result = wrapper.vm.formatTime(date.toISOString())
      expect(result).toMatch(/^\d+-\d+$/)
    })

    it('超过1年显示完整日期', () => {
      const date = new Date()
      date.setFullYear(date.getFullYear() - 2)
      const wrapper = createWrapper()
      const result = wrapper.vm.formatTime(date.toISOString())
      expect(result).toMatch(/^\d{4}-\d+-\d+$/)
    })
  })

  describe('Helper 函数', () => {
    it('fixUrl 处理 /api 前缀', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl('/api/uploads/test.jpg')).toBe('/uploads/test.jpg')
    })

    it('fixUrl 处理 /uploads 路径', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl('/uploads/test.jpg')).toBe('/uploads/test.jpg')
    })

    it('fixUrl 处理普通 URL', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl('http://example.com/test.jpg')).toBe('http://example.com/test.jpg')
    })

    it('fixUrl 处理 null', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl(null)).toBe(null)
    })

    it('getImageUrl 返回默认头像', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getImageUrl(null)).toBe('/default-avatar.png')
    })

    it('getImageUrl 处理相对路径', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getImageUrl('/uploads/test.jpg')).toBe('/api/uploads/test.jpg')
    })

    it('getImageUrl 处理完整 URL', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getImageUrl('http://example.com/test.jpg')).toBe('http://example.com/test.jpg')
    })

    it('getMediaUrl 返回相对路径', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl('/uploads/test.jpg')).toBe('/api/uploads/test.jpg')
    })

    it('getMediaUrl 处理 null', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl(null)).toBe(null)
    })
  })

  describe('Props', () => {
    it('正确接收 currentUser', () => {
      const wrapper = createWrapper({ currentUser: mockUser })
      expect(wrapper.props('currentUser')).toEqual(mockUser)
    })
  })
})