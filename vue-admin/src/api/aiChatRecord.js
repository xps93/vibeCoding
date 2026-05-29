import request from '@/utils/request'

/** 分页查询聊天记录列表 */
export function listChatRecords(params) {
  return request({ url: '/admin/chat-records', method: 'get', params })
}

/** 获取对话消息详情 */
export function getChatMessages(id) {
  return request({ url: '/admin/chat-records/' + id + '/messages', method: 'get' })
}

/** 删除对话记录 */
export function deleteChatRecord(id) {
  return request({ url: '/admin/chat-records/' + id, method: 'delete' })
}
