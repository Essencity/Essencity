import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ProfilePage from '../ProfilePage.vue'

vi.mock('../TheSidebar.vue', () => ({
  default: {
    name: 'TheSidebar',
    props: ['activeItem', 'currentUser'],
    template: '<div class="mock-sidebar"></div>'
  }
}))

describe('ProfilePage.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(ProfilePage, {
      props: {
        currentUser: { id: 1, username: 'test', nickname: 'Test User', avatar: '/avatar.jpg' },
        ...props
      },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  const mockUser = { id: 1, username: 'testuser', nickname: 'Test User', avatar: '/avatar.jpg' }

  beforeEach(() => {
    global.fetch = vi.fn()
  })

  describe('初始状态', () => {
    it('默认标签为"笔记"', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.activeTab).toBe('notes')
    })

    it('showEditModal 初始为 false', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.showEditModal).toBe(false)
    })

    it('isSaving 初始为 false', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.isSaving).toBe(false)
    })

    it('用户帖子列表初始为空', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.userPosts).toEqual([])
    })
  })

  describe('Helper 函数', () => {
    it('fixUrl 处理 /api 前缀', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl('/api/uploads/test.jpg')).toBe('/uploads/test.jpg')
    })

    it('fixUrl 处理 http://localhost:3000', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl('http://localhost:3000/uploads/test.jpg')).toBe('/uploads/test.jpg')
    })

    it('fixUrl 处理 http://localhost:8080/api', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl('http://localhost:8080/api/uploads/test.jpg')).toBe('/uploads/test.jpg')
    })

    it('fixUrl 处理 /uploads 路径', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl('/uploads/test.jpg')).toBe('/uploads/test.jpg')
    })

    it('fixUrl 处理 null', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.fixUrl(null)).toBe(null)
    })

    it('getImageUrl 返回空字符串', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getImageUrl(null)).toBe('')
    })

    it('getImageUrl 处理 http URL', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getImageUrl('http://example.com/test.jpg')).toBe('http://example.com/test.jpg')
    })

    it('getImageUrl 添加 /api 前缀', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getImageUrl('/uploads/test.jpg')).toBe('/api/uploads/test.jpg')
    })

    it('getMediaUrl 处理 null', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl(null)).toBe(null)
    })

    it('getMediaUrl 处理 http URL', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl('http://example.com/test.jpg')).toBe('http://example.com/test.jpg')
    })

    it('getMediaUrl 处理 /api 路径', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl('/api/uploads/test.jpg')).toBe('/api/uploads/test.jpg')
    })

    it('getMediaUrl 添加 /api 前缀', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl('/uploads/test.jpg')).toBe('/api/uploads/test.jpg')
    })
  })

  describe('标签列表', () => {
    it('包含笔记、收藏、点赞三个标签', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.tabs).toHaveLength(3)
    })

    it('标签包含正确 id', () => {
      const wrapper = createWrapper()
      const ids = wrapper.vm.tabs.map(t => t.id)
      expect(ids).toContain('notes')
      expect(ids).toContain('favorites')
      expect(ids).toContain('likes')
    })
  })

  describe('性别选项', () => {
    it('包含男、女、保密', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.genderOptions).toEqual(['男', '女', '保密'])
    })
  })

  describe('Props', () => {
    it('正确接收 currentUser', () => {
      const wrapper = createWrapper({ currentUser: mockUser })
      expect(wrapper.props('currentUser')).toEqual(mockUser)
    })

    it('正确接收 isOwnProfile', () => {
      const wrapper = createWrapper({ isOwnProfile: false })
      expect(wrapper.props('isOwnProfile')).toBe(false)
    })

    it('isOwnProfile 默认为 true', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('isOwnProfile')).toBe(true)
    })
  })
})