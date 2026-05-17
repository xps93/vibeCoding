import request from '../utils/request'

// 查询在线用户列表
export function listOnlineUsers() {
  return request.get('/online-users')
}

// 强制用户下线
export function forceLogout(token) {
  return request.delete(`/online-users/${token}`)
}
