<template>
  <div class="setting-block">
    <div class="setting-label">模型选择</div>
    <el-select
      :model-value="modelStore.selectedId"
      @update:model-value="modelStore.selectModel"
      placeholder="请选择模型"
      size="default"
      style="width: 100%"
      :loading="modelStore.loading"
    >
      <el-option
        v-for="m in modelStore.items"
        :key="m.id"
        :label="m.name"
        :value="m.id"
      >
        <div class="model-option">
          <span>{{ m.name }}</span>
          <el-tag size="small" type="info">{{ m.provider }}</el-tag>
        </div>
      </el-option>
    </el-select>
    <div v-if="selectedModel" class="model-capabilities">
      <el-tag
        v-for="cap in selectedModel.capabilities"
        :key="cap"
        size="small"
        :type="capTagType(cap)"
      >
        {{ cap }}
      </el-tag>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useModelStore } from '@/stores/model'
import { storeToRefs } from 'pinia'

const modelStore = useModelStore()
const { selectedModel } = storeToRefs(modelStore)

onMounted(() => {
  if (modelStore.items.length === 0) {
    modelStore.fetchModels()
  }
})

function capTagType(cap) {
  const map = { chat: '', code: 'warning', reasoning: 'success' }
  return map[cap] || 'info'
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
}

.model-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.model-capabilities {
  margin-top: 8px;
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}
</style>
