import { describe, it, expect, vi, beforeEach } from 'vitest'
import { getAiSummary, generateAiSummary } from '../../api/ai'

describe('ai.js API', () => {
  beforeEach(() => {
    global.fetch = vi.fn()
  })

  describe('getAiSummary', () => {
    it('成功获取AI总结', async () => {
      const mockResponse = { summary: '这是一篇关于旅行的文章' }
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse
      })

      const result = await getAiSummary(1)
      expect(result).toEqual(mockResponse)
      expect(global.fetch).toHaveBeenCalledWith('/api/ai/summary/1')
    })

    it('获取失败时抛出错误', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: false,
        status: 404
      })

      await expect(getAiSummary(999)).rejects.toThrow('获取AI总结失败')
    })

    it('使用正确的URL格式', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({})
      })

      await getAiSummary(123)
      expect(global.fetch).toHaveBeenCalledWith('/api/ai/summary/123')
    })
  })

  describe('generateAiSummary', () => {
    it('成功生成AI总结', async () => {
      const mockResponse = { summary: 'AI生成的总结内容' }
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse
      })

      const result = await generateAiSummary(1, '测试标题', '测试内容')
      expect(result).toEqual(mockResponse)
      expect(global.fetch).toHaveBeenCalledWith('/api/ai/summary', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ postId: 1, title: '测试标题', content: '测试内容' })
      })
    })

    it('生成失败时抛出错误', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: false,
        status: 500
      })

      await expect(generateAiSummary(1, '标题', '内容')).rejects.toThrow('生成AI总结失败')
    })

    it('使用POST方法', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({})
      })

      await generateAiSummary(1, '标题', '内容')
      expect(global.fetch).toHaveBeenCalledWith(
        '/api/ai/summary',
        expect.objectContaining({ method: 'POST' })
      )
    })

    it('设置正确的Content-Type', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({})
      })

      await generateAiSummary(1, '标题', '内容')
      expect(global.fetch).toHaveBeenCalledWith(
        '/api/ai/summary',
        expect.objectContaining({
          headers: { 'Content-Type': 'application/json' }
        })
      )
    })

    it('正确传递请求体参数', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({})
      })

      await generateAiSummary(42, '我的帖子', '这是帖子的详细内容')
      expect(global.fetch).toHaveBeenCalledWith(
        '/api/ai/summary',
        expect.objectContaining({
          body: JSON.stringify({ postId: 42, title: '我的帖子', content: '这是帖子的详细内容' })
        })
      )
    })
  })
})