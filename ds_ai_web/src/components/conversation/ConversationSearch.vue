<template>
  <div class="conversation-search">
    <el-input
      v-model="query"
      :placeholder="$t('conversation.searchPlaceholder')"
      :prefix-icon="Search"
      clearable
      size="small"
      @input="onSearch"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useConversationStore } from '@/stores/conversation'

const conversationStore = useConversationStore()
const query = ref('')

function onSearch(val) {
  conversationStore.setSearchQuery(val)
}
</script>

<style lang="scss" scoped>
.conversation-search {
  padding: 8px 12px;
}
</style>
