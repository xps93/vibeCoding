import request from '../utils/request'

export function listOperLogs(params) {
  return request.get('/oper-logs', { params })
}

export function deleteOperLog(id) {
  return request.delete(`/oper-logs/${id}`)
}

export function clearOperLogs() {
  return request.delete('/oper-logs/clear')
}
