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
import { ref, onMounted } from 'vue'
import { useConversationStore } from '@/stores/conversation'
import { useConfigStore } from '@/stores/config'
import { useModelStore } from '@/stores/model'
import { useKnowledgeStore } from '@/stores/knowledge'
import { useUserStore } from '@/stores/user'
import { useMobile } from '@/composables/useMobile'
import LeftSidebar from './LeftSidebar.vue'
import RightPanel from './RightPanel.vue'
import ChatView from '@/components/chat/ChatView.vue'

const conversationStore = useConversationStore()
const configStore = useConfigStore()
const modelStore = useModelStore()
const knowledgeStore = useKnowledgeStore()
const userStore = useUserStore()
const { isMobile, isTablet, leftDrawer, rightDrawer } = useMobile()

const leftDrawerOpen = ref(false)
const rightDrawerOpen = ref(false)

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) {
    Promise.all([
      conversationStore.fetchList(),
      configStore.fetchConfig(),
      modelStore.fetchModels(),
      knowledgeStore.fetchKnowledgeBases(),
      userStore.fetchUserInfo()
    ])
  } else {
    // 未登录时加载默认模型列表
    modelStore.fetchModels()
    knowledgeStore.fetchKnowledgeBases()
  }
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
