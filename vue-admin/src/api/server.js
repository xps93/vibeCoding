import request from '../utils/request'

// 获取服务器监控信息
export function getServerInfo() {
  return request.get('/monitor/server')
}
