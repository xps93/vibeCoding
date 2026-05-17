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
        <el-button type="primary" size="small" @click="retryLast">重试</el-button>
      </div>
      <EmptyState
        v-if="!chatStore.hasMessages && !chatStore.isStreaming"
        @send="handleSend"
      />
      <div v-else class="message-list">
        <ChatMessage
          v-for="(msg, i) in chatStore.allMessages"
          :key="i"
          :message="msg"
        />
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
import { storeToRefs } from 'pinia'
import { useChatStore } from '@/stores/chat'
import { useConversationStore } from '@/stores/conversation'
import { useSseChat } from '@/composables/useSseChat'
import { useAutoScroll } from '@/composables/useAutoScroll'
import { useMobile } from '@/composables/useMobile'
import HeaderToolbar from '@/components/common/HeaderToolbar.vue'
import EmptyState from './EmptyState.vue'
import ChatMessage from './ChatMessage.vue'
import ChatInput from './ChatInput.vue'

defineEmits(['toggle-left', 'toggle-right'])

const chatStore = useChatStore()
const conversationStore = useConversationStore()
const { error: chatError } = storeToRefs(chatStore)
const { showLeftSidebar, showRightPanel } = useMobile()
const { send, stop } = useSseChat()

const messageContainer = ref(null)
const inputRef = ref(null)
const lastUserMessage = ref('')

const { onScroll } = useAutoScroll(messageContainer, computed(() => chatStore.allMessages.length))

async function handleSend(content) {
  lastUserMessage.value = content
  await send(content)
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
</style>
