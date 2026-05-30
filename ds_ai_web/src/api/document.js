import request from './request'

/** 上传文档，返回解析后的文本与元数据 */
export function uploadDocument(file, conversationId) {
  const formData = new FormData()
  formData.append('file', file)
  if (conversationId) {
    formData.append('conversationId', conversationId)
  }
  return request.post('/documents/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  })
}

/** 获取对话关联的文档列表 */
export function listDocumentsByConversation(conversationId) {
  return request.get(`/conversations/${conversationId}/documents`)
}

/** 获取用户上传的文档列表 */
export function listDocumentsByUser() {
  return request.get('/documents')
}

/** 获取文档详情（含解析文本） */
export function getDocument(id) {
  return request.get(`/documents/${id}`)
}

/** 删除文档 */
export function deleteDocument(id) {
  return request.delete(`/documents/${id}`)
}
