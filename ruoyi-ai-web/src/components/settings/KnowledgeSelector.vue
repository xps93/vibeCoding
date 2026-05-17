<template>
  <div class="setting-block">
    <div class="setting-label">
      知识库
      <span v-if="selectedCount > 0" class="selected-count">已选{{ selectedCount }}个</span>
    </div>
    <el-select
      :model-value="knowledgeStore.selectedIds"
      @update:model-value="onChange"
      multiple
      placeholder="选择知识库"
      size="default"
      style="width: 100%"
      :loading="knowledgeStore.loading"
    >
      <el-option
        v-for="kb in knowledgeStore.items"
        :key="kb.id"
        :label="kb.name"
        :value="kb.id"
      />
    </el-select>
    <div v-if="selectedIds.length > 0" class="selected-tags">
      <el-tag
        v-for="id in selectedIds"
        :key="id"
        size="small"
        closable
        @close="knowledgeStore.toggle(id)"
      >
        {{ getKbName(id) }}
      </el-tag>
    </div>
    <div v-if="knowledgeStore.items.length === 0 && !knowledgeStore.loading" class="empty-hint">
      暂无可用知识库
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useKnowledgeStore } from '@/stores/knowledge'
import { storeToRefs } from 'pinia'

const knowledgeStore = useKnowledgeStore()
const { selectedIds } = storeToRefs(knowledgeStore)

const selectedCount = computed(() => knowledgeStore.selectedIds.length)

onMounted(() => {
  knowledgeStore.fetchKnowledgeBases()
})

function onChange(val) {
  knowledgeStore.selectedIds = val
}

function getKbName(id) {
  const kb = knowledgeStore.items.find(i => i.id === id)
  return kb ? kb.name : id
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

  .selected-count {
    font-weight: 400;
    color: var(--accent-color);
  }
}

.selected-tags {
  margin-top: 8px;
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.empty-hint {
  margin-top: 4px;
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
}
</style>
