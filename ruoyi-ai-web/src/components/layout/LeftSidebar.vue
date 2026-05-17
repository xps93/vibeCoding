<template>
  <el-drawer
    v-if="leftDrawer"
    v-model="drawerVisible"
    direction="ltr"
    :size="280"
    :with-header="false"
    :before-close="emitClose"
  >
    <div class="sidebar-content">
      <ConversationList />
      <div class="user-footer">
        <div class="user-info" @click="goProfile">
          <el-avatar :size="36" :src="userStore.avatar">
            <el-icon :size="20"><UserFilled /></el-icon>
          </el-avatar>
          <span class="user-name">{{ userStore.nickname || userStore.username || '未登录' }}</span>
        </div>
        <el-button :icon="SwitchButton" text size="small" @click="handleLogout" title="退出登录" />
      </div>
    </div>
  </el-drawer>
  <aside v-else class="left-sidebar">
    <div class="sidebar-content">
      <ConversationList />
      <div class="user-footer">
        <div class="user-info" @click="goProfile">
          <el-avatar :size="36" :src="userStore.avatar">
            <el-icon :size="20"><UserFilled /></el-icon>
          </el-avatar>
          <span class="user-name">{{ userStore.nickname || userStore.username || '未登录' }}</span>
        </div>
        <el-button :icon="SwitchButton" text size="small" @click="handleLogout" title="退出登录" />
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import ConversationList from '@/components/conversation/ConversationList.vue'

const router = useRouter()

const props = defineProps({
  visible: { type: Boolean, default: false },
  leftDrawer: { type: Boolean, default: false }
})

const emit = defineEmits(['close'])
const userStore = useUserStore()

const drawerVisible = ref(false)

watch(() => props.visible, (val) => {
  drawerVisible.value = val
})

function emitClose() {
  emit('close')
}

function goProfile() {
  if (userStore.username) {
    router.push({ name: 'profile' })
  }
}

async function handleLogout() {
  await userStore.logout()
}
</script>

<style lang="scss" scoped>
.left-sidebar {
  width: var(--sidebar-width);
  height: 100%;
  border-right: 1px solid var(--border-color);
  background: var(--bg-sidebar);
  flex-shrink: 0;
  overflow: hidden;
}

.sidebar-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.sidebar-content > :deep(.conversation-list) {
  flex: 1;
  overflow: hidden;
}

.user-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid var(--border-light);
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex: 1;
  cursor: pointer;
  border-radius: var(--radius-sm);
  padding: 4px 8px;
  margin: -4px -8px;
  transition: background 0.15s;

  &:hover {
    background: var(--bg-hover);
  }
}

.user-name {
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
