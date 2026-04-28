import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import CreationPage from '../CreationPage.vue'

describe('CreationPage.vue', () => {
  const createWrapper = (props = {}) => {
    return mount(CreationPage, {
      props: {
        currentUser: { id: 1, username: 'testuser' },
        editingPost: null,
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
    it('默认激活视频标签', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.activeTab).toBe('video')
    })

    it('默认发布类型为立即发布', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.publishType).toBe('immediate')
    })

    it('图片列表初始为空', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.imageFiles).toEqual([])
      expect(wrapper.vm.imageUrls).toEqual([])
    })

    it('未选择标签', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.selectedTag).toBe('')
    })
  })

  describe('标签选择', () => {
    it('预置标签列表包含10个标签', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.tags).toHaveLength(10)
    })

    it('可以选择预置标签', async () => {
      const wrapper = createWrapper()
      wrapper.vm.selectedTag = '穿搭'
      expect(wrapper.vm.selectedTag).toBe('穿搭')
    })

    it('isCustomTag 在选择自定义标签时返回 true', () => {
      const wrapper = createWrapper()
      wrapper.vm.selectedTag = '#自定义标签'
      expect(wrapper.vm.isCustomTag).toBe(true)
    })

    it('isCustomTag 在选择预置标签时返回 false', () => {
      const wrapper = createWrapper()
      wrapper.vm.selectedTag = '美食'
      expect(wrapper.vm.isCustomTag).toBe(false)
    })
  })

  describe('自定义标签', () => {
    it('toggleCustomTagInput 切换显示状态', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.showCustomTagInput).toBe(false)
      wrapper.vm.toggleCustomTagInput()
      expect(wrapper.vm.showCustomTagInput).toBe(true)
      wrapper.vm.toggleCustomTagInput()
      expect(wrapper.vm.showCustomTagInput).toBe(false)
    })

    it('selectCustomTag 设置带 # 的标签', () => {
      const wrapper = createWrapper()
      wrapper.vm.showCustomTagInput = true
      wrapper.vm.customTagInput = '测试标签'
      wrapper.vm.selectCustomTag()
      expect(wrapper.vm.selectedTag).toBe('#测试标签')
    })

    it('selectCustomTag 忽略空白标签', () => {
      const wrapper = createWrapper()
      wrapper.vm.showCustomTagInput = true
      wrapper.vm.customTagInput = '   '
      wrapper.vm.selectCustomTag()
      expect(wrapper.vm.selectedTag).toBe('')
    })

    it('已选标签时不能选自定义标签', () => {
      const wrapper = createWrapper()
      wrapper.vm.selectedTag = '穿搭'
      wrapper.vm.showCustomTagInput = false
      wrapper.vm.toggleCustomTagInput()
      expect(wrapper.vm.selectedTag).toBe('穿搭')
    })
  })

  describe('图片处理', () => {
    it('removeImage 移除指定索引的图片', () => {
      const wrapper = createWrapper()
      wrapper.vm.imageFiles = ['file1', 'file2']
      wrapper.vm.imageUrls = ['url1', 'url2']
      wrapper.vm.imageInfos = [{ size: '1MB' }, { size: '2MB' }]
      wrapper.vm.removeImage(0)
      expect(wrapper.vm.imageFiles).toHaveLength(1)
      expect(wrapper.vm.imageUrls).toHaveLength(1)
      expect(wrapper.vm.imageUrls[0]).toBe('url2')
    })
  })

  describe('文件大小格式化', () => {
    it('小于1MB返回KB单位', () => {
      const wrapper = createWrapper()
      const size = wrapper.vm.formatFileSize(512 * 1024)
      expect(size).toBe('512.00 KB')
    })

    it('大于等于1MB返回MB单位', () => {
      const wrapper = createWrapper()
      const size = wrapper.vm.formatFileSize(2 * 1024 * 1024)
      expect(size).toBe('2.00 MB')
    })
  })

  describe('视频时长格式化', () => {
    it('正确格式化时长', () => {
      const wrapper = createWrapper()
      const duration = wrapper.vm.formatDuration(125)
      expect(duration).toBe('2min 5s')
    })

    it('处理0秒', () => {
      const wrapper = createWrapper()
      const duration = wrapper.vm.formatDuration(0)
      expect(duration).toBe('0min 0s')
    })
  })
})