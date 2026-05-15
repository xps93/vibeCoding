import request from '../utils/request'

export function listMenus() {
  return request.get('/menus')
}

export function getAllMenus() {
  return request.get('/menus/all')
}

export function getMenu(id) {
  return request.get(`/menus/${id}`)
}

export function addMenu(data) {
  return request.post('/menus', data)
}

export function updateMenu(data) {
  return request.put('/menus', data)
}

export function deleteMenu(id) {
  return request.delete(`/menus/${id}`)
}
