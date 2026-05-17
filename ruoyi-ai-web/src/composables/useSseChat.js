import { useChatStore } from '@/stores/chat'
import { useConversationStore } from '@/stores/conversation'
import { useModelStore } from '@/stores/model'
import { useKnowledgeStore } from '@/stores/knowledge'
import { useConfigStore } from '@/stores/config'
import { sendChatMessage, stopChat } from '@/api/chat'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export function useSseChat() {
  const chatStore = useChatStore()
  const conversationStore = useConversationStore()
  const modelStore = useModelStore()
  const knowledgeStore = useKnowledgeStore()
  const configStore = useConfigStore()
  const router = useRouter()

  async function send(content) {
    if (!content || !content.trim()) return
    if (chatStore.isStreaming) return

    // 未登录时跳转到登录页
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录后再发送消息')
      router.push({ name: 'login', query: { redirect: '/' } })
      return
    }

    // 没有活跃对话则先创建
    if (!conversationStore.activeId) {
      await conversationStore.create()
      // 创建失败则中止
      if (!conversationStore.activeId) return
    }

    // 添加用户消息
    chatStore.addMessage({ role: 'user', content: content.trim() })
    chatStore.setStreaming(true)
    chatStore.currentStreamContent = ''
    chatStore.error = null

    const abortCtrl = new AbortController()
    chatStore.setAbortController(abortCtrl)

    try {
      const response = await sendChatMessage({
        conversationId: conversationStore.activeId,
        content: content.trim(),
        modelId: modelStore.selectedId,
        knowledgeBaseIds: knowledgeStore.selectedIds,
        temperature: configStore.temperature,
        maxTokens: configStore.maxTokens,
        systemPrompt: configStore.systemPrompt
      }, abortCtrl.signal)

      if (!response.ok) {
        chatStore.setError(`请求失败 (${response.status})`)
        return
      }

      // 检查 Content-Type 是否表明 SSE，如果不是，则当作普通 JSON 响应处理
      const contentType = response.headers.get('content-type')
      if (contentType && contentType.includes('text/event-stream')) {
        // SSE 模式
        await readStream(response, abortCtrl)
      } else {
        // 非 SSE 回退：当作普通 JSON 响应处理
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
        // 用户主动停止
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
        // 保留最后一个可能不完整的行
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
          } catch (e) {
            // 忽略 JSON 解析错误（可能是部分数据）
          }
        }
      }
    } catch (e) {
      if (e.name !== 'AbortError') {
        chatStore.setError('流式传输中断')
      }
    } finally {
      // 确保 reader 已释放
      try { reader.releaseLock() } catch (e) { /* noop */ }
      // 如果循环结束但未标记完成
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

  return { send, stop }
}
