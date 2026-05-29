<template>
  <div class="conversation-list">
    <div class="list-header">
      <ConversationSearch v-if="!showFavoritesOnly" />
      <!-- 收藏夹搜索 -->
      <div v-if="showFavoritesOnly && !conversationStore.selectMode" class="fav-search">
        <el-input
          v-model="favoriteStore.searchKeyword"
          :placeholder="$t('conversation.searchFav')"
          size="small"
          clearable
          @input="onFavSearch"
        >
          <template #prefix>
            <el-icon :size="14"><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="filter-tabs" v-if="!conversationStore.selectMode">
        <button
          class="filter-tab"
          :class="{ active: !showFavoritesOnly }"
          @click="switchTab(false)"
        >{{ $t('conversation.all') }}</button>
        <button
          class="filter-tab"
          :class="{ active: showFavoritesOnly }"
          @click="switchTab(true)"
        >
          <el-icon :size="13"><StarFilled /></el-icon>
          {{ $t('conversation.favorites') }}
        </button>
      </div>
      <!-- 分组快捷选择 -->
      <div v-if="showFavoritesOnly && favoriteStore.groups.length > 0 && !conversationStore.selectMode" class="group-chips">
        <button
          class="group-chip"
          :class="{ active: !favoriteStore.activeGroupId }"
          @click="selectGroup(null)"
        >{{ $t('conversation.all') }}</button>
        <button
          v-for="g in favoriteStore.groups"
          :key="g.id"
          class="group-chip"
          :class="{ active: favoriteStore.activeGroupId === g.id }"
          @click="selectGroup(g.id)"
        >{{ g.name }}</button>
        <button class="group-chip group-add" @click="showAddGroup = true" :title="$t('conversation.newGroup')">
          <el-icon :size="12"><Plus /></el-icon>
        </button>
      </div>
      <!-- 新建分组输入框 -->
      <div v-if="showAddGroup" class="group-add-input">
        <el-input
          v-model="newGroupName"
          size="small"
          :placeholder="$t('conversation.newGroupPlaceholder')"
          @keyup.enter="confirmAddGroup"
          ref="groupInputRef"
        />
        <el-button size="small" type="primary" @click="confirmAddGroup" :disabled="!newGroupName.trim()">{{ $t('common.confirm') }}</el-button>
        <el-button size="small" @click="showAddGroup = false">{{ $t('common.cancel') }}</el-button>
      </div>
      <el-button
        v-if="!showFavoritesOnly"
        type="primary"
        size="small"
        :icon="Plus"
        class="new-chat-btn"
        :loading="creating"
        @click="handleCreate"
      >
        {{ $t('chat.newChat') }}
      </el-button>
      <el-button
        v-if="displayItems.length > 0"
        size="small"
        class="select-mode-btn"
        @click="conversationStore.enterSelectMode()"
      >
        {{ $t('chat.batchManage') }}
      </el-button>
    </div>

    <div v-if="conversationStore.selectMode && filteredItems.length > 0" class="batch-toolbar">
      <el-checkbox
        :model-value="conversationStore.allSelected"
        :indeterminate="conversationStore.selectedIds.length > 0 && !conversationStore.allSelected"
        @change="conversationStore.toggleSelectAll()"
      >
        {{ $t('conversation.selectAllCount', { count: conversationStore.selectedIds.length }) }}
      </el-checkbox>
      <el-popconfirm
        :title="$t('chat.deleteConfirm')"
        :confirm-button-text="$t('common.delete')"
        :cancel-button-text="$t('common.cancel')"
        @confirm="conversationStore.batchRemove()"
      >
        <template #reference>
          <el-button size="small" type="danger" :disabled="conversationStore.selectedIds.length === 0">
            <el-icon><Delete /></el-icon>
            {{ $t('chat.deleteSelected') }}
          </el-button>
        </template>
      </el-popconfirm>
      <el-button size="small" @click="conversationStore.exitSelectMode()">{{ $t('common.cancel') }}</el-button>
    </div>

    <div class="list-body" v-loading="conversationStore.loading">
      <div v-if="displayItems.length === 0" class="list-empty">
        <el-empty :description="emptyDescription" :image-size="60" />
      </div>
      <ConversationItem
        v-for="item in displayItems"
        :key="item.id"
        :item="item"
        :is-active="item.id === conversationStore.activeId"
        :select-mode="conversationStore.selectMode"
        :is-selected="conversationStore.selectedIds.includes(item.id)"
        :favorited="favoriteStore.isFavorited(item.id)"
        :show-group-actions="showFavoritesOnly"
        :groups="favoriteStore.groups"
        @select="conversationStore.setActive"
        @rename="conversationStore.rename"
        @delete="conversationStore.remove"
        @favorite="favoriteStore.toggle"
        @move-group="(convId, groupId) => favoriteStore.moveFavoriteToGroup(convId, groupId)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import { Plus, Delete, StarFilled, Search } from '@element-plus/icons-vue'
