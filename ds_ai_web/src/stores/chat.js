import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getMessages } from '@/api/chat'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const isStreaming = ref(false)
  const currentStreamContent = ref('')
  const currentReasoningContent = ref('')
  const error = ref(null)
  const abortController = ref(null)
  const webSearchEnabled = ref(false)
  const messageRatings = ref({}) // { messageId: 1|0 }

  const allMessages = computed(() => {
    if (currentStreamContent.value) {
      const msg = {
        role: 'assistant',
        content: currentStreamContent.value,
        streaming: true,
        timestamp: Date.now()
      }
      if (currentReasoningContent.value) {
        msg.reasoningContent = currentReasoningContent.value
      }
      return [...messages.value, msg]
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
    currentReasoningContent.value = ''
    error.value = null
    messageRatings.value = {}
  }

  function appendToCurrentStream(chunk) {
    currentStreamContent.value += chunk
  }

  function appendToReasoningStream(chunk) {
    currentReasoningContent.value += chunk
  }

  function finalizeStream() {
    if (currentStreamContent.value) {
      const msg = {
        role: 'assistant',
        content: currentStreamContent.value,
        streaming: false,
        timestamp: Date.now()
      }
      if (currentReasoningContent.value) {
        msg.reasoningContent = currentReasoningContent.value
      }
      messages.value.push(msg)
      currentStreamContent.value = ''
      currentReasoningContent.value = ''
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
      const msg = {
        role: 'assistant',
        content: currentStreamContent.value,
        streaming: false,
        timestamp: Date.now()
      }
      if (currentReasoningContent.value) {
        msg.reasoningContent = currentReasoningContent.value
      }
      messages.value.push(msg)
      currentStreamContent.value = ''
      currentReasoningContent.value = ''
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
    currentReasoningContent.value = ''
  }

  // 编辑用户消息
  function editMessage(index, newContent) {
    if (index >= 0 && index < messages.value.length) {
      messages.value[index] = { ...messages.value[index], content: newContent }
    }
  }

  // 删除最后一条AI消息（用于重新生成）
  function removeLastAssistantMessage() {
    const lastIdx = messages.value.length - 1
    if (lastIdx >= 0 && messages.value[lastIdx].role === 'assistant') {
      messages.value.splice(lastIdx, 1)
    }
  }

  // 评分
  function setMessageRating(messageId, rating) {
    messageRatings.value = { ...messageRatings.value, [messageId]: rating }
  }

  function clearMessageRating(messageId) {
    const newRatings = { ...messageRatings.value }
    delete newRatings[messageId]
    messageRatings.value = newRatings
  }

  // 加载评分数据
  async function loadRatings(conversationId) {
    try {
      const res = await getMessages(conversationId)
      // request.js 拦截器已解包 res.data，此处直接拿到 { messages, ratings }
      if (res && res.ratings) {
        messageRatings.value = res.ratings
      }
    } catch (e) { /* ignore */ }
  }

  const ragEnabled = ref(false)

  function toggleWebSearch() {
    webSearchEnabled.value = !webSearchEnabled.value
  }

  function toggleRag() {
    ragEnabled.value = !ragEnabled.value
  }

  return {
    messages,
    isStreaming,
    currentStreamContent,
    currentReasoningContent,
    error,
    abortController,
    webSearchEnabled,
    ragEnabled,
    messageRatings,
    allMessages,
    hasMessages,
    setMessages,
    addMessage,
    clearMessages,
    appendToCurrentStream,
    appendToReasoningStream,
    finalizeStream,
    setStreaming,
    setError,
    setAbortController,
    stopGeneration,
    editMessage,
    removeLastAssistantMessage,
    setMessageRating,
    clearMessageRating,
    loadRatings,
    toggleWebSearch,
    toggleRag
  }
})
