import request from './request'

export function listConversations(params) {
  return request.get('/conversations', { params })
}

export function getConversation(id) {
  return request.get(`/conversations/${id}`)
}

export function createConversation(data) {
  return request.post('/conversations', data)
}

export function updateConversation(data) {
  return request.put('/conversations', data)
}

export function deleteConversation(id) {
  return request.delete(`/conversations/${id}`)
}

export function batchDeleteConversations(ids) {
  return request.delete('/conversations/batch', { data: { ids } })
}
