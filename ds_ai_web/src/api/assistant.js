import request from './request'

export function listAssistants() {
  return request.get('/assistants')
}
