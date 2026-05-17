<template>
  <div class="chat-message" :class="[message.role]">
    <div class="message-avatar">
      <el-avatar v-if="message.role === 'user'" :size="32" icon="UserFilled" />
      <el-avatar v-else :size="32">
        <el-icon :size="20"><Cpu /></el-icon>
      </el-avatar>
    </div>
    <div class="message-body">
      <div class="message-role">
        {{ message.role === 'user' ? '我' : 'AI 助手' }}
        <span v-if="message.streaming" class="typing-dot" />
      </div>
      <div v-if="message.role === 'user'" class="message-text">
        {{ message.content }}
      </div>
      <MarkdownRenderer v-else :content="message.content" />
      <div v-if="message.streaming" class="typing-indicator">
        <span class="dot" />
        <span class="dot" />
        <span class="dot" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { Cpu } from '@element-plus/icons-vue'
import MarkdownRenderer from './MarkdownRenderer.vue'

defineProps({
  message: { type: Object, required: true }
})
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
      text-align: right;
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

  .message-text {
    font-size: var(--font-size-base);
    line-height: 1.7;
    white-space: pre-wrap;
    word-break: break-word;
    color: var(--text-primary);
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
