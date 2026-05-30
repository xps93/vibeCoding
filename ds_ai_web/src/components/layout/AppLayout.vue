<template>
  <div class="app-layout" :class="{ 'is-mobile': isMobile, 'is-tablet': isTablet }">
    <LeftSidebar
      :visible="leftDrawerOpen"
      :left-drawer="leftDrawer"
      @close="leftDrawerOpen = false"
    />
    <main class="main-content">
      <ChatView
        @toggle-left="leftDrawerOpen = !leftDrawerOpen"
        @toggle-right="rightDrawerOpen = !rightDrawerOpen"
      />
    </main>
    <RightPanel
      :visible="rightDrawerOpen"
      :right-drawer="rightDrawer"
      @close="rightDrawerOpen = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useConversationStore } from '@/stores/conversation'
import { useChatStore } from '@/stores/chat'
import { useConfigStore } from '@/stores/config'
import { useModelStore } from '@/stores/model'
import { useKnowledgeStore } from '@/stores/knowledge'
import { useAssistantStore } from '@/stores/assistant'
import { useFavoriteStore } from '@/stores/favorite'
import { useUserStore } from '@/stores/user'
import { useMobile } from '@/composables/useMobile'
import { getActiveToken, migrateLegacyToken } from '@/utils/accounts'
import LeftSidebar from './LeftSidebar.vue'
import RightPanel from './RightPanel.vue'
import ChatView from '@/components/chat/ChatView.vue'

const conversationStore = useConversationStore()
const chatStore = useChatStore()
const configStore = useConfigStore()
const favoriteStore = useFavoriteStore()
const modelStore = useModelStore()
const knowledgeStore = useKnowledgeStore()
const assistantStore = useAssistantStore()
const userStore = useUserStore()
const { isMobile, isTablet, leftDrawer, rightDrawer } = useMobile()

const leftDrawerOpen = ref(false)
const rightDrawerOpen = ref(false)
let unregisterSwitch = null

/** 加载所有需要认证的数据 */
async function loadAuthedData() {
  await Promise.all([
    conversationStore.fetchList(),
    favoriteStore.fetchList(),
    configStore.fetchConfig(),
    userStore.fetchUserInfo()
  ])
}

/** 加载公共数据（无需认证） */
function loadPublicData() {
  modelStore.fetchModels()
  knowledgeStore.fetchKnowledgeBases()
  assistantStore.fetchAssistants()
}

onMounted(() => {
  // 迁移旧版单token到多账号格式
  migrateLegacyToken()

  const token = getActiveToken()
  if (token) {
    loadAuthedData()
  }
  loadPublicData()

  // 注册账号切换事件
  unregisterSwitch = userStore.onAccountSwitch(() => {
    conversationStore.clear()
    chatStore.stopGeneration()
    chatStore.clearMessages()
    favoriteStore.clear()
    loadAuthedData()
  })
})

onUnmounted(() => {
  if (unregisterSwitch) unregisterSwitch()
})
</script>

<style lang="scss" scoped>
.app-layout {
  display: grid;
  grid-template-columns: var(--sidebar-width) 1fr var(--right-panel-width);
  height: 100%;
  overflow: hidden;

  &.is-tablet {
    grid-template-columns: var(--sidebar-width) 1fr;
  }

  &.is-mobile {
    grid-template-columns: 1fr;
  }
}

.main-content {
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: var(--bg-chat);
}
</style>
