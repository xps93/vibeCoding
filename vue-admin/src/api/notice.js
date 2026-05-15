import request from '../utils/request'

export function listNotices(params) {
  return request.get('/notices', { params })
}

export function getNotice(id) {
  return request.get(`/notices/${id}`)
}

export function addNotice(data) {
  return request.post('/notices', data)
}

export function updateNotice(data) {
  return request.put('/notices', data)
}

export function deleteNotice(id) {
  return request.delete(`/notices/${id}`)
}
