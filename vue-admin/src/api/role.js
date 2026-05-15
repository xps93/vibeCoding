import request from '../utils/request'

export function listRoles(params) {
  return request.get('/roles', { params })
}

export function getRole(id) {
  return request.get(`/roles/${id}`)
}

export function addRole(data) {
  return request.post('/roles', data)
}

export function updateRole(data) {
  return request.put('/roles', data)
}

export function deleteRole(id) {
  return request.delete(`/roles/${id}`)
}

export function getRoleMenuIds(id) {
  return request.get(`/roles/menuIds/${id}`)
}

export function getAllMenus() {
  return request.get('/roles/menus')
}
