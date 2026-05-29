import request from '@/utils/request'

export function listSiteConfigs() {
  return request({ url: '/admin/site-configs', method: 'get' })
}

export function updateSiteConfig(data) {
  return request({ url: '/admin/site-configs', method: 'put', data })
}
