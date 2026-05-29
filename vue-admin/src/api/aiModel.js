import request from '@/utils/request'

// 获取所有模型（含禁用）
export function listModels() {
  return request({ url: '/admin/models', method: 'get' })
}

// 获取单个模型
export function getModel(id) {
  return request({ url: '/admin/models/' + id, method: 'get' })
}

// 新增模型
export function createModel(data) {
  return request({ url: '/admin/models', method: 'post', data })
}

// 更新模型
export function updateModel(data) {
  return request({ url: '/admin/models', method: 'put', data })
}

// 删除模型
export function deleteModel(id) {
  return request({ url: '/admin/models/' + id, method: 'delete' })
}

// 切换模型状态
export function toggleModelStatus(id, status) {
  return request({ url: '/admin/models/' + id + '/status', method: 'put', params: { status } })
}
