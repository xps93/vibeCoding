import request from '../utils/request'

export function listOnlineUsers() {
  return request.get('/online-users')
}

export function forceLogout(token) {
  return request.delete(`/online-users/${token}`)
}
