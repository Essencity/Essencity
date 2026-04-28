import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import PostDetailModal from '../PostDetailModal.vue'

vi.mock('./AiSummary.vue', () => ({
  default: {
    name: 'AiSummary',
    props: ['postId'],
    template: '<div class="mock-ai-summary"></div>'
  }
}))

describe('PostDetailModal.vue', () => {
  const createWrapper = (props = {}) => {
    const defaultPost = {
      id: 1,
      title: '测试帖子',
      content: '这是测试内容',
      image: '/test.jpg',
      type: 'image',
      likes: 100,
      likeCount: 100,
      likeStatus: false,
      collectionStatus: false,
      user: '测试用户',
      avatar: '/avatar.jpg'
    }

    return mount(PostDetailModal, {
      props: {
        post: defaultPost,
        currentUser: null,
        ...props
      },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  const mockPost = {
    id: 1,
    title: '测试帖子',
    content: '这是测试内容',
    image: '/test.jpg',
    type: 'image',
    likes: 100,
    likeCount: 100,
    likeStatus: false,
    collectionStatus: false,
    user: '测试用户',
    avatar: '/avatar.jpg'
  }

  const mockUser = { id: 1, username: 'test', nickname: 'Test User' }

  beforeEach(() => {
    global.fetch = vi.fn()
  })

  describe('初始状态', () => {
    it('isLiked 初始值来自 props', () => {
      const wrapper = createWrapper({ post: { ...mockPost, likeStatus: true } })
      expect(wrapper.vm.isLiked).toBe(true)
    })

    it('isCollected 初始值来自 props', () => {
      const wrapper = createWrapper({ post: { ...mockPost, collectionStatus: true } })
      expect(wrapper.vm.isCollected).toBe(true)
    })

    it('likeCount 初始值来自 props', () => {
      const wrapper = createWrapper({ post: { ...mockPost, likeCount: 500 } })
      expect(wrapper.vm.likeCount).toBe(500)
    })

    it('评论列表初始为空', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.comments).toEqual([])
    })

    it('newComment 初始为空', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.newComment).toBe('')
    })
  })

  describe('Helper 函数', () => {
    it('getAvatar 返回默认头像', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getAvatar(null)).toBeTruthy()
    })

    it('getAvatar 处理相对路径', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getAvatar('/avatar.jpg')).toBe('/api/avatar.jpg')
    })

    it('getAvatar 处理完整 URL', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getAvatar('http://example.com/avatar.jpg')).toBe('http://example.com/avatar.jpg')
    })

    it('getMediaUrl 处理相对路径', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl('/uploads/test.jpg')).toBe('/api/uploads/test.jpg')
    })

    it('getMediaUrl 处理完整 URL', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl('http://example.com/test.jpg')).toBe('http://example.com/test.jpg')
    })

    it('getMediaUrl 处理 null', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMediaUrl(null)).toBe(null)
    })
  })

  describe('点赞数格式化', () => {
    it('小于10000直接显示', () => {
      const wrapper = createWrapper({ post: { ...mockPost, likeCount: 9999 } })
      expect(wrapper.vm.formattedLikes).toBe(9999)
    })

    it('大于等于10000显示万', () => {
      const wrapper = createWrapper({ post: { ...mockPost, likeCount: 36000 } })
      expect(wrapper.vm.formattedLikes).toBe('3.6万')
    })
  })

  describe('收藏数格式化', () => {
    it('小于10000直接显示', () => {
      const wrapper = createWrapper({ post: { ...mockPost, collectionCount: 9999 } })
      expect(wrapper.vm.formattedCollects).toBe(9999)
    })

    it('大于等于10000显示万', () => {
      const wrapper = createWrapper({ post: { ...mockPost, collectionCount: 25000 } })
      expect(wrapper.vm.formattedCollects).toBe('2.5万')
    })
  })

  describe('AI 总结', () => {
    it('showAiSummary 初始为 false', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.showAiSummary).toBe(false)
    })

    it('切换 showAiSummary 状态', async () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.showAiSummary).toBe(false)
      wrapper.vm.showAiSummary = true
      expect(wrapper.vm.showAiSummary).toBe(true)
    })
  })

  describe('交互事件', () => {
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

  describe('Props', () => {
    it('正确接收 post', () => {
      const wrapper = createWrapper({ post: mockPost })
      expect(wrapper.props('post')).toEqual(mockPost)
    })

    it('正确接收 currentUser', () => {
      const wrapper = createWrapper({ currentUser: mockUser })
      expect(wrapper.props('currentUser')).toEqual(mockUser)
    })
  })
})