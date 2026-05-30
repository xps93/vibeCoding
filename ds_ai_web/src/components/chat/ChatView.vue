<template>
  <div class="chat-view">
    <HeaderToolbar
      :show-sidebar="showLeftSidebar"
      :show-right-panel="showRightPanel"
      @toggle-left="$emit('toggle-left')"
      @toggle-right="$emit('toggle-right')"
    />
    <div ref="messageContainer" class="message-area" @scroll="onScroll">
      <div v-if="chatError" class="chat-error">
        <el-alert :title="chatError" type="error" show-icon closable @close="chatStore.error = null" />
        <el-button type="primary" size="small" @click="retryLast">{{ $t('chat.retry') }}</el-button>
      </div>
      <EmptyState
        v-if="!chatStore.hasMessages && !chatStore.isStreaming"
        @send="handleSend"
      />
      <div v-else class="message-list" ref="messageListRef">
        <ChatMessage
          v-for="(msg, i) in chatStore.allMessages"
          :key="i"
          :message="msg"
          :message-index="i"
          @export="handleExport"
          @regenerate="handleRegenerate"
          @edit-done="handleEditDone"
        />
      </div>
    </div>

    <!-- 聊天工具栏：助手选择 + 消息导航 -->
    <div v-if="chatStore.hasMessages || assistantStore.flatAssistants.length > 0" class="chat-toolbar">
      <div class="quick-assistant-bar" v-if="assistantStore.flatAssistants.length > 0">
        <span class="toolbar-label">{{ $t('settings.assistant') }}</span>
        <div class="assistant-scroll">
          <button
            v-for="item in assistantStore.flatAssistants"
            :key="item.id"
            class="assistant-chip"
            :class="{ active: assistantStore.selectedAssistantId === item.id }"
            @click="selectAssistant(item)"
            :title="item.description"
          >
            <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
            <span>{{ item.name }}</span>
          </button>
        </div>
        <button
          v-if="assistantStore.selectedAssistantId"
          class="assistant-clear-btn"
          @click="assistantStore.clearSelection(); configStore.updateSystemPrompt(DEFAULT_SYSTEM_PROMPT)"
          :title="$t('chat.clearAssistant')"
        >
          <el-icon :size="12"><Close /></el-icon>
        </button>
      </div>

      <div class="chat-toolbar-actions">
        <!-- 分享按钮 -->
        <el-tooltip :content="shareUrl ? $t('chat.shareLinkCopied') : $t('chat.shareConversation')" placement="top">
          <button class="toolbar-btn" @click="handleShare">
            <el-icon :size="15"><component :is="shareUrl ? Link : Share" /></el-icon>
          </button>
        </el-tooltip>

        <!-- 消息导航 -->
        <div class="message-nav" v-if="chatStore.hasMessages">
          <el-tooltip :content="$t('nav.firstQuestion')" placement="top">
            <button class="nav-chip" @click="goToFirstQuestion" :disabled="!hasQuestions">
              <el-icon :size="14"><Top /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="$t('nav.prevQuestion')" placement="top">
            <button class="nav-chip" @click="goToPrevQuestion" :disabled="!hasQuestions">
              <el-icon :size="14"><ArrowUpBold /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="$t('nav.nextQuestion')" placement="top">
            <button class="nav-chip" @click="goToNextQuestion" :disabled="!hasQuestions">
              <el-icon :size="14"><ArrowDownBold /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="$t('nav.latestMessage')" placement="top">
            <button class="nav-chip" @click="goToLatest">
              <el-icon :size="14"><Bottom /></el-icon>
            </button>
          </el-tooltip>
        </div>
      </div>
    </div>

    <ChatInput
      ref="inputRef"
      :is-streaming="chatStore.isStreaming"
      @send="handleSend"
      @stop="handleStop"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { storeToRefs } from 'pinia'
import { Top, Bottom, ArrowUpBold, ArrowDownBold, Close, Share, Link } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useChatStore } from '@/stores/chat'
import { useConversationStore } from '@/stores/conversation'
import { useAssistantStore } from '@/stores/assistant'
import { useConfigStore } from '@/stores/config'
import { DEFAULT_SYSTEM_PROMPT } from '@/utils/constants'
import { useSseChat } from '@/composables/useSseChat'
import { useAutoScroll } from '@/composables/useAutoScroll'
import { useMobile } from '@/composables/useMobile'
import { useExport } from '@/composables/useExport'
import { createShare } from '@/api/chat'
import { queryDataset } from '@/api/dataset'
import { useDatasetStore } from '@/stores/dataset'
import HeaderToolbar from '@/components/common/HeaderToolbar.vue'
import EmptyState from './EmptyState.vue'
import ChatMessage from './ChatMessage.vue'
import ChatInput from './ChatInput.vue'

defineEmits(['toggle-left', 'toggle-right'])

