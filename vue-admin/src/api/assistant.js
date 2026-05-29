import request from '@/utils/request'

export function listAssistants() {
  return request({ url: '/admin/assistants', method: 'get' })
}

export function createAssistant(data) {
  return request({ url: '/admin/assistants', method: 'post', data })
}

export function updateAssistant(data) {
  return request({ url: '/admin/assistants', method: 'put', data })
}

export function deleteAssistant(id) {
  return request({ url: '/admin/assistants/' + id, method: 'delete' })
}
