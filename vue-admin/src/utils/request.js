import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(
  // 请求拦截器：自动携带Token
  config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  // 响应拦截器：统一处理响应结果和401未授权
  response => {
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('token')
      router.push('/login')
      return Promise.reject(new Error(res.msg))
    }
    if (res.code !== 200) {
      Message.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res
  },
  // 响应拦截器：统一处理HTTP错误状态码
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      } else if (error.response.status === 403) {
        Message.error('权限不足')
      } else {
        Message.error(error.message || '请求失败')
      }
    } else {
      Message.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
