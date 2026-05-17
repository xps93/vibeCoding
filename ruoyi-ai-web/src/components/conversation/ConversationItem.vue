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
        <span class="conv-title" @dblclick.stop="startEdit">{{ item.title || '新对话' }}</span>
        <span class="conv-time">{{ timeText }}</span>
      </template>
    </div>
    <div v-show="!editing && !selectMode" class="conv-actions">
      <el-button size="small" text :icon="Edit" @click.stop="startEdit" />
      <el-popconfirm
        title="确定删除该对话？"
        confirm-button-text="删除"
        cancel-button-text="取消"
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
import { ChatDotRound, Edit, Delete } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/format'

const props = defineProps({
  item: { type: Object, required: true },
  isActive: { type: Boolean, default: false },
  selectMode: { type: Boolean, default: false },
  isSelected: { type: Boolean, default: false }
})

const emit = defineEmits(['select', 'delete', 'rename'])

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

  .conv-actions {
    opacity: 0;
    display: flex;
    flex-shrink: 0;
    transition: opacity 0.15s;
  }
}
</style>
