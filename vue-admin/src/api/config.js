import request from '../utils/request'

export function listConfigs(params) {
  return request.get('/configs', { params })
}

export function getConfig(id) {
  return request.get(`/configs/${id}`)
}

export function addConfig(data) {
  return request.post('/configs', data)
}

export function updateConfig(data) {
  return request.put('/configs', data)
}

export function deleteConfig(id) {
  return request.delete(`/configs/${id}`)
}
