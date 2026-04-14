import { ref, onUnmounted } from 'vue'

export function useSpeech() {
  const isListening = ref(false)
  const isSupported = ref(false)
  const transcript = ref('')
  const error = ref(null)

  let recognition = null

  const checkSupport = () => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
    if (!SpeechRecognition) {
      isSupported.value = false
      error.value = '当前浏览器不支持语音识别'
      return false
    }
    isSupported.value = true
    return true
  }

  const initRecognition = () => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition

    if (!SpeechRecognition) {
      isSupported.value = false
      error.value = '当前浏览器不支持语音识别'
      return false
    }

    isSupported.value = true
    recognition = new SpeechRecognition()
    recognition.lang = 'zh-CN'
    recognition.continuous = false
    recognition.interimResults = true

    recognition.onstart = () => {
      isListening.value = true
      error.value = null
    }

    recognition.onresult = (event) => {
      let finalTranscript = ''
      let interimTranscript = ''

      for (let i = event.resultIndex; i < event.results.length; i++) {
        const transcript_item = event.results[i][0].transcript
        if (event.results[i].isFinal) {
          finalTranscript += transcript_item
        } else {
          interimTranscript += transcript_item
        }
      }

      if (finalTranscript) {
        transcript.value = finalTranscript
      } else {
        transcript.value = interimTranscript
      }
    }

    recognition.onerror = (event) => {
      isListening.value = false
      switch (event.error) {
        case 'no-speech':
          error.value = '没有检测到语音，请重试'
          break
        case 'audio-capture':
          error.value = '无法访问麦克风'
          break
        case 'not-allowed':
          error.value = '麦克风权限被拒绝'
          break
        case 'network':
          error.value = '网络错误，请检查网络连接'
          break
        default:
          error.value = '语音识别出错'
      }
    }

    recognition.onend = () => {
      isListening.value = false
    }

    return true
  }

  const startListening = () => {
    if (!recognition) {
      if (!initRecognition()) {
        return
      }
    }

    transcript.value = ''
    error.value = null

    try {
      recognition.start()
    } catch (err) {
      error.value = '启动语音识别失败'
    }
  }

  const stopListening = () => {
    if (recognition && isListening.value) {
      recognition.stop()
    }
  }

  const resetTranscript = () => {
    transcript.value = ''
    error.value = null
  }

  onUnmounted(() => {
    if (recognition) {
      recognition.abort()
    }
  })

  return {
    isListening,
    isSupported,
    transcript,
    error,
    checkSupport,
    startListening,
    stopListening,
    resetTranscript
  }
}