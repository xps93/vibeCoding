import request from '../utils/request'

export function listJobs(params) {
  return request.get('/jobs', { params })
}

export function getJob(id) {
  return request.get(`/jobs/${id}`)
}

export function addJob(data) {
  return request.post('/jobs', data)
}

export function updateJob(data) {
  return request.put('/jobs', data)
}

export function deleteJob(id) {
  return request.delete(`/jobs/${id}`)
}
