import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import CompleteProfileModal from '../CompleteProfileModal.vue'

describe('CompleteProfileModal.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(CompleteProfileModal, {
      props: {
        user: { id: 1, username: 'test', avatar: '' },
        ...props
      },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  const mockUser = { id: 1, username: 'testuser', nickname: '', avatar: '/avatar.jpg' }

  beforeEach(() => {
    global.fetch = vi.fn()
  })

  describe('初始状态', () => {
    it('nickname 初始为空', () => {
      const wrapper = createWrapper({ user: mockUser })
      expect(wrapper.vm.nickname).toBe('')
    })

    it('gender 默认为"保密"', () => {
      const wrapper = createWrapper({ user: mockUser })
      expect(wrapper.vm.gender).toBe('保密')
    })

    it('avatarFile 初始为 null', () => {
      const wrapper = createWrapper({ user: mockUser })
      expect(wrapper.vm.avatarFile).toBe(null)
    })

    it('loading 初始为 false', () => {
      const wrapper = createWrapper({ user: mockUser })
      expect(wrapper.vm.loading).toBe(false)
    })

    it('errorMessage 初始为空', () => {
      const wrapper = createWrapper({ user: mockUser })
      expect(wrapper.vm.errorMessage).toBe('')
    })
  })

  describe('头像预览', () => {
    it('使用用户现有头像作为预览', () => {
      const wrapper = createWrapper({ user: mockUser })
      expect(wrapper.vm.avatarPreview).toBe(mockUser.avatar)
    })

    it('用户无头像时预览为空', () => {
      const wrapper = createWrapper({ user: { ...mockUser, avatar: '' } })
      expect(wrapper.vm.avatarPreview).toBe('')
    })
  })

  describe('文件上传', () => {
    it('文件大小超过2MB显示错误', async () => {
      const wrapper = createWrapper({ user: mockUser })
      const file = new File([new ArrayBuffer(3 * 1024 * 1024)], 'test.jpg', { type: 'image/jpeg' })
      const event = { target: { files: [file] } }
      await wrapper.vm.handleFileChange(event)
      expect(wrapper.vm.errorMessage).toBe('图片大小不能超过 2MB')
    })
  })

  describe('表单验证', () => {
    it('昵称为空时显示错误', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({})
      })
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: 1, nickname: 'test' })
      })
      const wrapper = createWrapper({ user: mockUser })
      wrapper.vm.nickname = ''
      await wrapper.vm.handleSubmit()
      expect(wrapper.vm.errorMessage).toBe('请输入昵称')
    })

    it('昵称有效时不显示错误', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({})
      })
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: 1, nickname: 'test' })
      })
      const wrapper = createWrapper({ user: mockUser })
      wrapper.vm.nickname = 'testuser'
      wrapper.vm.avatarFile = null
      await wrapper.vm.handleSubmit()
      expect(wrapper.vm.errorMessage).toBe('')
    })
  })

  describe('表单提交', () => {
    it('成功更新后触发 complete 事件', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ url: '/new-avatar.jpg' })
      })
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: 1, nickname: 'TestUser', avatar: '/new-avatar.jpg' })
      })
      const wrapper = createWrapper({ user: mockUser })
      wrapper.vm.nickname = 'TestUser'
      await wrapper.vm.handleSubmit()
      expect(wrapper.emitted('complete')).toBeTruthy()
    })
  })

  describe('Props', () => {
    it('正确接收 user', () => {
      const wrapper = createWrapper({ user: mockUser })
      expect(wrapper.props('user')).toEqual(mockUser)
    })
  })
})