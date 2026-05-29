<template>
  <div class="header-toolbar">
    <div class="toolbar-left">
      <el-button
        v-if="!showSidebar"
        :icon="Menu"
        text
        circle
        @click="$emit('toggle-left')"
      />
      <span class="toolbar-title">{{ $t('chat.title') }}</span>
    </div>
    <div class="toolbar-right">
      <LanguageSwitcher />
      <ThemeToggle />
      <el-button
        v-if="!showRightPanel"
        :icon="Setting"
        text
        circle
        @click="$emit('toggle-right')"
      />
    </div>
  </div>
</template>

<script setup>
import { Menu, Setting } from '@element-plus/icons-vue'
import ThemeToggle from './ThemeToggle.vue'
import LanguageSwitcher from './LanguageSwitcher.vue'

defineProps({
  showSidebar: { type: Boolean, default: true },
  showRightPanel: { type: Boolean, default: true }
})

defineEmits(['toggle-left', 'toggle-right'])
</script>

<style lang="scss" scoped>
.header-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-height);
  padding: 0 16px;
  border-bottom: 1px solid var(--border-light);
  background: var(--bg-primary);

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .toolbar-title {
    font-size: var(--font-size-lg);
    font-weight: 600;
    color: var(--text-primary);
  }
}
</style>
