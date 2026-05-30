import request from './request'

/** 添加收藏 */
export function addFavorite(conversationId) {
  return request.post(`/favorites/${conversationId}`)
}

/** 取消收藏 */
export function removeFavorite(conversationId) {
  return request.delete(`/favorites/${conversationId}`)
}

/** 获取收藏列表（支持分组过滤和关键词搜索） */
export function listFavorites(params = {}) {
  return request.get('/favorites', { params })
}

/** 批量检查对话收藏状态 */
export function checkFavorites(conversationIds) {
  return request.post('/favorites/check', { conversationIds })
}

/** 移动收藏到指定分组 */
export function moveToGroup(conversationId, groupId) {
  return request.put(`/favorites/${conversationId}/group`, { groupId: groupId || null })
}

// ──────────────── 分组管理 ────────────────

/** 获取分组列表 */
export function listGroups() {
  return request.get('/favorites/groups')
}

/** 创建分组 */
export function createGroup(name) {
  return request.post('/favorites/groups', { name })
}

/** 更新分组 */
export function updateGroup(id, name) {
  return request.put(`/favorites/groups/${id}`, { name })
}

/** 删除分组 */
export function deleteGroup(id) {
  return request.delete(`/favorites/groups/${id}`)
}
