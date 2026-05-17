import request from '../utils/request'

// 用户登录
export function login(data) {
  return request.post('/login', data)
}

// 用户退出登录
export function logout() {
  return request.post('/logout')
}

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/user/info')
}

// 获取动态路由菜单
export function getRouters() {
  return request.get('/menus/routers')
}
