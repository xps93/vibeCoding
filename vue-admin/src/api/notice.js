import request from '../utils/request'

// 分页查询通知公告列表
export function listNotices(params) {
  return request.get('/notices', { params })
}

// 根据ID获取通知公告详情
export function getNotice(id) {
  return request.get(`/notices/${id}`)
}

// 新增通知公告
export function addNotice(data) {
  return request.post('/notices', data)
}

// 修改通知公告
export function updateNotice(data) {
  return request.put('/notices', data)
}

// 删除通知公告
export function deleteNotice(id) {
  return request.delete(`/notices/${id}`)
}
