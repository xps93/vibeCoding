<template>
  <div class="setting-block">
    <div class="setting-label">
      Temperature
      <span class="setting-value">{{ configStore.temperature.toFixed(1) }}</span>
    </div>
    <el-slider
      :model-value="configStore.temperature"
      @update:model-value="configStore.updateTemperature"
      :min="0"
      :max="2"
      :step="0.1"
      :marks="marks"
      size="small"
      show-input
    />
    <div class="setting-hint">
      值越高回复越随机，值越低回复越确定
    </div>
  </div>
</template>

<script setup>
import { useConfigStore } from '@/stores/config'

const configStore = useConfigStore()

const marks = {
  0: '0',
  1: '1',
  2: '2'
}
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

.setting-hint {
  margin-top: 4px;
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
}
</style>
