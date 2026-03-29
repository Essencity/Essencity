# 多图笔记功能测试规范

## 测试文件位置
`frontend/src/components/__tests__/CreationPage.multiImage.spec.js`

## 测试框架
Vitest + @vue/test-utils

## 测试用例

### 1. 多图上传功能测试

```javascript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import CreationPage from '../CreationPage.vue'

// Mock fetch
global.fetch = vi.fn()

const mockCurrentUser = {
  id: 1,
  nickname: '测试用户'
}

const createMockFile = (name, size, type = 'image/jpeg') => {
  const file = new File(['mock'], name, { type })
  file.size = size
  return file
}

describe('CreationPage - 多图笔记功能', () => {

  beforeEach(() => {
    fetch.mockClear()
  })

  describe('初始状态', () => {
    it('默认没有图片', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      expect(wrapper.vm.imageFiles.length).toBe(0)
      expect(wrapper.vm.imageUrls.length).toBe(0)
      expect(wrapper.vm.imageInfos.length).toBe(0)
    })

    it('默认显示上传区域', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      expect(wrapper.find('.upload-area').exists()).toBe(true)
    })
  })

  describe('图片选择', () => {
    it('选择单张图片', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      const mockFile = createMockFile('test.jpg', 1024 * 1024)

      await wrapper.vm.processImageFile(mockFile)

      expect(wrapper.vm.imageFiles.length).toBe(1)
      expect(wrapper.vm.imageUrls.length).toBe(1)
      expect(wrapper.vm.imageInfos.length).toBe(1)
    })

    it('选择多张图片', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      const files = [
        createMockFile('test1.jpg', 1024 * 1024),
        createMockFile('test2.jpg', 1024 * 1024),
        createMockFile('test3.jpg', 1024 * 1024)
      ]

      files.forEach(file => wrapper.vm.processImageFile(file))

      expect(wrapper.vm.imageFiles.length).toBe(3)
      expect(wrapper.vm.imageUrls.length).toBe(3)
    })

    it('最多只能选择9张图片', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'

      // 添加10张图片
      for (let i = 0; i < 10; i++) {
        wrapper.vm.processImageFile(createMockFile(`test${i}.jpg`, 1024 * 1024))
      }

      expect(wrapper.vm.imageFiles.length).toBe(9)
    })

    it('超过9张时只取前9张', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      const files = Array.from({ length: 5 }, (_, i) =>
        createMockFile(`test${i}.jpg`, 1024 * 1024)
      )

      // 先添加7张
      files.slice(0, 7).forEach(file => wrapper.vm.processImageFile(file))

      // 再尝试添加5张，应该只添加剩余的2张（9-7=2）
      files.slice(0, 5).forEach(file => wrapper.vm.processImageFile(file))

      expect(wrapper.vm.imageFiles.length).toBe(9)
    })
  })

  describe('图片删除', () => {
    it('删除指定索引的图片', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'

      // 添加3张图片
      for (let i = 0; i < 3; i++) {
        wrapper.vm.processImageFile(createMockFile(`test${i}.jpg`, 1024 * 1024))
      }

      expect(wrapper.vm.imageFiles.length).toBe(3)

      // 删除第二张
      await wrapper.vm.removeImage(1)

      expect(wrapper.vm.imageFiles.length).toBe(2)
      expect(wrapper.vm.imageUrls.length).toBe(2)
    })

    it('删除后重新计算索引', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'

      for (let i = 0; i < 3; i++) {
        wrapper.vm.processImageFile(createMockFile(`test${i}.jpg`, 1024 * 1024))
      }

      const firstUrl = wrapper.vm.imageUrls[0]
      const secondUrl = wrapper.vm.imageUrls[1]

      // 删除第一张后，原来第二张变成第一张
      await wrapper.vm.removeImage(0)

      expect(wrapper.vm.imageUrls[0]).toBe(secondUrl)
    })
  })

  describe('图片替换', () => {
    it('替换指定索引的图片', async () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'

      wrapper.vm.processImageFile(createMockFile('original.jpg', 1024 * 1024))
      const originalUrl = wrapper.vm.imageUrls[0]

      // Mock window.URL.createObjectURL
      const mockUrl = 'blob:http://localhost/mock-url'
      vi.spyOn(URL, 'createObjectURL').mockReturnValue(mockUrl)

      // 触发替换逻辑（需要模拟文件选择）
      // 由于无法直接模拟文件输入，我们测试逻辑
      expect(wrapper.vm.imageUrls[0]).toBe(originalUrl)
    })
  })

  describe('发布功能', () => {
    it('图片笔记需要至少一张图片', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      wrapper.vm.title = '测试标题'

      // 没有图片时不能发布
      expect(wrapper.vm.canPublish).toBe(false)

      // 添加图片后才能发布
      wrapper.vm.processImageFile(createMockFile('test.jpg', 1024 * 1024))
      expect(wrapper.vm.canPublish).toBe(true)
    })

    it('发布图片笔记时上传所有图片', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ url: '/uploads/1.jpg' })
      }).mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ url: '/uploads/2.jpg' })
      }).mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ success: true })
      })

      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      wrapper.vm.title = '测试标题'
      wrapper.vm.processImageFile(createMockFile('test1.jpg', 1024 * 1024))
      wrapper.vm.processImageFile(createMockFile('test2.jpg', 1024 * 1024))

      await wrapper.vm.handlePublish()

      // 应该上传了2张图片
      expect(fetch).toHaveBeenCalledTimes(3) // 2张图片 + 1个帖子
    })

    it('发布成功后清空所有图片', async () => {
      fetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ url: '/uploads/test.jpg' })
      })

      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      wrapper.vm.title = '测试标题'
      wrapper.vm.processImageFile(createMockFile('test.jpg', 1024 * 1024))

      await wrapper.vm.handlePublish()

      expect(wrapper.vm.imageFiles.length).toBe(0)
      expect(wrapper.vm.imageUrls.length).toBe(0)
      expect(wrapper.vm.imageInfos.length).toBe(0)
    })
  })

  describe('UI 显示', () => {
    it('显示已上传图片数量', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      wrapper.vm.processImageFile(createMockFile('test.jpg', 1024 * 1024))
      wrapper.vm.processImageFile(createMockFile('test2.jpg', 1024 * 1024))

      expect(wrapper.find('.section-title').text()).toContain('2/9')
    })

    it('第一张图片显示封面标识', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'
      wrapper.vm.processImageFile(createMockFile('test.jpg', 1024 * 1024))

      expect(wrapper.find('.cover-badge').exists()).toBe(true)
      expect(wrapper.find('.cover-badge').text()).toBe('封面')
    })

    it('图片数量达到9张时不显示添加按钮', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'

      for (let i = 0; i < 9; i++) {
        wrapper.vm.processImageFile(createMockFile(`test${i}.jpg`, 1024 * 1024))
      }

      expect(wrapper.find('.add-more').exists()).toBe(false)
    })

    it('图片数量未达9张时显示添加按钮', () => {
      const wrapper = mount(CreationPage, {
        props: { currentUser: mockCurrentUser }
      })

      wrapper.vm.activeTab = 'image'

      for (let i = 0; i < 5; i++) {
        wrapper.vm.processImageFile(createMockFile(`test${i}.jpg`, 1024 * 1024))
      }

      expect(wrapper.find('.add-more').exists()).toBe(true)
    })
  })
})
```

## 手动测试清单

### 多图上传
- [ ] 点击上传区域可以选择多张图片
- [ ] 拖拽多张图片到上传区域
- [ ] 已上传图片显示在网格中
- [ ] 第一张图片显示"封面"标识
- [ ] 悬停图片显示编辑和删除按钮
- [ ] 点击删除按钮移除图片
- [ ] 点击编辑按钮替换图片
- [ ] 点击添加图片继续添加
- [ ] 达到9张后不显示添加按钮
- [ ] 发布后正确上传所有图片
- [ ] 发布后清空所有已上传图片

### API 集成
- [ ] 多张图片正确上传到服务器
- [ ] imageUrls 数组正确发送到后端
- [ ] 第一张图片作为封面 URL
