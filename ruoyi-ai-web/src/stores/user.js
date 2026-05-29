import { defineStore } from 'pinia'
import { ref } from 'vue'
import { logout as apiLogout } from '@/api/auth'
import router from '@/router'
import {
  getActiveToken, getActiveAccount, getAccounts, getActiveIndex,
  addOrUpdateAccount, switchAccount, logoutActiveAccount, migrateLegacyToken
} from '@/utils/accounts'

export const useUserStore = defineStore('user', () => {
  const username = ref('')
  const nickname = ref('')
  const email = ref('')
  const phone = ref('')
  const avatar = ref('')
  const loading = ref(false)

  // 账号切换事件总线（用于通知AppLayout重新加载数据）
  const accountSwitchCallbacks = []

  /** 注册账号切换回调 */
  function onAccountSwitch(callback) {
    accountSwitchCallbacks.push(callback)
    return () => {
      const idx = accountSwitchCallbacks.indexOf(callback)
      if (idx >= 0) accountSwitchCallbacks.splice(idx, 1)
    }
  }

  /** 触发账号切换 */
  function notifyAccountSwitch() {
    accountSwitchCallbacks.forEach(cb => cb())
  }

  /** 获取用户信息（使用活跃令牌） */
  async function fetchUserInfo() {
    const token = getActiveToken()
    if (!token) return
    loading.value = true
    try {
      const res = await fetch('/api/user/info', {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const json = await res.json()
      if (json.code === 200 && json.data) {
        const user = json.data.user
        username.value = user.username || ''
        nickname.value = user.nickname || user.username || ''
        email.value = user.email || ''
        phone.value = user.phone || ''
        avatar.value = user.avatar || ''
        // 同步更新账号列表中的信息
        const idx = getActiveIndex()
        const accounts = getAccounts()
        if (idx >= 0 && idx < accounts.length) {
          accounts[idx].nickname = nickname.value
          accounts[idx].avatar = avatar.value
          accounts[idx].username = username.value
          addOrUpdateAccount(token, username.value, nickname.value, avatar.value)
        }
      }
    } catch (e) {
      // 获取失败保持空白
    } finally {
      loading.value = false
    }
  }

  /** 更新个人资料 */
  async function updateProfile(data) {
    const token = getActiveToken()
    if (!token) throw new Error('未登录')
    const res = await fetch('/api/user/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(data)
    })
    const json = await res.json()
    if (json.code !== 200) {
      throw new Error(json.msg || '保存失败')
    }
    if (data.nickname !== undefined) nickname.value = data.nickname
    if (data.email !== undefined) email.value = data.email
    if (data.phone !== undefined) phone.value = data.phone
    if (data.avatar !== undefined) avatar.value = data.avatar
    return json.data
  }

  /** 登录后记录账号 */
  function onLogin(token, user, nick, ava) {
    addOrUpdateAccount(token, user, nick || user, ava || '')
    username.value = user
    nickname.value = nick || user
    avatar.value = ava || ''
  }

  /** 切换到指定账号 */
  function switchToAccount(index) {
    const account = switchAccount(index)
    if (account) {
      username.value = account.username || ''
      nickname.value = account.nickname || ''
      avatar.value = account.avatar || ''
      email.value = ''
      phone.value = ''
      notifyAccountSwitch()
    }
  }

  /** 登出当前账号 */
  async function logout() {
    const token = getActiveToken()
    try { await apiLogout(token) } catch (e) { /* 忽略 */ }
    const hasOthers = logoutActiveAccount()
    username.value = ''
    nickname.value = ''
    email.value = ''
    phone.value = ''
    avatar.value = ''
    if (hasOthers) {
      // 自动切换到下一个账号
      const account = getActiveAccount()
      if (account) {
        username.value = account.username || ''
        nickname.value = account.nickname || ''
        avatar.value = account.avatar || ''
        notifyAccountSwitch()
        return
      }
    }
    router.push({ name: 'login' })
  }

  /** 获取账号列表（用于UI展示） */
  function getAccountList() {
    return getAccounts()
  }

  return {
    username, nickname, email, phone, avatar, loading,
    fetchUserInfo, updateProfile, logout, onLogin, onAccountSwitch,
    switchToAccount, getAccountList, getActiveIndex
  }
})
