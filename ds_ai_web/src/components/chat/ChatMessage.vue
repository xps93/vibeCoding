<template>
  <div class="chat-message" :class="[message.role]" :data-role="message.role">
    <div class="message-avatar">
      <el-avatar v-if="message.role === 'user'" :size="32" icon="UserFilled" />
      <el-avatar v-else :size="32">
        <el-icon :size="20"><Cpu /></el-icon>
      </el-avatar>
    </div>
    <div class="message-body">
      <div class="message-role">
        {{ message.role === 'user' ? $t('chat.me') : $t('chat.aiAssistant') }}
        <span v-if="message.streaming" class="typing-dot" />
      </div>
      <div v-if="message.role === 'user'" class="message-text-wrapper">
        <template v-if="editing">
          <el-input
            v-model="editContent"
            type="textarea"
            :rows="2"
            class="edit-input"
            @keyup.enter.exact.prevent="confirmEdit"
            @keyup.esc="cancelEdit"
          />
          <div class="edit-actions">
            <el-button size="small" type="primary" @click="confirmEdit" :disabled="!editContent.trim()">{{ $t('common.confirm') }}</el-button>
            <el-button size="small" @click="cancelEdit">{{ $t('common.cancel') }}</el-button>
          </div>
        </template>
        <div v-else class="message-text">
          {{ message.content }}
        </div>
        <div v-if="!message.streaming && !editing" class="message-actions user-actions">
          <el-tooltip :content="$t('common.edit')" placement="top">
            <button class="action-btn" @click="startEdit">
              <el-icon :size="14"><EditPen /></el-icon>
            </button>
          </el-tooltip>
        </div>
      </div>
      <template v-else>
        <div v-if="message.reasoningContent" class="reasoning-section">
          <div class="reasoning-header" @click="showReasoning = !showReasoning">
            <el-icon :size="14"><component :is="showReasoning ? ArrowDownBold : ArrowRightBold" /></el-icon>
            <span>{{ $t('chat.deepThinking') }}</span>
            <span v-if="!showReasoning" class="reasoning-preview">{{ previewReasoning }}</span>
          </div>
          <div v-show="showReasoning" class="reasoning-content">
            <MarkdownRenderer :content="message.reasoningContent" />
          </div>
        </div>
        <MarkdownRenderer :content="message.content" />
        <div v-if="!message.streaming" class="message-actions">
          <el-tooltip :content="$t('common.copy')" placement="top">
            <button class="action-btn" @click="copyContent">
              <el-icon :size="14"><CopyDocument /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="$t('chat.regenerate')" placement="top">
            <button class="action-btn" @click="handleRegenerate">
              <el-icon :size="14"><Refresh /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="$t('chat.thumbUp')" placement="top">
            <button class="action-btn" :class="{ active: rated === 1 }" @click="handleRate(1)">
              <el-icon :size="14"><component :is="rated === 1 ? StarFilled : Star" /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="$t('chat.thumbDown')" placement="top">
            <button class="action-btn" :class="{ active: rated === 0 }" @click="handleRate(0)">
              <el-icon :size="14"><component :is="rated === 0 ? StarFilled : Star" /></el-icon>
            </button>
          </el-tooltip>
          <el-dropdown trigger="click" @command="handleDownload">
            <button class="action-btn" :title="$t('chat.exportChat')">
              <el-icon :size="14"><Download /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="md">
                  <el-icon><Document /></el-icon> {{ $t('chat.exportMd') }}
                </el-dropdown-item>
                <el-dropdown-item command="txt">
                  <el-icon><Document /></el-icon> {{ $t('chat.exportTxt') }}
                </el-dropdown-item>
                <el-dropdown-item command="json">
                  <el-icon><Document /></el-icon> {{ $t('chat.exportJson') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </template>
      <div v-if="message.streaming" class="typing-indicator">
        <span class="dot" />
        <span class="dot" />
        <span class="dot" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Cpu, ArrowDownBold, ArrowRightBold, CopyDocument, Download, Document, EditPen, Refresh, Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useChatStore } from '@/stores/chat'
import { updateMessage, rateMessage, cancelRate } from '@/api/chat'
import MarkdownRenderer from './MarkdownRenderer.vue'

const { t } = useI18n()

const props = defineProps({
  message: { type: Object, required: true },
  messageIndex: { type: Number, default: -1 }
})

const emit = defineEmits(['export', 'regenerate', 'edit-done'])

const chatStore = useChatStore()
const showReasoning = ref(true)
const editing = ref(false)
const editContent = ref('')

const previewReasoning = computed(() => {
  const content = props.message.reasoningContent || ''
  return content.length > 60 ? content.substring(0, 60) + '...' : content
})

// 获取该消息的评分状态
const rated = computed(() => {
  // message 对象上可能没有 id（前端临时消息），通过 index 匹配
  const msgId = props.message.id
  if (msgId) {
    return chatStore.messageRatings[msgId] !== undefined ? chatStore.messageRatings[msgId] : -1
  }
  return -1
})

function copyContent() {
  navigator.clipboard.writeText(props.message.content).then(() => {
    ElMessage.success(t('chat.copySuccess'))
  }).catch(() => {
    ElMessage.error(t('chat.copyFailed'))
  })
}

function handleDownload(format) {
  emit('export', format)
}

function handleRegenerate() {
  emit('regenerate', props.messageIndex)
}

// ──────── 评分 ────────
async function handleRate(rating) {
  const msgId = props.message.id
  if (!msgId) return
  const currentRating = chatStore.messageRatings[msgId]
  if (currentRating === rating) {
    // 取消评分
    try {
      await cancelRate(msgId)
      chatStore.clearMessageRating(msgId)
    } catch (e) { /* ignore */ }
  } else {
    try {
      await rateMessage(msgId, rating)
      chatStore.setMessageRating(msgId, rating)
    } catch (e) { /* ignore */ }
  }
}

// ──────── 编辑 ────────
function startEdit() {
  editContent.value = props.message.content
  editing.value = true
}

async function confirmEdit() {
  const newContent = editContent.value.trim()
  if (!newContent) return
  const msgId = props.message.id
  if (msgId) {
    try {
      await updateMessage(msgId, newContent)
    } catch (e) { /* ignore */ }
  }
  editing.value = false
  emit('edit-done', props.messageIndex, newContent)
}

function cancelEdit() {
  editing.value = false
  editContent.value = ''
}
</script>

<style lang="scss" scoped>
.chat-message {
  display: flex;
  gap: 12px;
  padding: 16px 24px;
  animation: fadeIn 0.3s;

  &.assistant {
    justify-content: flex-start;

    .message-body {
      background: var(--bg-message-ai);
      border-radius: var(--radius-lg) var(--radius-lg) var(--radius-lg) var(--radius-sm);
      max-width: 75%;
    }
  }

  &.user {
    flex-direction: row-reverse;

    .message-body {
      background: var(--bg-message-user);
      border-radius: var(--radius-lg) var(--radius-lg) var(--radius-sm) var(--radius-lg);
      max-width: 75%;
    }

    .message-role {
      justify-content: flex-end;
    }
  }

  .message-avatar {
    flex-shrink: 0;
    padding-top: 4px;
  }

  .message-body {
    padding: 10px 16px;
    position: relative;
    min-width: 60px;
  }

  .message-role {
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
    margin-bottom: 4px;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .message-text-wrapper {
    position: relative;
  }

  .message-text {
    font-size: var(--font-size-base);
    line-height: 1.7;
    white-space: pre-wrap;
    word-break: break-word;
    color: var(--text-primary);
  }

  .edit-input {
    margin-bottom: 8px;
  }

  .edit-actions {
    display: flex;
    gap: 6px;
    justify-content: flex-end;
  }

  .typing-indicator {
    display: flex;
    gap: 4px;
    padding: 4px 0;

    .dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: var(--text-tertiary);
      animation: typing 1.4s infinite ease-in-out;

      &:nth-child(2) { animation-delay: 0.2s; }
      &:nth-child(3) { animation-delay: 0.4s; }
    }
  }

  .typing-dot {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: var(--accent-color);
    display: inline-block;
    animation: pulse 1s infinite;
  }

  .reasoning-section {
    margin-bottom: 12px;
    border: 1px solid var(--border-color);
    border-radius: var(--radius-md);
    background: var(--bg-secondary);
    overflow: hidden;

    .reasoning-header {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 12px;
      cursor: pointer;
      font-size: var(--font-size-sm);
      color: var(--text-secondary);
      user-select: none;

      &:hover {
        background: var(--bg-tertiary);
      }

      .reasoning-preview {
        color: var(--text-tertiary);
        font-size: var(--font-size-xs);
        margin-left: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex: 1;
      }
    }

    .reasoning-content {
      padding: 8px 12px 12px;
      border-top: 1px solid var(--border-color);
      font-size: var(--font-size-sm);
      color: var(--text-secondary);
      line-height: 1.65;
    }
  }

  .message-actions {
    display: flex;
    align-items: center;
    gap: 2px;
    margin-top: 6px;
    padding-top: 6px;
    border-top: 1px solid var(--border-light);
    opacity: 0;
    transition: opacity 0.2s;
  }

  .user-actions {
    border-top: none;
    padding-top: 4px;
    margin-top: 2px;
  }

  .message-body:hover .message-actions {
    opacity: 1;
  }

  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 26px;
    height: 26px;
    border-radius: 6px;
    border: 1px solid transparent;
    background: transparent;
    color: var(--text-tertiary);
    cursor: pointer;
    transition: all 0.15s;

    &:hover {
      background: var(--bg-secondary);
      color: var(--text-primary);
      border-color: var(--border-light);
    }

    &.active {
      color: var(--accent-color);
      background: var(--accent-light);
      border-color: var(--accent-color);
    }
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes typing {
  0%, 60%, 100% { opacity: 0.3; transform: scale(1); }
  30% { opacity: 1; transform: scale(1.2); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}
</style>
