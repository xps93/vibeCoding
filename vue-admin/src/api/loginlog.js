import request from '../utils/request'

export function listLoginLogs(params) {
  return request.get('/login-logs', { params })
}

export function deleteLoginLog(id) {
  return request.delete(`/login-logs/${id}`)
}

export function clearLoginLogs() {
  return request.delete('/login-logs/clear')
}
