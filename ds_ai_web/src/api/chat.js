import request from './request'
import { getActiveToken } from '@/utils/accounts'

export function sendChatMessage(data, signal) {
  const token = getActiveToken()
  return fetch('/api/ai/chat/send', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: JSON.stringify({
      conversationId: data.conversationId,
      content: data.content,
      documentId: data.documentId || null,
      modelId: data.modelId,
      knowledgeBaseIds: data.knowledgeBaseIds || [],
      temperature: data.temperature,
      maxTokens: data.maxTokens,
      systemPrompt: data.systemPrompt || '',
      webSearch: data.webSearch || false
    }),
    signal: signal || undefined
  })
}

export function regenerateChat(data, signal) {
  const token = getActiveToken()
  return fetch('/api/ai/chat/regenerate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: JSON.stringify({
      conversationId: data.conversationId,
      content: data.content || '',
      modelId: data.modelId,
      temperature: data.temperature,
      maxTokens: data.maxTokens,
      systemPrompt: data.systemPrompt || '',
      webSearch: data.webSearch || false
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

// 消息编辑
export function updateMessage(id, content) {
  return request.put(`/messages/${id}`, { content })
}

// 消息评分
export function rateMessage(id, rating) {
  return request.post(`/messages/${id}/rate`, { rating })
}

// 取消评分
export function cancelRate(id) {
  return request.delete(`/messages/${id}/rate`)
}

// 获取消息列表（含评分）
export function getMessages(conversationId) {
  return request.get(`/conversations/${conversationId}/messages`)
}

// 对话分享
export function createShare(conversationId) {
  return request.post(`/conversations/${conversationId}/share`)
}

// 撤销分享
export function revokeShare(conversationId) {
  return request.delete(`/conversations/${conversationId}/share`)
}

// 查看分享内容（无需登录）
export function viewShare(shareToken) {
  return request.get(`/share/${shareToken}`)
}
