import request from '@/utils/request'
import axios from 'axios'

// 分页查询数据库表列表
export function listTables(params) {
  return request.get('/gen/tables', { params })
}

// 根据表名获取表字段信息
export function getTableColumns(tableName) {
  return request.get(`/gen/tables/${tableName}`)
}

// 生成代码并下载为ZIP文件
export function generateCode(data) {
  return axios({
    method: 'post',
    url: '/api/gen/generate',
    data,
    responseType: 'blob',
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
  }).then(res => {
    const blob = new Blob([res.data], { type: 'application/zip' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = data.tableName + '_code.zip'
    document.body.appendChild(link)
    link.click()
    URL.revokeObjectURL(url)
    document.body.removeChild(link)
    return { code: 200, msg: '下载成功' }
  })
}
