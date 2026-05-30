import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 8种主题定义：value 为 CSS 选择器标识，label 为中文名
const THEMES = [
  { value: 'light', label: '亮色模式', icon: 'Sunny' },
  { value: 'dark', label: '暗色模式', icon: 'Moon' },
  { value: 'eye-care', label: '护眼模式', icon: 'Sunrise' },
  { value: 'warm', label: '暖色模式', icon: 'Sunset' },
  { value: 'high-contrast', label: '高对比度', icon: 'View' },
  { value: 'blue-pro', label: '蓝色专业', icon: 'Monitor' },
  { value: 'purple', label: '紫色梦幻', icon: 'MagicStick' },
  { value: 'minimal', label: '简约灰白', icon: 'Brush' },
]

const DEFAULT_THEME = 'light'

export const useThemeStore = defineStore('theme', () => {
  const current = ref(localStorage.getItem('app-theme') || DEFAULT_THEME)

  const isDark = computed(() => ['dark', 'blue-pro', 'purple'].includes(current.value))

  function setTheme(name) {
    const valid = THEMES.find(t => t.value === name)
    if (valid) {
      current.value = name
      localStorage.setItem('app-theme', name)
    }
  }

  function toggle() {
    const idx = THEMES.findIndex(t => t.value === current.value)
    const next = THEMES[(idx + 1) % THEMES.length]
    setTheme(next.value)
  }

  return { current, isDark, themes: THEMES, setTheme, toggle }
})
