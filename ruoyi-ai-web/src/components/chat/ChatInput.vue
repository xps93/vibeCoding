<template>
  <div class="chat-input-area">
    <div class="input-container">
      <el-input
        ref="inputRef"
        v-model="inputText"
        type="textarea"
        :rows="1"
        :autosize="{ minRows: 1, maxRows: 6 }"
        placeholder="输入消息... (Enter 发送, Shift+Enter 换行)"
        :disabled="isStreaming"
        resize="none"
        @keydown.enter.exact.prevent="handleSend"
      />
      <div class="input-actions">
        <span class="token-hint" v-if="inputText">
          ~{{ estimatedTokens }} tokens
        </span>
        <template v-if="isStreaming">
          <el-button type="danger" size="small" @click="$emit('stop')">
            <el-icon><VideoPause /></el-icon>
            停止生成
          </el-button>
        </template>
        <template v-else>
          <el-button
            type="primary"
            size="small"
            :disabled="!inputText.trim()"
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
import { ref, computed, watch } from 'vue'
import { VideoPause, Promotion } from '@element-plus/icons-vue'
import { estimateTokens } from '@/utils/format'

const props = defineProps({
  isStreaming: { type: Boolean, default: false }
})

const emit = defineEmits(['send', 'stop'])

const inputText = ref('')
const inputRef = ref(null)

const estimatedTokens = computed(() => estimateTokens(inputText.value))

function handleSend() {
  const text = inputText.value.trim()
  if (!text || props.isStreaming) return
  emit('send', text)
  inputText.value = ''
}

// 暴露聚焦方法
function focus() {
  inputRef.value?.focus()
}

defineExpose({ focus })
</script>

<style lang="scss" scoped>
.chat-input-area {
  padding: 12px 24px 16px;
  background: var(--bg-primary);
  border-top: 1px solid var(--border-light);
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
</style>
