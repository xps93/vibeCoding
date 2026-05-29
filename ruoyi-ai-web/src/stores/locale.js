import { defineStore } from 'pinia'
import { ref } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import en from 'element-plus/dist/locale/en.mjs'
import ja from 'element-plus/dist/locale/ja.mjs'

const LANGUAGES = [
  { value: 'zh-CN', label: '简体中文', elLocale: zhCn },
  { value: 'en', label: 'English', elLocale: en },
  { value: 'ja', label: '日本語', elLocale: ja },
]

const epLocaleMap = { 'zh-CN': zhCn, en, ja }

export const useLocaleStore = defineStore('locale', () => {
  const current = ref(localStorage.getItem('app-locale') || 'zh-CN')
  const epLocale = ref(epLocaleMap[current.value] || zhCn)

  function setLocale(locale) {
    const valid = LANGUAGES.find(l => l.value === locale)
    if (!valid) return
    current.value = locale
    localStorage.setItem('app-locale', locale)
    epLocale.value = epLocaleMap[locale] || zhCn
  }

  return { current, epLocale, languages: LANGUAGES, setLocale }
})
