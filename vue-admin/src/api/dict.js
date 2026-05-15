import request from '../utils/request'

export function listDictTypes(params) {
  return request.get('/dict-types', { params })
}

export function getDictType(id) {
  return request.get(`/dict-types/${id}`)
}

export function addDictType(data) {
  return request.post('/dict-types', data)
}

export function updateDictType(data) {
  return request.put('/dict-types', data)
}

export function deleteDictType(id) {
  return request.delete(`/dict-types/${id}`)
}

export function getDictTypeByType(dictType) {
  return request.get(`/dict-types/dictType/${dictType}`)
}

export function listDictDataByTypeId(typeId) {
  return request.get('/dict-data', { params: { dictTypeId: typeId } })
}

export function addDictData(data) {
  return request.post('/dict-data', data)
}

export function updateDictData(data) {
  return request.put('/dict-data', data)
}

export function deleteDictData(id) {
  return request.delete(`/dict-data/${id}`)
}

export function listDictDataByDictType(dictType) {
  return request.get(`/dict-data/type/${dictType}`)
}
