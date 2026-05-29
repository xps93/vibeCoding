import request from '@/utils/request'

export function listCategories() {
  return request({ url: '/admin/assistant-categories', method: 'get' })
}

export function createCategory(data) {
  return request({ url: '/admin/assistant-categories', method: 'post', data })
}

export function updateCategory(data) {
  return request({ url: '/admin/assistant-categories', method: 'put', data })
}

export function deleteCategory(id) {
  return request({ url: '/admin/assistant-categories/' + id, method: 'delete' })
}
