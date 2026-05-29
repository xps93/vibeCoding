<template>
  <div
    class="chat-input-area"
    :class="{ 'drag-over': isDragging }"
    @dragover.prevent="onDragOver"
    @dragenter.prevent="onDragEnter"
    @dragleave="onDragLeave"
    @drop.prevent="onDrop"
  >
    <div v-if="isDragging" class="drag-overlay">
      <div class="drag-hint">
        <el-icon :size="40"><UploadFilled /></el-icon>
        <p>{{ $t('chat.dragHint') }}</p>
      </div>
    </div>

    <div v-if="attachedFile" class="file-preview">
      <div class="file-chip">
        <el-icon class="file-icon"><Document /></el-icon>
        <span class="file-name">{{ attachedFile.name }}</span>
        <span class="file-size">{{ formatSize(attachedFile.size) }}</span>
        <span v-if="parsing" class="file-parsing">
          <el-icon class="is-loading"><Loading /></el-icon> {{ $t('chat.parsingFile') }}
        </span>
        <button class="file-remove" @click="removeFile" title="remove">
          <el-icon :size="14"><Close /></el-icon>
        </button>
      </div>
    </div>

    <div class="input-container">
      <el-input
        ref="inputRef"
        v-model="inputText"
        type="textarea"
        :rows="1"
        :autosize="{ minRows: 1, maxRows: 6 }"
        :placeholder="attachedFile ? $t('chat.docPlaceholder') : $t('chat.placeholder')"
        :disabled="isStreaming"
        resize="none"
        @keydown.enter.exact.prevent="handleSend"
        @paste="onPaste"
      />
      <div class="input-actions">
        <!-- 联网搜索开关 -->
        <el-tooltip :content="$t('chat.webSearch')" placement="top">
          <button
            class="action-icon-btn"
            :class="{ active: chatStore.webSearchEnabled }"
            :disabled="isStreaming"
            @click="chatStore.toggleWebSearch()"
          >
            <el-icon :size="18"><Search /></el-icon>
          </button>
        </el-tooltip>
        <!-- 上传按钮 -->
        <el-tooltip :content="$t('chat.uploadDoc')" placement="top">
          <button class="upload-btn" :disabled="isStreaming" @click="triggerUpload" :title="$t('chat.uploadDoc')">
            <el-icon :size="18"><UploadFilled /></el-icon>
          </button>
        </el-tooltip>
        <input
          ref="fileInputRef"
          type="file"
          accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.md,.csv,.json,.xml,.html"
          style="display:none"
          @change="handleFileChange"
        />
        <span class="token-hint" v-if="inputText">
          ~{{ estimatedTokens }} tokens
        </span>
        <template v-if="isStreaming">
          <el-button type="danger" size="small" @click="$emit('stop')">
            <el-icon><VideoPause /></el-icon>
            {{ $t('chat.stopGeneration') }}
          </el-button>
        </template>
        <template v-else>
          <el-button
            type="primary"
            size="small"
            :disabled="!inputText.trim() && !parsedContent"
            @click="handleSend"
          >
            <el-icon><Promotion /></el-icon>
          </el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { VideoPause, Promotion, UploadFilled, Document, Loading, Close, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useChatStore } from '@/stores/chat'
import { estimateTokens } from '@/utils/format'
import { uploadDocument } from '@/api/document'

const { t } = useI18n()
const chatStore = useChatStore()

const props = defineProps({
  isStreaming: { type: Boolean, default: false }
})

const emit = defineEmits(['send', 'stop'])

const inputText = ref('')
const inputRef = ref(null)
const fileInputRef = ref(null)

const attachedFile = ref(null)
const parsedContent = ref('')
const documentId = ref(null)
const parsing = ref(false)
const isDragging = ref(false)
let dragEnterCount = 0

const estimatedTokens = computed(() => estimateTokens(inputText.value))

function triggerUpload() {
  fileInputRef.value?.click()
}

async function processFile(file) {
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error(t('chat.fileTooLarge'))
    return
  }

  if (attachedFile.value) {
    removeFile()
  }

  attachedFile.value = file
  parsedContent.value = ''
  parsing.value = true

  try {
    const data = await uploadDocument(file, null)
    documentId.value = data.id || null
    parsedContent.value = data.content || ''
    if (data.content) {
      ElMessage.success(t('chat.docParsed', { count: data.content.length }))
    } else {
      ElMessage.warning(t('chat.docNoText'))
    }
  } catch (e) {
    ElMessage.error(t('chat.uploadFailed') + ': ' + (e.message || t('common.networkError')))
    attachedFile.value = null
  } finally {
    parsing.value = false
  }
}

