import axios from 'axios'

const siteRequest = axios.create({
  baseURL: '/api/site',
  timeout: 10000
})

siteRequest.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) return res.data
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  error => Promise.reject(error)
)

export function getSiteConfig() {
  return siteRequest.get('/config')
}
