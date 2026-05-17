import request from '../utils/request'

// 分页查询参数配置列表
export function listConfigs(params) {
  return request.get('/configs', { params })
}

// 根据ID获取参数配置详情
export function getConfig(id) {
  return request.get(`/configs/${id}`)
}

// 新增参数配置
export function addConfig(data) {
  return request.post('/configs', data)
}

// 修改参数配置
export function updateConfig(data) {
  return request.put('/configs', data)
}

// 删除参数配置
export function deleteConfig(id) {
  return request.delete(`/configs/${id}`)
}
