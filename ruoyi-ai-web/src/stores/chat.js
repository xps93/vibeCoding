import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const isStreaming = ref(false)
  const currentStreamContent = ref('')
  const error = ref(null)
  const abortController = ref(null)

  const allMessages = computed(() => {
    if (currentStreamContent.value) {
      return [
        ...messages.value,
        {
          role: 'assistant',
          content: currentStreamContent.value,
          streaming: true,
          timestamp: Date.now()
        }
      ]
    }
    return messages.value
  })

  const hasMessages = computed(() => messages.value.length > 0)

  function setMessages(list) {
    messages.value = list || []
  }

  function addMessage(msg) {
    messages.value.push({ ...msg, timestamp: msg.timestamp || Date.now() })
  }

  function clearMessages() {
    messages.value = []
    currentStreamContent.value = ''
    error.value = null
  }

  function appendToCurrentStream(chunk) {
    currentStreamContent.value += chunk
  }

  function finalizeStream() {
    if (currentStreamContent.value) {
      messages.value.push({
        role: 'assistant',
        content: currentStreamContent.value,
        streaming: false,
        timestamp: Date.now()
      })
      currentStreamContent.value = ''
    }
    isStreaming.value = false
  }

  function setStreaming(val) {
    isStreaming.value = val
    if (!val) {
      error.value = null
    }
  }

  function setError(err) {
    error.value = err
    isStreaming.value = false
    if (currentStreamContent.value) {
      messages.value.push({
        role: 'assistant',
        content: currentStreamContent.value,
        streaming: false,
        timestamp: Date.now()
      })
      currentStreamContent.value = ''
    }
  }

  function setAbortController(ctrl) {
    abortController.value = ctrl
  }

  function stopGeneration() {
    if (abortController.value) {
      abortController.value.abort()
      abortController.value = null
    }
    if (currentStreamContent.value) {
      finalizeStream()
    }
    isStreaming.value = false
  }

  return {
    messages,
    isStreaming,
    currentStreamContent,
    error,
    abortController,
    allMessages,
    hasMessages,
    setMessages,
    addMessage,
    clearMessages,
    appendToCurrentStream,
    finalizeStream,
    setStreaming,
    setError,
    setAbortController,
    stopGeneration
  }
})
