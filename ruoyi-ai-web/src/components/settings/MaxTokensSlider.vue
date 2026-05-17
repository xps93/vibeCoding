<template>
  <div class="setting-block">
    <div class="setting-label">
      最大 Token 数
      <span class="setting-value">{{ configStore.maxTokens }}</span>
    </div>
    <el-slider
      :model-value="configStore.maxTokens"
      @update:model-value="configStore.updateMaxTokens"
      :min="64"
      :max="8192"
      :step="64"
      size="small"
      show-input
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
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.setting-value {
  font-weight: 500;
  color: var(--accent-color);
}
</style>
