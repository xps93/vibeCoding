import { defineStore } from 'pinia'
import { ref } from 'vue'
import { logout as apiLogout } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const username = ref('')
  const nickname = ref('')
  const email = ref('')
  const phone = ref('')
  const avatar = ref('')
  const loading = ref(false)

  /** 获取用户信息 */
  async function fetchUserInfo() {
    const token = localStorage.getItem('token')
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
      }
    } catch (e) {
      // 获取失败保持空白
    } finally {
      loading.value = false
    }
  }

  /** 更新个人资料 */
  async function updateProfile(data) {
    const token = localStorage.getItem('token')
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
    // 更新本地状态
    if (data.nickname !== undefined) nickname.value = data.nickname
    if (data.email !== undefined) email.value = data.email
    if (data.phone !== undefined) phone.value = data.phone
    if (data.avatar !== undefined) avatar.value = data.avatar
    return json.data
  }

  /** 登出 */
  async function logout() {
    const token = localStorage.getItem('token')
    try { await apiLogout(token) } catch (e) { /* 忽略 */ }
    localStorage.removeItem('token')
    username.value = ''
    nickname.value = ''
    email.value = ''
    phone.value = ''
    avatar.value = ''
    router.push({ name: 'login' })
  }

  return {
    username, nickname, email, phone, avatar, loading,
    fetchUserInfo, updateProfile, logout
  }
})
