import request from './request'

export function sendChatMessage(data, signal) {
  const token = localStorage.getItem('token')
  return fetch('/api/ai/chat/send', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: JSON.stringify({
      conversationId: data.conversationId,
      content: data.content,
      modelId: data.modelId,
      knowledgeBaseIds: data.knowledgeBaseIds || [],
      temperature: data.temperature,
      maxTokens: data.maxTokens,
      systemPrompt: data.systemPrompt || ''
    }),
    signal: signal || undefined
  })
}

export function stopChat(data) {
  return request.post('/chat/stop', data)
}

export function getChatStatus(conversationId) {
  return request.get('/chat/status', { params: { conversationId } })
}
