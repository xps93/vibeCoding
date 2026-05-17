import request from '../utils/request'

// 分页查询定时任务列表
export function listJobs(params) {
  return request.get('/jobs', { params })
}

// 根据ID获取定时任务详情
export function getJob(id) {
  return request.get(`/jobs/${id}`)
}

// 新增定时任务
export function addJob(data) {
  return request.post('/jobs', data)
}

// 修改定时任务
export function updateJob(data) {
  return request.put('/jobs', data)
}

// 删除定时任务
export function deleteJob(id) {
  return request.delete(`/jobs/${id}`)
}
