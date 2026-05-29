import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getActiveToken } from '@/utils/accounts'

const request = axios.create({
  baseURL: '/api/ai',
  timeout: 30000
})

request.interceptors.request.use(config => {
  const token = getActiveToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      return Promise.reject(new Error(res.msg || '未登录'))
    }
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res.data
  },
  error => {
    if (error.response?.status === 403) {
      ElMessage.error('权限不足')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
