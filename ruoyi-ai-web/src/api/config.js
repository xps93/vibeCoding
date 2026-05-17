import request from './request'

export function getConfig() {
  return request.get('/config')
}

export function saveConfig(data) {
  return request.put('/config', data)
}