import { useConversationStore } from '@/stores/conversation'
import { useFavoriteStore } from '@/stores/favorite'
import { storeToRefs } from 'pinia'
import ConversationSearch from './ConversationSearch.vue'
import ConversationItem from './ConversationItem.vue'

const { t } = useI18n()

const conversationStore = useConversationStore()
const favoriteStore = useFavoriteStore()
const { filteredItems } = storeToRefs(conversationStore)
const creating = ref(false)
const showFavoritesOnly = ref(false)

// 分组管理
const showAddGroup = ref(false)
const newGroupName = ref('')
const groupInputRef = ref(null)

const emptyDescription = computed(() => {
  if (!showFavoritesOnly.value) return t('conversation.noConversations')
  if (favoriteStore.searchKeyword) return t('conversation.noMatchFav')
  if (favoriteStore.activeGroupId) return t('conversation.noGroupFav')
  return t('conversation.noFavorites')
})

// 显示列表：全部中排除已收藏，收藏夹仅显示已收藏
const displayItems = computed(() => {
  if (showFavoritesOnly.value) {
    return filteredItems.value.filter(item => favoriteStore.isFavorited(item.id))
  }
  return filteredItems.value.filter(item => !favoriteStore.isFavorited(item.id))
})

// 对话列表变化时，批量检查收藏状态
watch(filteredItems, (items) => {
  if (items && items.length > 0) {
    const ids = items.map(i => i.id)
    favoriteStore.check(ids)
  }
}, { immediate: true })

// ──────────────── 标签切换 ────────────────

async function switchTab(favorites) {
  showFavoritesOnly.value = favorites
  if (favorites) {
    await favoriteStore.fetchGroups()
    await favoriteStore.fetchList()
  }
}

// ──────────────── 分组操作 ────────────────

function selectGroup(groupId) {
  favoriteStore.activeGroupId = groupId
  favoriteStore.fetchList()
}

async function confirmAddGroup() {
  const name = newGroupName.value.trim()
  if (!name) return
  await favoriteStore.addGroup(name)
  newGroupName.value = ''
  showAddGroup.value = false
}

// ──────────────── 搜索 ────────────────

let searchTimer = null
function onFavSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    favoriteStore.fetchList()
  }, 300)
}

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

  .fav-search {
    padding: 4px 12px 0;
  }

  .filter-tabs {
    display: flex;
    gap: 4px;
    padding: 4px 12px 0;
  }

  .filter-tab {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 4px 12px;
    border-radius: 12px;
    border: 1px solid var(--border-light);
    background: var(--bg-secondary);
    color: var(--text-secondary);
    font-size: 12px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: var(--accent-color);
      color: var(--accent-color);
    }

    &.active {
      border-color: var(--accent-color);
      background: var(--accent-color);
      color: #fff;
    }
  }

  /* 分组快捷芯片 */
  .group-chips {
    display: flex;
    gap: 4px;
    padding: 6px 12px 0;
    overflow-x: auto;
    scrollbar-width: none;
    &::-webkit-scrollbar { display: none; }
  }

  .group-chip {
    display: flex;
    align-items: center;
    gap: 2px;
    padding: 2px 10px;
    border-radius: 10px;
    border: 1px solid var(--border-light);
    background: var(--bg-secondary);
    color: var(--text-secondary);
    font-size: 11px;
    white-space: nowrap;
    cursor: pointer;
    transition: all 0.15s;
    flex-shrink: 0;

    &:hover {
      border-color: var(--accent-color);
      color: var(--accent-color);
    }

    &.active {
      border-color: var(--accent-color);
      background: var(--accent-color);
      color: #fff;
    }
  }

  .group-add {
    opacity: 0.6;
    &:hover { opacity: 1; }
  }

  .group-add-input {
    display: flex;
    gap: 4px;
    padding: 6px 12px 0;
  }

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
