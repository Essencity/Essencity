# 语音搜索功能测试规范

## 测试文件位置
`frontend/src/components/__tests__/VoiceSearch.spec.js`
`frontend/src/composables/__tests__/useSpeech.spec.js`

## 测试框架
Vitest + @vue/test-utils

---

## useSpeech.js 组合式函数测试

```javascript
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { useSpeech } from '../../composables/useSpeech.js'

// Mock SpeechRecognition
class MockSpeechRecognition {
  constructor() {
    this.lang = 'zh-CN'
    this.continuous = false
    this.interimResults = true
    this.onstart = null
    this.onresult = null
    this.onerror = null
    this.onend = null
  }

  start() {
    if (this.onstart) this.onstart()
  }

  stop() {
    if (this.onend) this.onend()
  }

  abort() {
    if (this.onend) this.onend()
  }
}

// Mock window
const mockWindow = {
  SpeechRecognition: MockSpeechRecognition,
  webkitSpeechRecognition: MockSpeechRecognition
}

describe('useSpeech Composable', () => {

  beforeEach(() => {
    global.window = mockWindow
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('初始化', () => {
    it('应该正确初始化状态', () => {
      const { isListening, isSupported, transcript, error } = useSpeech()

      expect(isListening.value).toBe(false)
      expect(isSupported.value).toBe(true)
      expect(transcript.value).toBe('')
      expect(error.value).toBe(null)
    })

    it('浏览器不支持时应设置 isSupported 为 false', () => {
      const originalWindow = global.window
      global.window = {}

      const { isSupported } = useSpeech()
      expect(isSupported.value).toBe(false)

      global.window = originalWindow
    })
  })

  describe('startListening', () => {
    it('调用 startListening 时设置 isListening 为 true', () => {
      const { startListening, isListening } = useSpeech()

      startListening()
      expect(isListening.value).toBe(true)
    })

    it('开始监听时重置 transcript 和 error', () => {
      const { startListening, transcript, error } = useSpeech()

      transcript.value = 'previous text'
      error.value = 'some error'

      startListening()
      expect(transcript.value).toBe('')
      expect(error.value).toBe(null)
    })
  })

  describe('stopListening', () => {
    it('调用 stopListening 时设置 isListening 为 false', () => {
      const { startListening, stopListening, isListening } = useSpeech()

      startListening()
      stopListening()
      expect(isListening.value).toBe(false)
    })
  })

  describe('resetTranscript', () => {
    it('重置 transcript 和 error', () => {
      const { transcript, error, resetTranscript } = useSpeech()

      transcript.value = 'test'
      error.value = 'error'

      resetTranscript()
      expect(transcript.value).toBe('')
      expect(error.value).toBe(null)
    })
  })
})
```

---

## VoiceSearch.vue 组件测试

