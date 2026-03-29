# 话题标签系统测试规范

## 测试文件位置
`frontend/src/components/__tests__/CreationPage.tagSystem.spec.js`

## 测试框架
Vitest + @vue/test-utils

## 测试用例

### 1. 预设标签功能测试

```javascript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import CreationPage from '../CreationPage.vue'

const mockCurrentUser = {
  id: 1,
  nickname: '测试用户'
}

describe('CreationPage - 话题标签系统', () => {

  describe('预设标签', () => {
    it('显示所有预设标签', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      const presetTags = ['穿搭', '美食', '彩妆', '影视', '职场', '情感', '家居', '游戏', '旅行', '健身']
      const tagItems = wrapper.findAll('.tag-item')

      // 过滤掉自定义标签
      const presetTagItems = tagItems.filter(item => !item.classes('custom-trigger'))
      expect(presetTagItems.length).toBe(presetTags.length)
    })

    it('点击标签选中', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      const tagItems = wrapper.findAll('.tag-item')
      const firstTag = tagItems[0]

      await firstTag.trigger('click')

      expect(firstTag.classes('active')).toBe(true)
      expect(wrapper.vm.selectedTag).toBe(firstTag.text())
    })

    it('只能选择一个标签', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      const tagItems = wrapper.findAll('.tag-item')
      await tagItems[0].trigger('click')
      await tagItems[1].trigger('click')

      expect(tagItems[0].classes('active')).toBe(false)
      expect(tagItems[1].classes('active')).toBe(true)
      expect(wrapper.vm.selectedTag).toBe(tagItems[1].text())
    })

    it('再次点击已选标签取消选中', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      const tagItems = wrapper.findAll('.tag-item')
      await tagItems[0].trigger('click')
      expect(wrapper.vm.selectedTag).toBe(tagItems[0].text())

      await tagItems[0].trigger('click')
      expect(wrapper.vm.selectedTag).toBe('')
    })
  })

  describe('自定义标签', () => {
    it('显示自定义标签入口', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      const customTrigger = wrapper.find('.custom-trigger')
      expect(customTrigger.exists()).toBe(true)
      expect(customTrigger.text()).toBe('+ 自定义')
    })

    it('点击自定义入口显示输入框', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      const customTrigger = wrapper.find('.custom-trigger')
      await customTrigger.trigger('click')

      expect(wrapper.find('.custom-tag-input-wrapper').exists()).toBe(true)
      expect(wrapper.find('.custom-tag-input').exists()).toBe(true)
    })

    it('输入自定义标签', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      await wrapper.find('.custom-trigger').trigger('click')
      await wrapper.find('.custom-tag-input').setValue('测试话题')
      await wrapper.find('.custom-tag-input').trigger('keyup.enter')

      expect(wrapper.vm.selectedTag).toBe('#测试话题')
    })

    it('自定义标签自动添加 # 前缀', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      await wrapper.find('.custom-trigger').trigger('click')
      await wrapper.find('.custom-tag-input').setValue('测试话题')
      await wrapper.find('.custom-tag-input').trigger('keyup.enter')

      expect(wrapper.vm.selectedTag.startsWith('#')).toBe(true)
    })

    it('输入时自动过滤已有 # 前缀', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      await wrapper.find('.custom-trigger').trigger('click')
      await wrapper.find('.custom-tag-input').setValue('#测试话题')
      await wrapper.find('.custom-tag-input').trigger('keyup.enter')

      expect(wrapper.vm.selectedTag).toBe('#测试话题')
    })

    it('已有自定义标签时显示标签而非输入框', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.selectedTag = '#我的话题'

      expect(wrapper.find('.custom-tag').exists()).toBe(true)
      expect(wrapper.find('.custom-tag').text()).toBe('#我的话题')
      expect(wrapper.find('.custom-trigger').exists()).toBe(false)
    })

    it('点击已选自定义标签可重新编辑', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.selectedTag = '#我的话题'
      await wrapper.find('.custom-tag').trigger('click')

      expect(wrapper.find('.custom-tag-input-wrapper').exists()).toBe(true)
      expect(wrapper.find('.custom-tag-input').element.value).toBe('我的话题')
    })

    it('空白输入不创建标签', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      await wrapper.find('.custom-trigger').trigger('click')
      await wrapper.find('.custom-tag-input').setValue('   ')
      await wrapper.find('.custom-tag-input').trigger('keyup.enter')

      expect(wrapper.vm.selectedTag).toBe('')
      expect(wrapper.find('.custom-tag-input-wrapper').exists()).toBe(false)
    })
  })

  describe('标签与发布', () => {
    it('有标签时可以使用该标签发布', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      wrapper.vm.title = '测试标题'

      // Mock image file
      const mockFile = new File(['mock'], 'test.jpg', { type: 'image/jpeg' })
      mockFile.size = 1024 * 1024
      wrapper.vm.processImageFile(mockFile)

      const tagItems = wrapper.findAll('.tag-item')
      await tagItems[0].trigger('click')

      expect(wrapper.vm.canPublish).toBe(true)
    })

    it('发布时包含选中标签', async () => {
      global.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ url: '/uploads/test.jpg' })
      })

      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      wrapper.vm.title = '测试标题'

      const mockFile = new File(['mock'], 'test.jpg', { type: 'image/jpeg' })
      mockFile.size = 1024 * 1024
      wrapper.vm.processImageFile(mockFile)

      const tagItems = wrapper.findAll('.tag-item')
      await tagItems[2].trigger('click') // 选择"彩妆"

      await wrapper.vm.handlePublish()

      expect(global.fetch).toHaveBeenCalled()
      const postCall = global.fetch.mock.calls.find(call => call[0] === '/api/posts')
      expect(postCall).toBeDefined()
      const postBody = JSON.parse(postCall[1].body)
      expect(postBody.tag).toBe('彩妆')
    })

    it('自定义标签正确发送到后端', async () => {
      global.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ url: '/uploads/test.jpg' })
      })

      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      wrapper.vm.title = '测试标题'

      const mockFile = new File(['mock'], 'test.jpg', { type: 'image/jpeg' })
      mockFile.size = 1024 * 1024
      wrapper.vm.processImageFile(mockFile)

      await wrapper.find('.custom-trigger').trigger('click')
      await wrapper.find('.custom-tag-input').setValue('我的自定义话题')
      await wrapper.find('.custom-tag-input').trigger('keyup.enter')

      await wrapper.vm.handlePublish()

      const postCall = global.fetch.mock.calls.find(call => call[0] === '/api/posts')
      const postBody = JSON.parse(postCall[1].body)
      expect(postBody.tag).toBe('#我的自定义话题')
    })
  })

  describe('isCustomTag 计算属性', () => {
    it('预设标签返回 false', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.selectedTag = '美食'
      expect(wrapper.vm.isCustomTag).toBe(false)
    })

    it('自定义标签返回 true', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.selectedTag = '#我的话题'
      expect(wrapper.vm.isCustomTag).toBe(true)
    })

    it('无标签时返回 false', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.selectedTag = ''
      expect(wrapper.vm.isCustomTag).toBe(false)
    })
  })
})
```

## 手动测试清单

### 预设标签
- [ ] 显示所有10个预设标签
- [ ] 点击标签高亮选中
- [ ] 只能选中一个标签
- [ ] 再次点击取消选中
- [ ] 选中后背景色变化

### 自定义标签
- [ ] 显示"+ 自定义"入口
- [ ] 点击显示输入框
- [ ] 输入话题名称
- [ ] 回车确认创建
- [ ] 自动添加 # 前缀
- [ ] 空白输入不创建
- [ ] 已创建的自定义标签显示在列表
- [ ] 点击已选自定义标签可重新编辑

### 发布集成
- [ ] 有标签时可发布
- [ ] 预设标签正确发送
- [ ] 自定义标签正确发送
- [ ] 发布成功后标签清空
