import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TheSidebar from '../TheSidebar.vue'

describe('TheSidebar.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(TheSidebar, {
      props: { activeItem: 'discovery', ...props },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  describe('初始状态', () => {
    it('更多菜单初始关闭', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.showMoreMenu).toBe(false)
    })

    it('暗色模式根据 localStorage 设置', () => {
      localStorage.setItem('theme', 'dark')
      const wrapper = createWrapper()
      expect(wrapper.vm.isDarkMode).toBe(true)
      localStorage.setItem('theme', 'light')
    })
  })

  describe('菜单项', () => {
    it('包含发现、发布、通知三个菜单', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.menuItems).toHaveLength(3)
    })

    it('菜单项包含正确 id', () => {
      const wrapper = createWrapper()
      const ids = wrapper.vm.menuItems.map(item => item.id)
      expect(ids).toContain('discovery')
      expect(ids).toContain('publish')
      expect(ids).toContain('notification')
    })
  })

  describe('更多菜单', () => {
    it('点击切换更多菜单显示状态', async () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.showMoreMenu).toBe(false)
      await wrapper.vm.toggleMoreMenu()
      expect(wrapper.vm.showMoreMenu).toBe(true)
      await wrapper.vm.toggleMoreMenu()
      expect(wrapper.vm.showMoreMenu).toBe(false)
    })

    it('退出登录触发 logout 事件', async () => {
      const wrapper = createWrapper()
      wrapper.vm.showMoreMenu = true
      await wrapper.vm.handleLogout()
      expect(wrapper.emitted('logout')).toBeTruthy()
    })
  })

  describe('暗色模式', () => {
    it('切换暗色模式', async () => {
      localStorage.setItem('theme', 'light')
      const wrapper = createWrapper()
      expect(wrapper.vm.isDarkMode).toBe(false)
      await wrapper.vm.toggleDarkMode()
      expect(wrapper.vm.isDarkMode).toBe(true)
      localStorage.setItem('theme', 'light')
    })
  })

  describe('头像 URL 处理', () => {
    it('空 URL 返回空字符串', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getImageUrl('')).toBe('')
    })

    it('http URL 直接返回', () => {
      const wrapper = createWrapper()
      const url = 'http://example.com/avatar.jpg'
      expect(wrapper.vm.getImageUrl(url)).toBe(url)
    })

    it('相对路径添加 /api 前缀', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getImageUrl('/uploads/avatar.jpg')).toBe('/api/uploads/avatar.jpg')
    })
  })

  describe('Props', () => {
    it('正确接收 activeItem', () => {
      const wrapper = createWrapper({ activeItem: 'notification' })
      expect(wrapper.props('activeItem')).toBe('notification')
    })

    it('正确接收 currentUser', () => {
      const user = { id: 1, username: 'test' }
      const wrapper = createWrapper({ currentUser: user })
      expect(wrapper.props('currentUser')).toEqual(user)
    })
  })
})