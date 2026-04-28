import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AiLoading from '../AiLoading.vue'

describe('AiLoading.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(AiLoading, {
      props: { ...props }
    })
  }

  describe('默认属性', () => {
    it('默认 size 为 medium', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('size')).toBe('medium')
    })

    it('默认 text 为"加载中..."', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('text')).toBe('加载中...')
    })

    it('medium 尺寸应用正确类名', () => {
      const wrapper = createWrapper({ size: 'medium' })
      expect(wrapper.find('.ai-loading.medium').exists()).toBe(true)
    })
  })

  describe('尺寸变体', () => {
    it('small 尺寸应用正确类名', () => {
      const wrapper = createWrapper({ size: 'small' })
      expect(wrapper.find('.ai-loading.small').exists()).toBe(true)
    })

    it('large 尺寸应用正确类名', () => {
      const wrapper = createWrapper({ size: 'large' })
      expect(wrapper.find('.ai-loading.large').exists()).toBe(true)
    })

    it('size prop 验证器接受有效值', () => {
      const wrapper1 = createWrapper({ size: 'small' })
      const wrapper2 = createWrapper({ size: 'medium' })
      const wrapper3 = createWrapper({ size: 'large' })
      expect(wrapper1.props('size')).toBe('small')
      expect(wrapper2.props('size')).toBe('medium')
      expect(wrapper3.props('size')).toBe('large')
    })
  })

  describe('加载文字', () => {
    it('显示自定义加载文字', () => {
      const wrapper = createWrapper({ text: '正在处理...' })
      expect(wrapper.find('.loading-text').text()).toBe('正在处理...')
    })

    it('不传 text 时不显示文字', () => {
      const wrapper = createWrapper({ text: '' })
      expect(wrapper.find('.loading-text').exists()).toBe(false)
    })

    it('text 为 null 时不显示文字', () => {
      const wrapper = createWrapper({ text: null })
      expect(wrapper.find('.loading-text').exists()).toBe(false)
    })
  })

  describe('结构验证', () => {
    it('包含三个加载点', () => {
      const wrapper = createWrapper()
      const dots = wrapper.findAll('.dot')
      expect(dots.length).toBe(3)
    })

    it('加载容器结构正确', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.ai-loading').exists()).toBe(true)
      expect(wrapper.find('.loading-dots').exists()).toBe(true)
    })
  })
})