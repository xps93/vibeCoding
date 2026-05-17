import request from '../utils/request'

// 查询菜单列表（树形）
export function listMenus() {
  return request.get('/menus')
}

// 查询所有菜单
export function getAllMenus() {
  return request.get('/menus/all')
}

// 根据ID获取菜单详情
export function getMenu(id) {
  return request.get(`/menus/${id}`)
}

// 新增菜单
export function addMenu(data) {
  return request.post('/menus', data)
}

// 修改菜单
export function updateMenu(data) {
  return request.put('/menus', data)
}

// 删除菜单
export function deleteMenu(id) {
  return request.delete(`/menus/${id}`)
}
