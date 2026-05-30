<template>
  <div class="empty-state">
    <div class="welcome-content">
      <div class="welcome-logo">
        <el-icon :size="48"><ChatLineRound /></el-icon>
      </div>
      <h1 class="welcome-title">Ds-Ai</h1>
      <p class="welcome-desc">{{ $t('chat.emptyDesc') }}</p>
      <div class="welcome-suggestions">
        <div
          v-for="(s, i) in suggestions"
          :key="i"
          class="suggestion-item"
          @click="$emit('send', s)"
        >
          <el-icon><Promotion /></el-icon>
          <span>{{ s }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ChatLineRound, Promotion } from '@element-plus/icons-vue'
import { WELCOME_SUGGESTIONS } from '@/utils/constants'

defineEmits(['send'])

const suggestions = WELCOME_SUGGESTIONS
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.welcome-content {
  text-align: center;
  max-width: 520px;
  padding: 40px;

  .welcome-logo {
    color: var(--accent-color);
    margin-bottom: 16px;
  }

  .welcome-title {
    font-size: 28px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 8px;
  }

  .welcome-desc {
    font-size: var(--font-size-base);
    color: var(--text-tertiary);
    margin-bottom: 32px;
  }

  .welcome-suggestions {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;

    .suggestion-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 16px;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      cursor: pointer;
      color: var(--text-secondary);
      font-size: var(--font-size-sm);
      transition: all 0.2s;
      text-align: left;

      &:hover {
        border-color: var(--accent-color);
        color: var(--accent-color);
        background: var(--accent-light);
      }
    }
  }
}

@media (max-width: 767px) {
  .welcome-content .welcome-suggestions {
    grid-template-columns: 1fr;
  }
}
</style>
