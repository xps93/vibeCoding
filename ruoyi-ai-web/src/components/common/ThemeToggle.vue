<template>
  <el-tooltip :content="isDark ? '切换亮色模式' : '切换暗色模式'" placement="bottom">
    <el-button
      :icon="isDark ? Sunny : Moon"
      circle
      text
      size="small"
      @click="themeStore.toggle()"
    />
  </el-tooltip>
</template>

<script setup>
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useThemeStore } from '@/stores/theme'
import { storeToRefs } from 'pinia'

const themeStore = useThemeStore()
const { isDark } = storeToRefs(themeStore)
</script>