const { t } = useI18n()
const chatStore = useChatStore()
const conversationStore = useConversationStore()
const assistantStore = useAssistantStore()
const configStore = useConfigStore()
const datasetStore = useDatasetStore()
const { error: chatError } = storeToRefs(chatStore)
const { showLeftSidebar, showRightPanel } = useMobile()
const { send, reGen, stop } = useSseChat()
const { exportConversation } = useExport()

const messageContainer = ref(null)
const messageListRef = ref(null)
const inputRef = ref(null)
const lastUserMessage = ref('')
const shareUrl = ref('')

const hasQuestions = computed(() => {
  return chatStore.messages.some(m => m.role === 'user')
})

const { onScroll, isAtBottom, scrollToBottom } = useAutoScroll(messageContainer, computed(() => chatStore.allMessages.length))

// ──────────────── 助手选择 ────────────────

function selectAssistant(item) {
  assistantStore.selectAssistant(item.id)
  const prompt = item.prompt || `你是${item.name}。${item.description || '请用简洁清晰的中文回答问题。'}`
  configStore.updateSystemPrompt(prompt)
}

// ──────────────── 消息导航 ────────────────

function getUserMessageElements() {
  const container = messageContainer.value
  if (!container) return []
  return Array.from(container.querySelectorAll('[data-role="user"]'))
}

function findCurrentQuestionIndex(elements) {
  const container = messageContainer.value
  if (!container || elements.length === 0) return -1
  const viewTop = container.scrollTop
  for (let i = 0; i < elements.length; i++) {
    const elTop = elements[i].offsetTop
    if (elTop > viewTop + 10) {
      return Math.max(0, i - 1)
    }
  }
  return elements.length - 1
}