```javascript
import { describe, it, expect, vi, nextTick } from 'vitest'
import { mount } from '@vue/test-utils'
import VoiceSearch from '../VoiceSearch.vue'

// Mock useSpeech
vi.mock('../../composables/useSpeech.js', () => ({
  useSpeech: () => ({
    isListening: { value: false },
    isSupported: { value: true },
    transcript: { value: '' },
    error: { value: null },
    startListening: vi.fn(),
    stopListening: vi.fn(),
    resetTranscript: vi.fn()
  })
}))

describe('VoiceSearch Component', () => {

  describe('Props 和 Events', () => {
    it('默认 visible 为 false', () => {
      const wrapper = mount(VoiceSearch)
      expect(wrapper.find('.voice-search-overlay').exists()).toBe(false)
    })

    it('visible 为 true 时显示模态框', () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })
      expect(wrapper.find('.voice-search-overlay').exists()).toBe(true)
    })

    it('点击关闭按钮触发 close 事件', async () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      await wrapper.find('.close-btn').trigger('click')
      expect(wrapper.emitted('close')).toBeTruthy()
    })
  })

  describe('浏览器兼容性', () => {
    it('不支持语音识别时显示提示', () => {
      vi.clearAllMocks()
      vi.doMock('../../composables/useSpeech.js', () => ({
        useSpeech: () => ({
          isListening: { value: false },
          isSupported: { value: false },
          transcript: { value: '' },
          error: { value: '当前浏览器不支持语音识别' },
          startListening: vi.fn(),
          stopListening: vi.fn(),
          resetTranscript: vi.fn()
        })
      }))

      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      expect(wrapper.find('.not-supported').exists()).toBe(true)
      expect(wrapper.find('.not-supported p').text()).toContain('不支持')
    })
  })

  describe('搜索功能', () => {
    it('输入文字后点击搜索触发 search 事件', async () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      await wrapper.find('.search-input').setValue('测试搜索')
      await wrapper.find('.search-btn').trigger('click')

      expect(wrapper.emitted('search')).toBeTruthy()
      expect(wrapper.emitted('search')[0]).toEqual(['测试搜索'])
    })

    it('按 Enter 键触发搜索', async () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      await wrapper.find('.search-input').setValue('测试搜索')
      await wrapper.find('.search-input').trigger('keyup.enter')

      expect(wrapper.emitted('search')).toBeTruthy()
    })

    it('搜索后关闭模态框', async () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      await wrapper.find('.search-input').setValue('测试搜索')
      await wrapper.find('.search-btn').trigger('click')

      expect(wrapper.emitted('close')).toBeTruthy()
    })
  })

  describe('清除功能', () => {
    it('显示清除按钮当有输入时', async () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      await wrapper.find('.search-input').setValue('测试')
      await nextTick()

      expect(wrapper.find('.clear-btn').exists()).toBe(true)
    })

    it('点击清除按钮清空输入', async () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      await wrapper.find('.search-input').setValue('测试')
      await wrapper.find('.clear-btn').trigger('click')

      expect(wrapper.find('.search-input').element.value).toBe('')
    })
  })

  describe('语音识别状态', () => {
    it('未在听时显示"按住说话"', () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      expect(wrapper.find('.idle-text').exists()).toBe(true)
      expect(wrapper.find('.idle-text').text()).toBe('按住说话')
    })

    it('正在听时显示"正在听..."', async () => {
      vi.clearAllMocks()
      const isListening = { value: true }

      vi.doMock('../../composables/useSpeech.js', () => ({
        useSpeech: () => ({
          isListening,
          isSupported: { value: true },
          transcript: { value: '测试文字' },
          error: { value: null },
          startListening: vi.fn(),
          stopListening: vi.fn(),
          resetTranscript: vi.fn()
        })
      }))

      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      expect(wrapper.find('.listening-text').exists()).toBe(true)
      expect(wrapper.find('.listening-text').text()).toBe('正在听...')
    })
  })

  describe('点击遮罩关闭', () => {
    it('点击遮罩层关闭模态框', async () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      await wrapper.find('.voice-search-overlay').trigger('click')

      expect(wrapper.emitted('close')).toBeTruthy()
    })

    it('点击模态框内部不关闭', async () => {
      const wrapper = mount(VoiceSearch, {
        props: { visible: true }
      })

      await wrapper.find('.voice-search-modal').trigger('click')
      expect(wrapper.emitted('close')).toBeFalsy()
    })
  })
})
```

## 手动测试清单

### VoiceSearch 组件
- [ ] 点击麦克风按钮打开语音搜索弹窗
- [ ] 弹窗正确居中显示
- [ ] 关闭按钮可点击
- [ ] 点击遮罩层关闭弹窗
- [ ] 不支持时显示友好提示
- [ ] 麦克风按钮可点击
- [ ] 点击开始录音显示"正在听..."
- [ ] 录音时显示波形动画
- [ ] 说出内容后自动填充到输入框
- [ ] 点击搜索触发搜索事件
- [ ] 清除按钮功能正常

### useSpeech Hook
- [ ] 初始化正确设置 isSupported
- [ ] startListening 开始监听
- [ ] stopListening 停止监听
- [ ] transcript 正确更新
- [ ] error 正确处理各种错误情况
- [ ] 组件卸载时清理
