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
        <el-popover placement="top" :width="220" trigger="click" @show="refreshAccountList">
          <template #reference>
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.avatar">
                <el-icon :size="20"><UserFilled /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userStore.nickname || userStore.username || $t('common.notLoggedIn') }}</span>
              <el-icon v-if="accountList.length > 1" class="switch-hint"><ArrowDown /></el-icon>
            </div>
          </template>
          <!-- 账号切换面板 -->
          <div class="account-panel">
            <div class="panel-title">{{ $t('account.switchAccount') }}</div>
            <div
              v-for="(acc, i) in accountList"
              :key="i"
              class="account-item"
              :class="{ active: i === activeIdx }"
              @click="handleSwitch(i)"
            >
              <el-avatar :size="28" :src="acc.avatar">
                <el-icon :size="16"><UserFilled /></el-icon>
              </el-avatar>
              <span class="account-name">{{ acc.nickname || acc.username || $t('common.unknownAccount') }}</span>
              <el-icon v-if="i === activeIdx" class="check-icon" color="var(--accent-color)"><Check /></el-icon>
              <button class="remove-btn" @click.stop="handleRemoveAccount(i)" :title="$t('common.removeAccount')">
                <el-icon :size="12"><Close /></el-icon>
              </button>
            </div>
            <el-divider style="margin:8px 0" />
            <div class="account-item add-account" @click="goLogin">
              <el-icon :size="16"><Plus /></el-icon>
              <span>{{ $t('account.addAccount') }}</span>
            </div>
          </div>
        </el-popover>
        <el-button :icon="SwitchButton" text size="small" @click="handleLogout" :title="$t('account.logout')" />
      </div>
    </div>
  </el-drawer>
  <aside v-else class="left-sidebar">
    <div class="sidebar-content">
      <ConversationList />
      <div class="user-footer">
        <el-popover placement="top" :width="220" trigger="click" @show="refreshAccountList">
          <template #reference>
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.avatar">
                <el-icon :size="20"><UserFilled /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userStore.nickname || userStore.username || $t('common.notLoggedIn') }}</span>
              <el-icon v-if="accountList.length > 1" class="switch-hint"><ArrowDown /></el-icon>
            </div>
          </template>
          <!-- 账号切换面板 -->
          <div class="account-panel">
            <div class="panel-title">{{ $t('account.switchAccount') }}</div>
            <div
              v-for="(acc, i) in accountList"
              :key="i"
              class="account-item"
              :class="{ active: i === activeIdx }"
              @click="handleSwitch(i)"
            >
              <el-avatar :size="28" :src="acc.avatar">
                <el-icon :size="16"><UserFilled /></el-icon>
              </el-avatar>
              <span class="account-name">{{ acc.nickname || acc.username || $t('common.unknownAccount') }}</span>
              <el-icon v-if="i === activeIdx" class="check-icon" color="var(--accent-color)"><Check /></el-icon>
              <button class="remove-btn" @click.stop="handleRemoveAccount(i)" :title="$t('common.removeAccount')">
                <el-icon :size="12"><Close /></el-icon>
              </button>
            </div>
            <el-divider style="margin:8px 0" />
            <div class="account-item add-account" @click="goLogin">
              <el-icon :size="16"><Plus /></el-icon>
              <span>{{ $t('account.addAccount') }}</span>
            </div>
          </div>
        </el-popover>
        <el-button :icon="SwitchButton" text size="small" @click="handleLogout" :title="$t('account.logout')" />
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { UserFilled, SwitchButton, ArrowDown, Check, Plus, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getAccounts, getActiveIndex, removeAccount } from '@/utils/accounts'
import ConversationList from '@/components/conversation/ConversationList.vue'

const { t } = useI18n()

const router = useRouter()

const props = defineProps({
  visible: { type: Boolean, default: false },
  leftDrawer: { type: Boolean, default: false }
})

const emit = defineEmits(['close'])
const userStore = useUserStore()

const drawerVisible = ref(false)
const accountList = ref([])
const activeIdx = ref(0)

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

function refreshAccountList() {
  accountList.value = getAccounts()
  activeIdx.value = getActiveIndex()
}

function handleSwitch(index) {
  if (index === activeIdx.value) return
  userStore.switchToAccount(index)
  activeIdx.value = index
  ElMessage.success(t('common.accountSwitched'))
}

function handleRemoveAccount(index) {
  if (accountList.value.length <= 1) {
    ElMessage.warning(t('common.keepOneAccount'))
    return
  }
  const remaining = removeAccount(index)
  refreshAccountList()
  if (remaining) {
    // 如果移除的是当前账号，自动切换到第一个
    if (index === activeIdx.value || activeIdx.value >= accountList.value.length) {
      userStore.switchToAccount(0)
      activeIdx.value = 0
    }
  }
  ElMessage.success(t('common.accountRemoved'))
}

function goLogin() {
  router.push({ name: 'login', query: { redirect: '/' } })
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

.switch-hint {
  color: var(--text-tertiary);
  font-size: 12px;
  flex-shrink: 0;
}

/* 账号切换面板 */
.account-panel {
  .panel-title {
    font-size: 13px;
    color: var(--text-tertiary);
    margin-bottom: 6px;
    padding: 0 2px;
  }
}

.account-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;

  &:hover {
    background: var(--bg-hover);
  }

  &.active {
    background: var(--accent-light);
  }

  .account-name {
    flex: 1;
    font-size: 13px;
    color: var(--text-primary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .check-icon {
    flex-shrink: 0;
  }

  .remove-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: none;
    background: transparent;
    color: var(--text-tertiary);
    cursor: pointer;
    flex-shrink: 0;

    &:hover {
      background: var(--danger-color);
      color: #fff;
    }
  }

  &.add-account {
    color: var(--accent-color);
    font-size: 13px;
  }
}
</style>
