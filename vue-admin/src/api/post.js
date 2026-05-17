import request from '../utils/request'

// 分页查询岗位列表
export function listPosts(params) {
  return request.get('/posts', { params })
}

// 根据ID获取岗位详情
export function getPost(id) {
  return request.get(`/posts/${id}`)
}

// 新增岗位
export function addPost(data) {
  return request.post('/posts', data)
}

// 修改岗位
export function updatePost(data) {
  return request.put('/posts', data)
}

// 删除岗位
export function deletePost(id) {
  return request.delete(`/posts/${id}`)
}
