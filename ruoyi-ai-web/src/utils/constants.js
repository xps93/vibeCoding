export const DEFAULT_MODEL_ID = 'deepseek-v4-pro'

export const DEFAULT_TEMPERATURE = 0.7
export const TEMPERATURE_MIN = 0
export const TEMPERATURE_MAX = 2
export const TEMPERATURE_STEP = 0.1

export const DEFAULT_MAX_TOKENS = 2048
export const MAX_TOKENS_MIN = 64
export const MAX_TOKENS_MAX = 8192
export const MAX_TOKENS_STEP = 64

export const DEFAULT_SYSTEM_PROMPT = '你是一个有帮助的AI助手，请用简洁清晰的中文回答问题。'

export const MESSAGE_ROLES = {
  USER: 'user',
  ASSISTANT: 'assistant',
  SYSTEM: 'system'
}

export const MOBILE_BREAKPOINT = 768
export const TABLET_BREAKPOINT = 1200

export const WELCOME_SUGGESTIONS = [
  '帮我写一份工作总结',
  '解释一下什么是机器学习',
  '用Python写一个快速排序',
  '帮我翻译一段英文文档'
]
