import request from '../utils/request'

// 分页查询角色列表
export function listRoles(params) {
  return request.get('/roles', { params })
}

// 根据ID获取角色详情
export function getRole(id) {
  return request.get(`/roles/${id}`)
}

// 新增角色
export function addRole(data) {
  return request.post('/roles', data)
}

// 修改角色
export function updateRole(data) {
  return request.put('/roles', data)
}

// 删除角色
export function deleteRole(id) {
  return request.delete(`/roles/${id}`)
}

// 获取角色关联的菜单ID列表
export function getRoleMenuIds(id) {
  return request.get(`/roles/menuIds/${id}`)
}

// 获取所有菜单（用于角色授权）
export function getAllMenus() {
  return request.get('/roles/menus')
}
