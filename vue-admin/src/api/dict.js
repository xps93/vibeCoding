import request from '../utils/request'

// 分页查询字典类型列表
export function listDictTypes(params) {
  return request.get('/dict-types', { params })
}

// 根据ID获取字典类型详情
export function getDictType(id) {
  return request.get(`/dict-types/${id}`)
}

// 新增字典类型
export function addDictType(data) {
  return request.post('/dict-types', data)
}

// 修改字典类型
export function updateDictType(data) {
  return request.put('/dict-types', data)
}

// 删除字典类型
export function deleteDictType(id) {
  return request.delete(`/dict-types/${id}`)
}

// 根据字典类型标识查询字典类型
export function getDictTypeByType(dictType) {
  return request.get(`/dict-types/dictType/${dictType}`)
}

// 根据字典类型ID查询字典数据列表
export function listDictDataByTypeId(typeId) {
  return request.get('/dict-data', { params: { dictTypeId: typeId } })
}

// 新增字典数据
export function addDictData(data) {
  return request.post('/dict-data', data)
}

// 修改字典数据
export function updateDictData(data) {
  return request.put('/dict-data', data)
}

// 删除字典数据
export function deleteDictData(id) {
  return request.delete(`/dict-data/${id}`)
}

// 根据字典类型标识查询字典数据
export function listDictDataByDictType(dictType) {
  return request.get(`/dict-data/type/${dictType}`)
}
