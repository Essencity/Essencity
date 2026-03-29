# 评论点赞功能测试规范

## 测试文件位置
`frontend/src/components/__tests__/PostDetailModal.commentLike.spec.js`

## 测试框架
Vitest + @vue/test-utils

## 测试用例

### 1. 评论点赞功能测试

```javascript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import PostDetailModal from '../PostDetailModal.vue'

// Mock fetch
global.fetch = vi.fn()

const mockPost = {
  id: 1,
  title: '测试帖子',
  description: '测试内容',
  type: 'image',
  imageUrl: '/test.jpg',
  author_id: 2,
  authorNickname: '测试作者',
  authorAvatar: '/avatar.jpg',
  likeStatus: false,
  collectionStatus: false,
  likeCount: 10,
  collectionCount: 5
}

const mockCurrentUser = {
  id: 1,
  nickname: '测试用户',
  avatar: '/user-avatar.jpg'
}

const mockComments = [
  {
    id: 101,
    user_id: 3,
    user: { id: 3, nickname: '评论者1', avatar: '/c1.jpg' },
    content: '这是一条测试评论',
    likeCount: 5,
    subComments: []
  },
  {
    id: 102,
    user_id: 4,
    user: { id: 4, nickname: '评论者2', avatar: '/c2.jpg' },
    content: '这是另一条测试评论',
    likeCount: 3,
    subComments: [
      {
        id: 201,
        user_id: 5,
        user: { id: 5, nickname: '回复者', avatar: '/r1.jpg' },
        content: '这是回复内容',
        likeCount: 2,
        replyToUser: { id: 4, nickname: '评论者2' }
      }
    ]
  }
]

describe('PostDetailModal - 评论点赞功能', () => {

  beforeEach(() => {
    fetch.mockClear()
  })

  describe('handleCommentLike', () => {
    it('点赞评论时调用正确的 API', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ success: true })
      })

      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      wrapper.vm.comments = JSON.parse(JSON.stringify(mockComments))
      await wrapper.vm.handleCommentLike(101)

      expect(fetch).toHaveBeenCalledWith('/api/comments/101/like', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: mockCurrentUser.id })
      })
    })

    it('取消点赞时调用 unlike API', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ success: true })
      })

      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      wrapper.vm.comments = JSON.parse(JSON.stringify(mockComments))
      wrapper.vm.commentLikes = { 101: true }
      await wrapper.vm.handleCommentLike(101)

      expect(fetch).toHaveBeenCalledWith('/api/comments/101/unlike', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: mockCurrentUser.id })
      })
    })

    it('点赞成功后更新本地状态', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ success: true })
      })

      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      wrapper.vm.comments = JSON.parse(JSON.stringify(mockComments))
      await wrapper.vm.handleCommentLike(101)

      expect(wrapper.vm.commentLikes[101]).toBe(true)
    })

    it('点赞成功后更新评论点赞数', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ success: true })
      })

      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      wrapper.vm.comments = JSON.parse(JSON.stringify(mockComments))
      const originalCount = wrapper.vm.comments[0].likeCount
      await wrapper.vm.handleCommentLike(101)

      expect(wrapper.vm.comments[0].likeCount).toBe(originalCount + 1)
    })

    it('未登录用户不能点赞', async () => {
      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: null
        }
      })

      wrapper.vm.comments = JSON.parse(JSON.stringify(mockComments))
      await wrapper.vm.handleCommentLike(101)

      expect(fetch).not.toHaveBeenCalled()
    })

    it('API 失败时不更新状态', async () => {
      fetch.mockResolvedValueOnce({
        ok: false
      })

      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      wrapper.vm.comments = JSON.parse(JSON.stringify(mockComments))
      const originalLikes = { ...wrapper.vm.commentLikes }
      await wrapper.vm.handleCommentLike(101)

      expect(wrapper.vm.commentLikes).toEqual(originalLikes)
    })
  })

  describe('子评论点赞', () => {
    it('能够点赞子评论', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ success: true })
      })

      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      wrapper.vm.comments = JSON.parse(JSON.stringify(mockComments))
      await wrapper.vm.handleCommentLike(201)

      expect(fetch).toHaveBeenCalledWith('/api/comments/201/like', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: mockCurrentUser.id })
      })
    })

    it('点赞子评论后更新子评论点赞数', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ success: true })
      })

      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      wrapper.vm.comments = JSON.parse(JSON.stringify(mockComments))
      const originalSubCount = wrapper.vm.comments[1].subComments[0].likeCount
      await wrapper.vm.handleCommentLike(201)

      expect(wrapper.vm.comments[1].subComments[0].likeCount).toBe(originalSubCount + 1)
    })
  })

  describe('辅助函数', () => {
    it('getCommentLikeCount 返回正确的点赞数', () => {
      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      const comment = { likeCount: 10 }
      expect(wrapper.vm.getCommentLikeCount(comment)).toBe(10)
    })

    it('getCommentLikeCount 对没有点赞数的评论返回 0', () => {
      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      const comment = {}
      expect(wrapper.vm.getCommentLikeCount(comment)).toBe(0)
    })

    it('isCommentLiked 返回正确的点赞状态', () => {
      const wrapper = mount(PostDetailModal, {
        props: {
          post: mockPost,
          currentUser: mockCurrentUser
        }
      })

      wrapper.vm.commentLikes = { 101: true, 102: false }
      expect(wrapper.vm.isCommentLiked(101)).toBe(true)
      expect(wrapper.vm.isCommentLiked(102)).toBe(false)
      expect(wrapper.vm.isCommentLiked(999)).toBe(false)
    })
  })
})
```

## 运行测试命令

```bash
# 安装依赖
npm install vitest @vue/test-utils --save-dev

# 运行测试
npm run test
```

## 手动测试清单

### 评论点赞交互
- [ ] 未登录用户不显示点赞按钮
- [ ] 已登录用户显示点赞按钮
- [ ] 点击点赞按钮调用 API
- [ ] 点赞成功后按钮变红（liked 状态）
- [ ] 点赞成功后点赞数 +1
- [ ] 再次点击取消点赞
- [ ] 取消点赞后按钮恢复原状
- [ ] 取消点赞后点赞数 -1
- [ ] 子评论（楼中楼）也能点赞
- [ ] API 失败时显示错误提示
- [ ] 网络错误时正确处理
