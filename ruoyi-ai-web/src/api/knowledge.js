import request from './request'

export function listKnowledgeBases() {
  return request.get('/knowledge-bases')
}
