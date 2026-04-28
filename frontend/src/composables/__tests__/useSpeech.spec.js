import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { ref } from 'vue'
import { useSpeech } from '../useSpeech.js'

describe('useSpeech composable', () => {
  let mockRecognition
  let mockSpeechRecognition

  beforeEach(() => {
    mockRecognition = {
      lang: 'zh-CN',
      continuous: false,
      interimResults: true,
      start: vi.fn(),
      stop: vi.fn(),
      abort: vi.fn(),
      onstart: null,
      onresult: null,
      onerror: null,
      onend: null
    }

    mockSpeechRecognition = vi.fn(() => mockRecognition)

    window.SpeechRecognition = mockSpeechRecognition
    window.webkitSpeechRecognition = mockSpeechRecognition
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('checkSupport', () => {
    it('应返回 true 当浏览器支持语音识别', () => {
      const { checkSupport, isSupported } = useSpeech()
      const result = checkSupport()
      expect(result).toBe(true)
      expect(isSupported.value).toBe(true)
    })

    it('应返回 false 当浏览器不支持语音识别', () => {
      window.SpeechRecognition = undefined
      window.webkitSpeechRecognition = undefined

      const { checkSupport, isSupported, error } = useSpeech()
      const result = checkSupport()
      expect(result).toBe(false)
      expect(isSupported.value).toBe(false)
      expect(error.value).toBe('当前浏览器不支持语音识别')
    })
  })

  describe('startListening', () => {
    it('初始化后应能启动语音识别', () => {
      const { startListening, checkSupport } = useSpeech()
      checkSupport()
      startListening()
      expect(mockRecognition.start).toHaveBeenCalled()
    })

    it('启动时应清空 transcript', () => {
      const { startListening, transcript, checkSupport } = useSpeech()
      transcript.value = '旧内容'
      checkSupport()
      startListening()
      expect(transcript.value).toBe('')
    })

    it('启动时应清空 error', () => {
      const { startListening, error, checkSupport } = useSpeech()
      error.value = '旧错误'
      checkSupport()
      startListening()
      expect(error.value).toBe(null)
    })
  })

  describe('stopListening', () => {
    it('应在 isListening 时调用 recognition.stop', () => {
      const { startListening, stopListening, checkSupport, isListening } = useSpeech()
      checkSupport()
      startListening()
      mockRecognition.onstart()
      expect(isListening.value).toBe(true)
      stopListening()
      expect(mockRecognition.stop).toHaveBeenCalled()
    })
  })

  describe('resetTranscript', () => {
    it('应清空 transcript 和 error', () => {
      const { resetTranscript, transcript, error } = useSpeech()
      transcript.value = '测试文字'
      error.value = '测试错误'
      resetTranscript()
      expect(transcript.value).toBe('')
      expect(error.value).toBe(null)
    })
  })

  describe('状态初始化', () => {
    it('isListening 初始值应为 false', () => {
      const { isListening } = useSpeech()
      expect(isListening.value).toBe(false)
    })

    it('transcript 初始值应为空字符串', () => {
      const { transcript } = useSpeech()
      expect(transcript.value).toBe('')
    })

    it('error 初始值应为 null', () => {
      const { error } = useSpeech()
      expect(error.value).toBe(null)
    })
  })
})