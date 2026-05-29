import request from '@/utils/request'

/** 获取知识库列表 */
export function listKnowledgeBases() {
  return request({ url: '/admin/knowledge-bases', method: 'get' })
}

/** 获取单个知识库 */
export function getKnowledgeBase(id) {
  return request({ url: '/admin/knowledge-bases/' + id, method: 'get' })
}

/** 新增知识库 */
export function createKnowledgeBase(data) {
  return request({ url: '/admin/knowledge-bases', method: 'post', data })
}

/** 更新知识库 */
export function updateKnowledgeBase(data) {
  return request({ url: '/admin/knowledge-bases', method: 'put', data })
}

/** 删除知识库 */
export function deleteKnowledgeBase(id) {
  return request({ url: '/admin/knowledge-bases/' + id, method: 'delete' })
}
