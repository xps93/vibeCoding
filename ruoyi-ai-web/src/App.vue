<template>
  <div class="app-root" :class="{ dark: isDark }">
    <router-view />
  </div>
</template>

<script setup>
import { useThemeStore } from '@/stores/theme'
import { storeToRefs } from 'pinia'
import { watch } from 'vue'

const themeStore = useThemeStore()
const { isDark } = storeToRefs(themeStore)

watch(isDark, (val) => {
  document.documentElement.classList.toggle('dark', val)
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