function scrollToElement(el) {
  if (!el) return
  if (chatStore.isStreaming) {
    el.scrollIntoView({ behavior: 'auto', block: 'start' })
    const container = messageContainer.value
    if (container) {
      const atBottom = container.scrollTop >= container.scrollHeight - container.clientHeight - 20
      isAtBottom.value = atBottom
    }
  } else {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

function goToFirstQuestion() {
  const elements = getUserMessageElements()
  if (elements.length > 0) scrollToElement(elements[0])
}

function goToPrevQuestion() {
  const elements = getUserMessageElements()
  if (elements.length === 0) return
  const idx = findCurrentQuestionIndex(elements)
  const prevIdx = Math.max(0, idx - 1)
  scrollToElement(elements[prevIdx])
}

function goToNextQuestion() {
  const elements = getUserMessageElements()
  if (elements.length === 0) return
  const idx = findCurrentQuestionIndex(elements)
  const nextIdx = Math.min(elements.length - 1, idx + 1)
  scrollToElement(elements[nextIdx])
}

function goToLatest() {
  const container = messageContainer.value
  if (!container) return
  if (chatStore.isStreaming) {
    container.scrollTop = container.scrollHeight
    isAtBottom.value = true
    return
  }
  const allMsgs = container.querySelectorAll('[data-role]')
  if (allMsgs.length > 0) {
    allMsgs[allMsgs.length - 1].scrollIntoView({ behavior: 'smooth', block: 'end' })
  } else {
    container.scrollTop = container.scrollHeight
  }
}

// ──────────────── 发送 / 停止 ────────────────

async function handleSend(payload) {
  if (typeof payload === 'string') {
    lastUserMessage.value = payload
    await send(payload)
  } else if (payload.datasetQuery) {
    // 本地数据集查询模式
    await handleDatasetQuery(payload)
  } else {
    lastUserMessage.value = payload.content
    await send(payload.content, payload.documentId, payload.attachmentName)
  }
}

async function handleDatasetQuery(payload) {
  if (!payload.content || !payload.datasetId) return
  if (chatStore.isStreaming) return

  if (!conversationStore.activeId) {
    await conversationStore.create()
    if (!conversationStore.activeId) return
  }

  const queryText = payload.content.trim()
  chatStore.addMessage({ role: 'user', content: queryText })

  try {
    const data = await queryDataset(payload.datasetId, queryText)
    const resultMsg = formatDatasetResult(data, queryText)
    chatStore.addMessage({ role: 'assistant', content: resultMsg })
  } catch (e) {
    chatStore.addMessage({
      role: 'assistant',
      content: '数据集查询失败: ' + (e.message || '网络错误')
    })
  }
}

function formatDatasetResult(data, query) {
  if (!data || !data.rows || data.rows.length === 0) {
    return `**数据集"${data?.datasetName || '未知'}"查询结果**\n\n未找到与"${query}"匹配的数据。`
  }

  const columns = data.columns || []
  let markdown = `**数据集"${data.datasetName}"查询结果**\n\n`
  markdown += `查询: "${query}" | 匹配 ${data.matchCount} 条 / 共 ${data.totalRows} 条 | 显示前 ${data.rows.length} 条\n\n`

  // 构建表格
  if (columns.length > 0) {
    // 表头
    markdown += '| ' + columns.join(' | ') + ' |\n'
    markdown += '| ' + columns.map(() => '---').join(' | ') + ' |\n'
    // 数据行
    for (const row of data.rows) {
      const cells = columns.map(col => {
        const val = row[col]
        return val !== null && val !== undefined ? String(val) : ''
      })
      markdown += '| ' + cells.join(' | ') + ' |\n'
    }
  } else {
    for (const row of data.rows) {
      markdown += '- ' + JSON.stringify(row) + '\n'
    }
  }

  return markdown
}

async function handleStop() {
  await stop()
}

function retryLast() {
  chatStore.error = null
  if (lastUserMessage.value) {
    handleSend(lastUserMessage.value)
  }
}

// ──────────────── 重新生成 ────────────────

async function handleRegenerate(messageIndex) {
  // 找到该AI回复之前最近的用户消息
  const msgs = chatStore.messages
  let userContent = lastUserMessage.value
  for (let i = messageIndex - 1; i >= 0; i--) {
    if (msgs[i].role === 'user') {
      userContent = msgs[i].content
      break
    }
  }
  // 删除该AI消息及之后的所有消息
  chatStore.messages.splice(messageIndex)
  await reGen(userContent)
}

// ──────────────── 编辑后重新发送 ────────────────

async function handleEditDone(messageIndex, newContent) {
  chatStore.editMessage(messageIndex, newContent)
  lastUserMessage.value = newContent
  // 删除该编辑消息之后的所有消息
  chatStore.messages.splice(messageIndex + 1)
  await reGen(newContent)
}

// ──────────────── 分享 ─────────────

async function handleShare() {
  if (!conversationStore.activeId) {
    ElMessage.warning(t('chat.noConversation'))
    return
  }
  if (shareUrl.value) {
    // 已生成分享链接，直接复制
    navigator.clipboard.writeText(shareUrl.value)
    ElMessage.success(t('chat.shareLinkCopied'))
    return
  }
  try {
    const res = await createShare(conversationStore.activeId)
    // request.js 拦截器已解包 res.data，此处直接拿到 { shareToken, shareUrl }
    if (res && res.shareToken) {
      const base = window.location.origin
      shareUrl.value = base + res.shareUrl
      navigator.clipboard.writeText(shareUrl.value)
      ElMessage.success(t('chat.shareLinkCopied'))
    }
  } catch (e) {
    ElMessage.error(t('chat.shareFailed'))
  }
}

// ──────────────── 导出 ────────────────

function handleExport(format) {
  const conv = conversationStore.items.find(c => c.id === conversationStore.activeId)
  exportConversation(chatStore.allMessages, conv?.title || '对话', format)
}
</script>

<style lang="scss" scoped>
.chat-view {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.message-area {
  flex: 1;
  overflow-y: auto;
}

.chat-error {
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.message-list {
  padding-bottom: 8px;
}

/* ──────────────── 聊天工具栏 ──────────────── */
.chat-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 24px;
  gap: 12px;
  background: var(--bg-primary);
  border-top: 1px solid var(--border-light);
  overflow: hidden;
  min-height: 40px;
}

.chat-toolbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.toolbar-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
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
}

.quick-assistant-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.toolbar-label {
  font-size: 12px;
  color: var(--text-tertiary);
  flex-shrink: 0;
  margin-right: 2px;
}

.assistant-scroll {
  display: flex;
  gap: 6px;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  &::-webkit-scrollbar { display: none; }
}

.assistant-chip {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 14px;
  border: 1px solid var(--border-light);
  background: var(--bg-secondary);
  color: var(--text-secondary);
  font-size: 12px;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;

  &:hover {
    border-color: var(--accent-color);
    color: var(--accent-color);
    background: var(--accent-light);
  }

  &.active {
    border-color: var(--accent-color);
    background: var(--accent-color);
    color: #fff;

    .el-icon { color: #fff; }
  }

  .el-icon { font-size: 13px; }
}

.assistant-clear-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: none;
  background: var(--bg-tertiary);
  color: var(--text-tertiary);
  cursor: pointer;
  flex-shrink: 0;

  &:hover {
    background: var(--danger-color);
    color: #fff;
  }
}

.message-nav {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-shrink: 0;
}

.nav-chip {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: 1px solid transparent;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all 0.15s;

  &:hover:not(:disabled) {
    background: var(--bg-secondary);
    color: var(--text-primary);
    border-color: var(--border-light);
  }

  &:disabled {
    opacity: 0.3;
    cursor: not-allowed;
  }
}

@media (max-width: 767px) {
  .chat-toolbar {
    padding: 4px 12px;
    gap: 8px;
    flex-wrap: wrap;
  }

  .assistant-chip {
    padding: 3px 8px;
    font-size: 11px;
  }
}
</style>
