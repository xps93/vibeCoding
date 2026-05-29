import request from './request'

/** 上传数据集 */
export function uploadDataset(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/datasets/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 获取数据集列表 */
export function listDatasets() {
  return request.get('/datasets')
}

/** 获取数据集详情（含行数据） */
export function getDataset(id) {
  return request.get(`/datasets/${id}`)
}

/** 查询数据集 */
export function queryDataset(id, query) {
  return request.post(`/datasets/${id}/query`, { query })
}

/** 删除数据集 */
export function deleteDataset(id) {
  return request.delete(`/datasets/${id}`)
}
