import { createI18n } from 'vue-i18n'
import zhCN from '@/locales/zh-CN.json'
import en from '@/locales/en.json'
import ja from '@/locales/ja.json'

const DEFAULT_LOCALE = localStorage.getItem('app-locale') || 'zh-CN'

const i18n = createI18n({
  legacy: false,
  locale: DEFAULT_LOCALE,
  fallbackLocale: 'zh-CN',
  messages: { 'zh-CN': zhCN, en, ja }
})

export default i18n
