import request from '../utils/request'

export function listDepts(params) {
  return request.get('/depts', { params })
}

export function getDept(id) {
  return request.get(`/depts/${id}`)
}

export function addDept(data) {
  return request.post('/depts', data)
}

export function updateDept(data) {
  return request.put('/depts', data)
}

export function deleteDept(id) {
  return request.delete(`/depts/${id}`)
}

export function getDeptTree() {
  return request.get('/depts/tree')
}
