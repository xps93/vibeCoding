import request from '../utils/request'

// 分页查询操作日志列表
export function listOperLogs(params) {
  return request.get('/oper-logs', { params })
}

// 删除操作日志
export function deleteOperLog(id) {
  return request.delete(`/oper-logs/${id}`)
}

// 清空操作日志
export function clearOperLogs() {
  return request.delete('/oper-logs/clear')
}
