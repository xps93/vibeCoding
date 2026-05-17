import request from '../utils/request'

// 分页查询用户列表
export function listUsers(params) {
  return request.get('/users', { params })
}

// 根据ID获取用户详情
export function getUser(id) {
  return request.get(`/users/${id}`)
}

// 新增用户
export function addUser(data) {
  return request.post('/users', data)
}

// 修改用户
export function updateUser(data) {
  return request.put('/users', data)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}
