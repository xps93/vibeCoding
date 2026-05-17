import request from '../utils/request'

// 分页查询部门列表
export function listDepts(params) {
  return request.get('/depts', { params })
}

// 根据ID获取部门详情
export function getDept(id) {
  return request.get(`/depts/${id}`)
}

// 新增部门
export function addDept(data) {
  return request.post('/depts', data)
}

// 修改部门
export function updateDept(data) {
  return request.put('/depts', data)
}

// 删除部门
export function deleteDept(id) {
  return request.delete(`/depts/${id}`)
}

// 获取部门树形数据
export function getDeptTree() {
  return request.get('/depts/tree')
}
