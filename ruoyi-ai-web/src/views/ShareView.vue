<template>
  <div class="share-view">
    <div class="share-header">
      <h2>{{ conversation?.title || $t('chat.title') }}</h2>
      <p class="share-meta">{{ $t('chat.sharePageDesc') }}</p>
    </div>
    <div class="share-messages" v-loading="loading">
      <div v-if="error" class="share-error">
        <el-empty :description="error" />
      </div>
      <div v-else-if="messages.length === 0 && !loading" class="share-empty">
        <el-empty :description="$t('chat.noMessages')" />
      </div>
      <div v-else class="message-list">
        <div
          v-for="(msg, i) in messages"
          :key="i"
          class="share-message"
          :class="msg.role"
        >
          <div class="msg-role">{{ msg.role === 'user' ? $t('chat.me') : $t('chat.aiAssistant') }}</div>
          <div class="msg-content">{{ msg.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { viewShare } from '@/api/chat'

const { t } = useI18n()
const route = useRoute()

const conversation = ref(null)
const messages = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  const token = route.params.shareToken
  if (!token) {
    error.value = t('chat.invalidShareLink')
    loading.value = false
    return
  }
  try {
    const res = await viewShare(token)
    // request.js 拦截器已解包 res.data，此处直接拿到 { conversation, messages }
    if (res && res.conversation) {
      conversation.value = res.conversation
      messages.value = res.messages || []
    } else {
      error.value = t('chat.invalidShareLink')
    }
  } catch (e) {
    error.value = t('chat.shareLoadFailed')
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.share-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px 16px;
  min-height: 100vh;
  background: var(--bg-primary);
}

.share-header {
  text-align: center;
  padding: 24px 0 32px;
  border-bottom: 1px solid var(--border-light);
  margin-bottom: 24px;

  h2 {
    font-size: 20px;
    color: var(--text-primary);
    margin: 0 0 8px;
  }

  .share-meta {
    font-size: 13px;
    color: var(--text-tertiary);
    margin: 0;
  }
}

.share-messages {
  padding: 0 16px;
}

.share-error, .share-empty {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.share-message {
  padding: 12px 0;
  border-bottom: 1px solid var(--border-light);

  .msg-role {
    font-size: 12px;
    font-weight: 600;
    color: var(--text-secondary);
    margin-bottom: 6px;
  }

  .msg-content {
    font-size: 14px;
    line-height: 1.7;
    white-space: pre-wrap;
    word-break: break-word;
    color: var(--text-primary);
  }

  &.assistant .msg-content {
    color: var(--text-secondary);
  }
}
</style>
