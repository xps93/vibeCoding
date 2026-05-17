<template>
  <div class="conversation-list">
    <div class="list-header">
      <ConversationSearch />
      <el-button
        type="primary"
        size="small"
        :icon="Plus"
        class="new-chat-btn"
        :loading="creating"
        @click="handleCreate"
      >
        新对话
      </el-button>
      <el-button
        v-if="filteredItems.length > 0"
        size="small"
        class="select-mode-btn"
        @click="conversationStore.enterSelectMode()"
      >
        批量管理
      </el-button>
    </div>

    <div v-if="conversationStore.selectMode && filteredItems.length > 0" class="batch-toolbar">
      <el-checkbox
        :model-value="conversationStore.allSelected"
        :indeterminate="conversationStore.selectedIds.length > 0 && !conversationStore.allSelected"
        @change="conversationStore.toggleSelectAll()"
      >
        全选 ({{ conversationStore.selectedIds.length }})
      </el-checkbox>
      <el-popconfirm
        title="确定删除选中的对话？此操作不可撤销"
        confirm-button-text="删除"
        cancel-button-text="取消"
        @confirm="conversationStore.batchRemove()"
      >
        <template #reference>
          <el-button size="small" type="danger" :disabled="conversationStore.selectedIds.length === 0">
            <el-icon><Delete /></el-icon>
            删除选中
          </el-button>
        </template>
      </el-popconfirm>
      <el-button size="small" @click="conversationStore.exitSelectMode()">取消</el-button>
    </div>

    <div class="list-body" v-loading="conversationStore.loading">
      <div v-if="filteredItems.length === 0" class="list-empty">
        <el-empty description="暂无对话" :image-size="60" />
      </div>
      <ConversationItem
        v-for="item in filteredItems"
        :key="item.id"
        :item="item"
        :is-active="item.id === conversationStore.activeId"
        :select-mode="conversationStore.selectMode"
        :is-selected="conversationStore.selectedIds.includes(item.id)"
        @select="conversationStore.setActive"
        @rename="conversationStore.rename"
        @delete="conversationStore.remove"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { useConversationStore } from '@/stores/conversation'
import { storeToRefs } from 'pinia'
import ConversationSearch from './ConversationSearch.vue'
import ConversationItem from './ConversationItem.vue'

const conversationStore = useConversationStore()
const { filteredItems } = storeToRefs(conversationStore)
const creating = ref(false)

async function handleCreate() {
  creating.value = true
  try {
    await conversationStore.create()
  } finally {
    creating.value = false
  }
}
</script>

<style lang="scss" scoped>
.conversation-list {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.list-header {
  padding: 0 4px 8px;
  border-bottom: 1px solid var(--border-light);

  .new-chat-btn {
    margin: 0 12px;
    width: calc(100% - 24px);
  }

  .select-mode-btn {
    margin: 8px 12px 0;
    width: calc(100% - 24px);
  }
}

.batch-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-bottom: 1px solid var(--border-light);
  background: var(--bg-tertiary);
  flex-wrap: wrap;
}

.list-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.list-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
}
</style>
