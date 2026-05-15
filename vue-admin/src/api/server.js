import request from '../utils/request'

export function getServerInfo() {
  return request.get('/monitor/server')
}
