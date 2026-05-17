import request from '../utils/request'

// 分页查询登录日志列表
export function listLoginLogs(params) {
  return request.get('/login-logs', { params })
}

// 删除登录日志
export function deleteLoginLog(id) {
  return request.delete(`/login-logs/${id}`)
}

// 清空登录日志
export function clearLoginLogs() {
  return request.delete('/login-logs/clear')
}
