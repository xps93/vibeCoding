<template>
  <div
    class="conversation-item"
    :class="{ active: isActive }"
    @click="handleClick"
  >
    <el-checkbox
      v-if="selectMode"
      :model-value="isSelected"
      @click.stop
      @change="$emit('select', item.id)"
      class="conv-checkbox"
    />
    <div v-else class="conv-icon">
      <el-icon><ChatDotRound /></el-icon>
    </div>
    <div class="conv-content">
      <template v-if="editing">
        <el-input
          v-model="editTitle"
          size="small"
          @blur="confirmEdit"
          @keyup.enter="confirmEdit"
          @click.stop
        />
      </template>
      <template v-else>
        <span class="conv-title" @dblclick.stop="startEdit">{{ item.title || $t('chat.newChat') }}</span>
        <span class="conv-time">{{ timeText }}</span>
      </template>
    </div>
    <!-- 收藏星标：收藏后始终可见 -->
    <button
      v-show="!editing && !selectMode"
      class="fav-star"
      :class="{ 'is-favorited': favorited }"
      @click.stop="$emit('favorite', item.id)"
      :title="favorited ? $t('conversation.unfavorite') : $t('conversation.favorite')"
    >
      <el-icon :size="14"><StarFilled v-if="favorited" /><Star v-else /></el-icon>
    </button>
    <div v-show="!editing && !selectMode" class="conv-actions">
      <!-- 分组移动（仅在收藏夹显示） -->
      <el-dropdown
        v-if="showGroupActions && favorited && groups.length > 0"
        trigger="click"
        @command="(gid) => $emit('moveGroup', item.id, gid)"
        @click.stop
      >
        <button class="action-btn group-btn" :title="$t('conversation.moveToGroup')">
          <el-icon :size="12"><FolderOpened /></el-icon>
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="">{{ $t('conversation.ungrouped') }}</el-dropdown-item>
            <el-dropdown-item
              v-for="g in groups"
              :key="g.id"
              :command="g.id"
            >{{ g.name }}</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-button size="small" text :icon="Edit" @click.stop="startEdit" />
      <el-popconfirm
        :title="$t('conversation.deleteConv')"
        :confirm-button-text="$t('common.delete')"
        :cancel-button-text="$t('common.cancel')"
        @confirm="$emit('delete', item.id)"
        @click.stop
      >
        <template #reference>
          <el-button size="small" text :icon="Delete" />
        </template>
      </el-popconfirm>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ChatDotRound, Edit, Delete, Star, StarFilled, FolderOpened } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/format'

const props = defineProps({
  item: { type: Object, required: true },
  isActive: { type: Boolean, default: false },
  selectMode: { type: Boolean, default: false },
  isSelected: { type: Boolean, default: false },
  favorited: { type: Boolean, default: false },
  showGroupActions: { type: Boolean, default: false },
  groups: { type: Array, default: () => [] }
})

const emit = defineEmits(['select', 'delete', 'rename', 'favorite', 'moveGroup'])

const editing = ref(false)
const editTitle = ref('')

const timeText = computed(() => {
  return props.item.updateTime || props.item.createTime
    ? formatDate(props.item.updateTime || props.item.createTime)
    : ''
})

function handleClick() {
  emit('select', props.item.id)
}

function startEdit() {
  editTitle.value = props.item.title || ''
  editing.value = true
}

function confirmEdit() {
  editing.value = false
  const newTitle = editTitle.value.trim()
  if (newTitle && newTitle !== props.item.title) {
    emit('rename', props.item.id, newTitle)
  }
}
</script>

<style lang="scss" scoped>
.conversation-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  margin: 2px 8px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.15s;
  gap: 10px;

  &:hover {
    background: var(--bg-hover);
    .fav-star { opacity: 1; }
    .conv-actions { opacity: 1; }
  }

  &.active {
    background: var(--accent-light);
    color: var(--accent-color);
  }

  .conv-checkbox {
    flex-shrink: 0;
  }

  .conv-icon {
    flex-shrink: 0;
    font-size: 16px;
    color: var(--text-tertiary);
  }

  .conv-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 2px;

    .conv-title {
      font-size: var(--font-size-sm);
      color: var(--text-primary);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .conv-time {
      font-size: var(--font-size-xs);
      color: var(--text-tertiary);
    }
  }

  .fav-star {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    border: none;
    border-radius: 4px;
    background: transparent;
    color: var(--text-tertiary);
    cursor: pointer;
    flex-shrink: 0;
    opacity: 0;
    transition: opacity 0.15s, color 0.15s;

    &:hover {
      color: #f59e0b;
    }

    &.is-favorited {
      opacity: 1;
      color: #f59e0b;
    }
  }

  .conv-actions {
    opacity: 0;
    display: flex;
    align-items: center;
    flex-shrink: 0;
    transition: opacity 0.15s;
  }

  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    border: none;
    border-radius: 4px;
    background: transparent;
    color: var(--text-tertiary);
    cursor: pointer;

    &:hover {
      background: var(--bg-hover);
      color: var(--accent-color);
    }
  }
}
</style>
