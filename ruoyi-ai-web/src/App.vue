<template>
  <el-config-provider :locale="epLocale">
    <div class="app-root">
      <router-view />
    </div>
  </el-config-provider>
</template>

<script setup>
import { watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElConfigProvider } from 'element-plus'
import { useThemeStore } from '@/stores/theme'
import { useLocaleStore } from '@/stores/locale'
import { storeToRefs } from 'pinia'

const themeStore = useThemeStore()
const localeStore = useLocaleStore()
const { locale } = useI18n()
const { epLocale } = storeToRefs(localeStore)

watch(() => themeStore.current, (val) => {
  document.documentElement.setAttribute('data-theme', val)
}, { immediate: true })

watch(() => localeStore.current, (val) => {
  locale.value = val
}, { immediate: true })
</script>

<style lang="scss">
.app-root {
  height: 100%;
  background: var(--bg-primary);
  color: var(--text-primary);
  transition: background 0.3s ease, color 0.3s ease;
}
</style>
