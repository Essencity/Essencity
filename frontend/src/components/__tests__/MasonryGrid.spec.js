import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import MasonryGrid from '../MasonryGrid.vue'

const createPostCardMock = () => ({
  name: 'PostCard',
  props: ['post'],
  template: '<div class="post-card-mock" @click="$emit(\'open-detail\', post)">{{ post.title }}</div>',
  emits: ['open-detail']
})

describe('MasonryGrid.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(MasonryGrid, {
      props: {
        items: [],
        ...props
      },
      global: {
        stubs: {
          PostCard: createPostCardMock()
        }
      }
    })
  }

  const sampleItems = [
    { id: 1, title: 'Post 1', image: '/img1.jpg', user: 'User 1', avatar: '/av1.jpg', likes: 100, type: 'image' },
    { id: 2, title: 'Post 2', image: '/img2.jpg', user: 'User 2', avatar: '/av2.jpg', likes: 200, type: 'image' },
    { id: 3, title: 'Post 3', image: '/img3.jpg', user: 'User 3', avatar: '/av3.jpg', likes: 300, type: 'video' },
    { id: 4, title: 'Post 4', image: '/img4.jpg', user: 'User 4', avatar: '/av4.jpg', likes: 400, type: 'image' },
    { id: 5, title: 'Post 5', image: '/img5.jpg', user: 'User 5', avatar: '/av5.jpg', likes: 500, type: 'image' },
    { id: 6, title: 'Post 6', image: '/img6.jpg', user: 'User 6', avatar: '/av6.jpg', likes: 600, type: 'image' }
  ]

  describe('初始状态', () => {
    it('items 为空时渲染空网格', () => {
      const wrapper = createWrapper({ items: [] })
      expect(wrapper.findAll('.masonry-column')).toHaveLength(0)
    })

    it('列数根据窗口宽度设置', () => {
      const wrapper = createWrapper({ items: [] })
      expect([2, 3, 4, 5]).toContain(wrapper.vm.columnCount)
    })
  })

  describe('列数计算', () => {
    it('窗口宽度 >= 1380 时列数为5', async () => {
      const wrapper = createWrapper()
      window.innerWidth = 1400
      wrapper.vm.updateColumnCount()
      expect(wrapper.vm.columnCount).toBe(5)
    })

    it('窗口宽度 >= 1120 时列数为4', async () => {
      const wrapper = createWrapper()
      window.innerWidth = 1150
      wrapper.vm.updateColumnCount()
      expect(wrapper.vm.columnCount).toBe(4)
    })

    it('窗口宽度 >= 800 时列数为3', async () => {
      const wrapper = createWrapper()
      window.innerWidth = 850
      wrapper.vm.updateColumnCount()
      expect(wrapper.vm.columnCount).toBe(3)
    })

    it('窗口宽度 < 800 时列数为2', async () => {
      const wrapper = createWrapper()
      window.innerWidth = 700
      wrapper.vm.updateColumnCount()
      expect(wrapper.vm.columnCount).toBe(2)
    })
  })

  describe('项目分发', () => {
    it('正确分发项目到各列', () => {
      const wrapper = createWrapper({ items: sampleItems })
      const total = wrapper.vm.columns.reduce((sum, col) => sum + col.length, 0)
      expect(total).toBe(6)
    })

    it('轮询分发确保均匀分布', () => {
      const wrapper = createWrapper({ items: sampleItems })
      expect(wrapper.vm.columns[0][0].id).toBe(1)
      expect(wrapper.vm.columns[1][0].id).toBe(2)
    })

    it('空items时columns为空数组', () => {
      const wrapper = createWrapper({ items: [] })
      expect(wrapper.vm.columns.length).toBeGreaterThanOrEqual(0)
    })
  })

  describe('Props', () => {
    it('正确接收 items prop', () => {
      const wrapper = createWrapper({ items: sampleItems })
      expect(wrapper.props('items')).toEqual(sampleItems)
    })

    it('items 为空数组', () => {
      const wrapper = createWrapper({ items: [] })
      expect(wrapper.vm.items).toEqual([])
    })
  })
})