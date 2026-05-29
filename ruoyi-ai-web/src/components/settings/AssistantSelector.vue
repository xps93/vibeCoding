<template>
  <div class="setting-section">
    <div class="setting-header">
      <el-icon><ChatDotRound /></el-icon>
      <span>{{ $t('settings.assistant') }}</span>
      <el-button
        v-if="store.selectedAssistantId"
        type="text"
        size="mini"
        @click="store.clearSelection(); configStore.updateSystemPrompt(DEFAULT_SYSTEM_PROMPT)"
      >{{ $t('settings.clear') }}</el-button>
    </div>
    <div class="setting-body" v-loading="store.loading">
      <template v-if="store.categories.length > 0">
        <div v-for="cat in store.categories" :key="cat.id" class="assistant-category">
          <div class="category-label">{{ cat.name }}</div>
          <div class="assistant-list">
            <div
              v-for="item in store.groupedAssistants[cat.id]"
              :key="item.id"
              class="assistant-card"
              :class="{ active: store.selectedAssistantId === item.id }"
              @click="selectItem(item)"
            >
              <div class="assistant-card-header">
                <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
                <el-icon v-else><MagicStick /></el-icon>
                <span>{{ item.name }}</span>
              </div>
              <div class="assistant-card-desc" v-if="item.description">{{ item.description }}</div>
            </div>
          </div>
        </div>
      </template>
      <div v-else class="empty-hint">{{ $t('settings.noAssistant') }}</div>
    </div>
  </div>
</template>

<script setup>
import { useAssistantStore } from '@/stores/assistant'
import { useConfigStore } from '@/stores/config'
import { DEFAULT_SYSTEM_PROMPT } from '@/utils/constants'

const store = useAssistantStore()
const configStore = useConfigStore()

function selectItem(item) {
  store.selectAssistant(item.id)
  const prompt = item.prompt || `你是${item.name}。${item.description || '请用简洁清晰的中文回答问题。'}`
  configStore.updateSystemPrompt(prompt)
}
</script>

<style lang="scss" scoped>
.setting-section {
  margin-bottom: 24px;
}

.setting-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--text-primary);

  .el-icon {
    color: var(--color-primary);
  }
}

.assistant-category {
  margin-bottom: 12px;

  &:last-child { margin-bottom: 0; }
}

.category-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 6px;
  padding-left: 2px;
}

.assistant-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.assistant-card {
  padding: 10px 12px;
  border: 1px solid var(--border-light);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: var(--color-primary);
    background: var(--bg-hover);
  }

  &.active {
    border-color: var(--color-primary);
    background: var(--color-primary-light);
  }
}

.assistant-card-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);

  .el-icon {
    font-size: 14px;
    color: var(--color-primary);
  }
}

.assistant-card-desc {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.empty-hint {
  font-size: 13px;
  color: var(--text-secondary);
  text-align: center;
  padding: 16px 0;
}
</style>
