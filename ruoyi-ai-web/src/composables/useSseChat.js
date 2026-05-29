import { useChatStore } from '@/stores/chat'
import { useConversationStore } from '@/stores/conversation'
import { useModelStore } from '@/stores/model'
import { useKnowledgeStore } from '@/stores/knowledge'
import { useConfigStore } from '@/stores/config'
import { sendChatMessage, regenerateChat, stopChat } from '@/api/chat'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getActiveToken } from '@/utils/accounts'

export function useSseChat() {
  const chatStore = useChatStore()
  const conversationStore = useConversationStore()
  const modelStore = useModelStore()
  const knowledgeStore = useKnowledgeStore()
  const configStore = useConfigStore()
  const router = useRouter()

  async function send(content, documentId, attachmentName) {
    if ((!content || !content.trim()) && !documentId) return
    if (chatStore.isStreaming) return

    const token = getActiveToken()
    if (!token) {
      ElMessage.warning('请先登录后再发送消息')
      router.push({ name: 'login', query: { redirect: '/' } })
      return
    }

    if (!conversationStore.activeId) {
      await conversationStore.create()
      if (!conversationStore.activeId) return
    }

    const displayContent = attachmentName
      ? `[文档: ${attachmentName}]\n\n${content.trim()}`
      : content.trim()
    chatStore.addMessage({ role: 'user', content: displayContent })
    chatStore.setStreaming(true)
    chatStore.currentStreamContent = ''
    chatStore.currentReasoningContent = ''
    chatStore.error = null

    const abortCtrl = new AbortController()
    chatStore.setAbortController(abortCtrl)

    try {
      const response = await sendChatMessage({
        conversationId: conversationStore.activeId,
        content: content ? content.trim() : '',
        documentId: documentId || null,
        modelId: modelStore.selectedId,
        knowledgeBaseIds: knowledgeStore.selectedIds,
        temperature: configStore.temperature,
        maxTokens: configStore.maxTokens,
        systemPrompt: configStore.systemPrompt,
        webSearch: chatStore.webSearchEnabled,
        ragEnabled: chatStore.ragEnabled
      }, abortCtrl.signal)

      if (!response.ok) {
        chatStore.setError(`请求失败 (${response.status})`)
        return
      }

      const contentType = response.headers.get('content-type')
      if (contentType && contentType.includes('text/event-stream')) {
        await readStream(response, abortCtrl)
      } else {
        const data = await response.json()
        if (data.code === 200 && data.data) {
          chatStore.appendToCurrentStream(data.data.content || data.data)
          chatStore.finalizeStream()
        } else {
          chatStore.setError(data.msg || '响应格式错误')
        }
      }
    } catch (e) {
      if (e.name === 'AbortError') {
        chatStore.finalizeStream()
      } else {
        chatStore.setError(e.message || '网络连接失败')
      }
    }
  }

  // 重新生成：删除最后一条AI回复后重新发起请求
  async function reGen(previousContent) {
    if (chatStore.isStreaming) return

    const token = getActiveToken()
    if (!token) {
      ElMessage.warning('请先登录后再发送消息')
      router.push({ name: 'login', query: { redirect: '/' } })
      return
    }

    if (!conversationStore.activeId) return

    // 移除最后一条AI消息
    chatStore.removeLastAssistantMessage()

    chatStore.setStreaming(true)
    chatStore.currentStreamContent = ''
    chatStore.currentReasoningContent = ''
    chatStore.error = null

    const abortCtrl = new AbortController()
    chatStore.setAbortController(abortCtrl)

    try {
      const response = await regenerateChat({
        conversationId: conversationStore.activeId,
        content: previousContent || '',
        modelId: modelStore.selectedId,
        temperature: configStore.temperature,
        maxTokens: configStore.maxTokens,
        systemPrompt: configStore.systemPrompt,
        webSearch: chatStore.webSearchEnabled,
        ragEnabled: chatStore.ragEnabled
      }, abortCtrl.signal)

      if (!response.ok) {
        chatStore.setError(`请求失败 (${response.status})`)
        return
      }

      const contentType = response.headers.get('content-type')
      if (contentType && contentType.includes('text/event-stream')) {
        await readStream(response, abortCtrl)
      } else {
        const data = await response.json()
        if (data.code === 200 && data.data) {
          chatStore.appendToCurrentStream(data.data.content || data.data)
          chatStore.finalizeStream()
        } else {
          chatStore.setError(data.msg || '响应格式错误')
        }
      }
    } catch (e) {
      if (e.name === 'AbortError') {
        chatStore.finalizeStream()
      } else {
        chatStore.setError(e.message || '网络连接失败')
      }
    }
  }

  async function readStream(response, abortCtrl) {
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    try {
      while (true) {
        if (abortCtrl.signal.aborted) break

        const { done, value } = await reader.read()
        if (done) break

        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          const trimmed = line.trim()
          if (!trimmed || !trimmed.startsWith('data:')) continue

          const jsonStr = trimmed.slice(5).trim()
          if (jsonStr === '[DONE]') {
            chatStore.finalizeStream()
            return
          }

          try {
            const parsed = JSON.parse(jsonStr)
            if (parsed.done) {
              chatStore.finalizeStream()
              return
            }
            if (parsed.error) {
              chatStore.setError(parsed.error)
              return
            }
            if (parsed.content) {
              chatStore.appendToCurrentStream(parsed.content)
            }
            if (parsed.reasoning_content) {
              chatStore.appendToReasoningStream(parsed.reasoning_content)
            }
          } catch (e) {
            // 忽略 JSON 解析错误
          }
        }
      }
    } catch (e) {
      if (e.name !== 'AbortError') {
        chatStore.setError('流式传输中断')
      }
    } finally {
      try { reader.releaseLock() } catch (e) { /* noop */ }
      if (chatStore.isStreaming && !abortCtrl.signal.aborted) {
        chatStore.finalizeStream()
      }
    }
  }

  async function stop() {
    if (chatStore.abortController) {
      chatStore.abortController.abort()
    }
    try {
      await stopChat({ conversationId: conversationStore.activeId })
    } catch (e) { /* 忽略 */ }
    chatStore.stopGeneration()
  }

  return { send, reGen, stop }
}
