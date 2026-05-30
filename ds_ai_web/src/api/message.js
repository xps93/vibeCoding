import request from './request'

export function listMessages(conversationId, params) {
  return request.get(`/conversations/${conversationId}/messages`, { params })
}

export function deleteMessage(id) {
  return request.delete(`/messages/${id}`)
}
