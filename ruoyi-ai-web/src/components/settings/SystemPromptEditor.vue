<template>
  <div class="setting-block">
    <div class="setting-label">{{ $t('settings.systemPrompt') }}</div>
    <el-input
      :model-value="configStore.systemPrompt"
      @update:model-value="configStore.updateSystemPrompt"
      type="textarea"
      :rows="4"
      :placeholder="$t('settings.systemPromptPlaceholder')"
      resize="vertical"
    />
  </div>
</template>

<script setup>
import { useConfigStore } from '@/stores/config'

const configStore = useConfigStore()
</script>

<style lang="scss" scoped>
.setting-block {
  margin-bottom: 24px;
}

.setting-label {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}
</style>
