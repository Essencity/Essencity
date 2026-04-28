import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import CategoryTabs from '../CategoryTabs.vue'

describe('CategoryTabs.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(CategoryTabs, {
      props: { ...props },
      global: {
        stubs: {
          teleport: true
        }
      }
    })
  }

  describe('初始状态', () => {
    it('默认选中"推荐"分类', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.activeCategory).toBe('推荐')
    })

    it('默认推荐分类有 active 类名', () => {
      const wrapper = createWrapper()
      const recommendedBtn = wrapper.find('.tab-btn:first-child')
      expect(recommendedBtn.classes()).toContain('active')
    })
  })

  describe('分类列表', () => {
    it('包含所有预设分类', () => {
      const wrapper = createWrapper()
      const expectedCategories = ['推荐', '穿搭', '美食', '彩妆', '影视', '职场', '情感', '家居', '游戏', '旅行', '健身']
      expect(wrapper.vm.categories).toEqual(expectedCategories)
    })

    it('总共11个分类', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.categories).toHaveLength(11)
    })
  })

  describe('分类切换', () => {
    it('点击"美食"分类触发 change 事件', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAll('.tab-btn')
      await buttons[2].trigger('click')
      expect(wrapper.emitted('change')).toBeTruthy()
      expect(wrapper.emitted('change')[0]).toEqual(['美食'])
    })

    it('点击后 activeCategory 更新', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAll('.tab-btn')
      await buttons[3].trigger('click')
      expect(wrapper.vm.activeCategory).toBe('彩妆')
    })
  })

  describe('Props', () => {
    it('默认 activeCategory 为"推荐"', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('activeCategory')).toBeUndefined()
    })
  })
})