async function handleFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  await processFile(file)
}

function onDragOver(e) {
}

function onDragEnter(e) {
  dragEnterCount++
  if (!props.isStreaming) {
    isDragging.value = true
  }
}

function onDragLeave(e) {
  dragEnterCount--
  if (dragEnterCount <= 0) {
    dragEnterCount = 0
    isDragging.value = false
  }
}

function onDrop(e) {
  isDragging.value = false
  dragEnterCount = 0
  if (props.isStreaming) return

  const files = e.dataTransfer?.files
  if (files && files.length > 0) {
    processFile(files[0])
  }
}

function onPaste(e) {
  if (props.isStreaming) return
  const items = e.clipboardData?.items
  if (!items) return

  for (const item of items) {
    if (item.kind === 'file') {
      e.preventDefault()
      const file = item.getAsFile()
      if (file) {
        processFile(file)
      }
      return
    }
  }
}

function removeFile() {
  attachedFile.value = null
  parsedContent.value = ''
  documentId.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

function handleSend() {
  const text = inputText.value.trim()
  if ((!text && !parsedContent.value) || props.isStreaming) return

  const docId = documentId.value

  emit('send', { content: text || t('chat.docSummaryPrompt'), documentId: docId, attachmentName: attachedFile.value?.name || null })
  inputText.value = ''
  removeFile()
}

function focus() {
  inputRef.value?.focus()
}

function formatSize(bytes) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

defineExpose({ focus })
</script>

<style lang="scss" scoped>
.chat-input-area {
  padding: 12px 24px 16px;
  background: var(--bg-primary);
  border-top: 1px solid var(--border-light);
  position: relative;

  &.drag-over {
    .input-container {
      border-color: var(--accent-color);
      box-shadow: 0 0 0 2px var(--accent-light);
      background: var(--accent-light);
    }
  }
}

.drag-overlay {
  position: absolute;
  inset: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--accent-light);
  border-radius: var(--radius-md);
  pointer-events: none;
}

.drag-hint {
  text-align: center;
  color: var(--accent-color);

  p {
    margin-top: 8px;
    font-size: 15px;
    font-weight: 600;
  }
}

.file-preview {
  max-width: 800px;
  margin: 0 auto 8px;
}

.file-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  background: var(--accent-light);
  border: 1px solid var(--accent-color);
  border-radius: 8px;
  font-size: 13px;

  .file-icon { color: var(--accent-color); }
  .file-name { color: var(--text-primary); font-weight: 500; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .file-size { color: var(--text-tertiary); font-size: 11px; }
  .file-parsing { color: var(--text-secondary); font-size: 12px; display: flex; align-items: center; gap: 4px; }
  .file-remove {
    display: flex; align-items: center; justify-content: center;
    width: 18px; height: 18px; border-radius: 50%;
    border: none; background: var(--bg-tertiary); color: var(--text-tertiary); cursor: pointer;
    &:hover { background: var(--danger-color); color: #fff; }
  }
}

.input-container {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-input);
  transition: border-color 0.2s;

  &:focus-within {
    border-color: var(--accent-color);
    box-shadow: 0 0 0 2px var(--accent-light);
  }
}

:deep(.el-textarea__inner) {
  border: none;
  box-shadow: none;
  background: transparent;
  padding: 4px 0;
  font-size: var(--font-size-base);
  color: var(--text-primary);

  &::placeholder {
    color: var(--text-tertiary);
  }
}

.input-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;

  .token-hint {
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
    white-space: nowrap;
  }
}

.action-icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid transparent;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all 0.15s;

  &:hover:not(:disabled) {
    background: var(--bg-secondary);
    color: var(--accent-color);
    border-color: var(--border-light);
  }

  &.active {
    color: #fff;
    background: var(--accent-color);
    border-color: var(--accent-color);
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.upload-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid transparent;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all 0.15s;

  &:hover:not(:disabled) {
    background: var(--bg-secondary);
    color: var(--accent-color);
    border-color: var(--border-light);
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}
</style>
