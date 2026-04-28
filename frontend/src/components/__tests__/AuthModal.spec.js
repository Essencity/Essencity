import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import AuthModal from '../AuthModal.vue'

describe('AuthModal.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(AuthModal, {
      props: { ...props },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  beforeEach(() => {
    global.fetch = vi.fn()
  })

  describe('初始状态', () => {
    it('默认模式为登录', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.mode).toBe('login')
    })

    it('用户名和密码初始为空', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.username).toBe('')
      expect(wrapper.vm.password).toBe('')
    })

    it('错误信息初始为空', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.errorMessage).toBe('')
    })

    it('加载状态初始为 false', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.loading).toBe(false)
    })
  })

  describe('表单验证', () => {
    it('登录时用户名少于3位无效', () => {
      const wrapper = createWrapper()
      wrapper.vm.mode = 'login'
      wrapper.vm.username = 'ab'
      wrapper.vm.password = '123456'
      expect(wrapper.vm.isFormValid).toBe(false)
    })

    it('登录时密码少于6位无效', () => {
      const wrapper = createWrapper()
      wrapper.vm.mode = 'login'
      wrapper.vm.username = 'testuser'
      wrapper.vm.password = '12345'
      expect(wrapper.vm.isFormValid).toBe(false)
    })

    it('登录时用户名密码都有效', () => {
      const wrapper = createWrapper()
      wrapper.vm.mode = 'login'
      wrapper.vm.username = 'testuser'
      wrapper.vm.password = '123456'
      expect(wrapper.vm.isFormValid).toBe(true)
    })

    it('注册时确认密码不一致无效', () => {
      const wrapper = createWrapper()
      wrapper.vm.mode = 'register'
      wrapper.vm.username = 'testuser'
      wrapper.vm.password = '123456'
      wrapper.vm.confirmPassword = '123457'
      expect(wrapper.vm.isFormValid).toBe(false)
    })

    it('注册时所有字段有效', () => {
      const wrapper = createWrapper()
      wrapper.vm.mode = 'register'
      wrapper.vm.username = 'testuser'
      wrapper.vm.password = '123456'
      wrapper.vm.confirmPassword = '123456'
      expect(wrapper.vm.isFormValid).toBe(true)
    })
  })

  describe('模式切换', () => {
    it('switchMode 切换登录和注册', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.mode).toBe('login')
      wrapper.vm.switchMode()
      expect(wrapper.vm.mode).toBe('register')
      wrapper.vm.switchMode()
      expect(wrapper.vm.mode).toBe('login')
    })

    it('切换模式时清空错误信息', () => {
      const wrapper = createWrapper()
      wrapper.vm.errorMessage = 'some error'
      wrapper.vm.switchMode()
      expect(wrapper.vm.errorMessage).toBe('')
    })
  })

  describe('表单提交', () => {
    it('表单无效时不提交', async () => {
      const wrapper = createWrapper()
      wrapper.vm.username = 'ab'
      wrapper.vm.password = '123'
      await wrapper.vm.handleSubmit()
      expect(global.fetch).not.toHaveBeenCalled()
    })

    it('登录成功触发 login-success 事件', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: 1, username: 'test', avatar: '/avatar.jpg' })
      })
      const wrapper = createWrapper()
      wrapper.vm.username = 'testuser'
      wrapper.vm.password = '123456'
      await wrapper.vm.handleSubmit()
      expect(wrapper.emitted('login-success')).toBeTruthy()
    })

    it('登录失败显示错误信息', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: false,
        json: async () => ({ message: '用户名或密码错误' })
      })
      const wrapper = createWrapper()
      wrapper.vm.username = 'testuser'
      wrapper.vm.password = 'wrongpass'
      await wrapper.vm.handleSubmit()
      expect(wrapper.vm.errorMessage).toBe('用户名或密码错误')
    })
  })

  describe('关闭功能', () => {
    it('点击关闭按钮触发 close 事件', async () => {
      const wrapper = createWrapper()
      await wrapper.find('.close-btn').trigger('click')
      expect(wrapper.emitted('close')).toBeTruthy()
    })

    it('点击遮罩层触发 close 事件', async () => {
      const wrapper = createWrapper()
      await wrapper.find('.modal-overlay').trigger('click')
      expect(wrapper.emitted('close')).toBeTruthy()
    })
  })

  describe('UI 显示', () => {
    it('登录模式显示"登录"标题', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.modal-header h2').text()).toBe('登录')
    })

    it('注册模式显示"注册"标题', async () => {
      const wrapper = createWrapper()
      await wrapper.vm.switchMode()
      expect(wrapper.find('.modal-header h2').text()).toBe('注册')
    })

    it('显示注册时有确认密码字段', async () => {
      const wrapper = createWrapper()
      await wrapper.vm.switchMode()
      expect(wrapper.find('#confirmPassword').exists()).toBe(true)
    })

    it('登录时无确认密码字段', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('#confirmPassword').exists()).toBe(false)
    })
  })
})