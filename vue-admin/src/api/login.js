import request from '../utils/request'

export function login(data) {
  return request.post('/login', data)
}

export function logout() {
  return request.post('/logout')
}

export function getUserInfo() {
  return request.get('/user/info')
}

export function getRouters() {
  return request.get('/menus/routers')
}
