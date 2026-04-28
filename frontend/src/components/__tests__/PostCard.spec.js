import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import PostCard from '../PostCard.vue'

describe('PostCard.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(PostCard, {
      props: {
        post: {
          id: 1,
          title: '测试帖子',
          image: '/test.jpg',
          user: '测试用户',
          avatar: '/avatar.jpg',
          likes: 100,
          type: 'image',
          ...props.post
        },
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
    it('正确显示帖子标题', () => {
      const wrapper = createWrapper({
        post: { title: '测试标题', image: '/img.jpg', user: '用户', avatar: '/av.jpg', likes: 10, type: 'image' }
      })
      expect(wrapper.find('.post-title').text()).toBe('测试标题')
    })

    it('正确显示用户名', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户名', avatar: '/av.jpg', likes: 10, type: 'image' }
      })
      expect(wrapper.find('.username').text()).toBe('用户名')
    })
  })

  describe('图片处理', () => {
    it('图片使用 getMediaUrl 处理', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/uploads/test.jpg', user: '用户', avatar: '/av.jpg', likes: 10, type: 'image' }
      })
      const img = wrapper.find('.cover-image')
      expect(img.attributes('src')).toBe('/api/uploads/test.jpg')
    })

    it('完整 URL 直接使用', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: 'http://example.com/test.jpg', user: '用户', avatar: '/av.jpg', likes: 10, type: 'image' }
      })
      const img = wrapper.find('.cover-image')
      expect(img.attributes('src')).toBe('http://example.com/test.jpg')
    })
  })

  describe('头像处理', () => {
    it('空头像返回默认头像', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户', avatar: '', likes: 10, type: 'image' }
      })
      const avatar = wrapper.find('.avatar')
      expect(avatar.attributes('src')).toBeTruthy()
    })

    it('相对路径添加 /api 前缀', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户', avatar: '/avatar.jpg', likes: 10, type: 'image' }
      })
      const avatar = wrapper.find('.avatar')
      expect(avatar.attributes('src')).toBe('/api/avatar.jpg')
    })
  })

  describe('点赞数格式化', () => {
    it('小于10000直接显示', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户', avatar: '/av.jpg', likes: 9999, type: 'image' }
      })
      expect(wrapper.find('.like-count').text()).toBe('9999')
    })

    it('大于等于10000显示万', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户', avatar: '/av.jpg', likes: 36000, type: 'image' }
      })
      expect(wrapper.find('.like-count').text()).toBe('3.6万')
    })

    it('正好10000显示1.0万', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户', avatar: '/av.jpg', likes: 10000, type: 'image' }
      })
      expect(wrapper.find('.like-count').text()).toBe('1.0万')
    })
  })

  describe('视频标识', () => {
    it('视频类型显示播放图标', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户', avatar: '/av.jpg', likes: 10, type: 'video' }
      })
      expect(wrapper.find('.video-badge').exists()).toBe(true)
    })

    it('图片类型不显示播放图标', () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户', avatar: '/av.jpg', likes: 10, type: 'image' }
      })
      expect(wrapper.find('.video-badge').exists()).toBe(false)
    })
  })

  describe('点击事件', () => {
    it('点击卡片触发 open-detail 事件', async () => {
      const wrapper = createWrapper({
        post: { title: '测试', image: '/img.jpg', user: '用户', avatar: '/av.jpg', likes: 10, type: 'image' }
      })
      await wrapper.find('.post-card').trigger('click')
      expect(wrapper.emitted('open-detail')).toBeTruthy()
    })
  })
})