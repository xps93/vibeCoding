import request from '../utils/request'

export function getCacheInfo() {
  return request.get('/monitor/cache')
}
