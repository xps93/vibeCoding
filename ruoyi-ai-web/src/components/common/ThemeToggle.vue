<template>
  <el-dropdown trigger="click" @command="handleSelect">
    <el-button :icon="currentIcon" circle text size="small" title="切换主题" />
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="t in themes"
          :key="t.value"
          :command="t.value"
          :class="{ 'is-active': themeStore.current === t.value }"
        >
          <el-icon :size="14" style="margin-right:8px">
            <component :is="t.iconComp" />
          </el-icon>
          <span>{{ t.label }}</span>
          <el-icon v-if="themeStore.current === t.value" :size="14" style="margin-left:auto;color:var(--accent-color)">
            <Check />
          </el-icon>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import {
  Sunny, Moon, Sunrise, Sunset, View, Monitor, MagicStick, Brush, Check
} from '@element-plus/icons-vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

const iconMap = {
  Sunny, Moon, Sunrise, Sunset, View, Monitor, MagicStick, Brush
}

const themes = computed(() =>
  themeStore.themes.map(t => ({ ...t, iconComp: iconMap[t.icon] }))
)

const currentIcon = computed(() => {
  const t = themeStore.themes.find(t => t.value === themeStore.current)
  return iconMap[t?.icon || 'Sunny']
})

function handleSelect(val) {
  themeStore.setTheme(val)
}
</script>
