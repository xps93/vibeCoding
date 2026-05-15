import request from '../utils/request'

export function listPosts(params) {
  return request.get('/posts', { params })
}

export function getPost(id) {
  return request.get(`/posts/${id}`)
}

export function addPost(data) {
  return request.post('/posts', data)
}

export function updatePost(data) {
  return request.put('/posts', data)
}

export function deletePost(id) {
  return request.delete(`/posts/${id}`)
}
