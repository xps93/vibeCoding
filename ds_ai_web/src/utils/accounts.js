/** 多账号管理 — localStorage 持久化，支持账号切换 */

const ACCOUNTS_KEY = 'ai_accounts'
const ACTIVE_INDEX_KEY = 'ai_active_account'

/** 获取所有已保存的账号 */
export function getAccounts() {
  try {
    const raw = localStorage.getItem(ACCOUNTS_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

/** 保存账号列表 */
function saveAccounts(list) {
  localStorage.setItem(ACCOUNTS_KEY, JSON.stringify(list))
}

/** 获取当前活跃账号索引 */
export function getActiveIndex() {
  try {
    const raw = localStorage.getItem(ACTIVE_INDEX_KEY)
    const idx = raw ? parseInt(raw, 10) : 0
    const accounts = getAccounts()
    return idx >= 0 && idx < accounts.length ? idx : 0
  } catch {
    return 0
  }
}

/** 保存活跃账号索引 */
function saveActiveIndex(idx) {
  localStorage.setItem(ACTIVE_INDEX_KEY, String(idx))
}

/** 获取当前活跃账号 */
export function getActiveAccount() {
  const accounts = getAccounts()
  const idx = getActiveIndex()
  return accounts.length > 0 && idx < accounts.length ? accounts[idx] : null
}

/** 获取当前活跃的令牌 */
export function getActiveToken() {
  const account = getActiveAccount()
  return account ? account.token : null
}

/**
 * 登录成功后添加/更新账号
 * @returns {Object} 更新后的账号对象
 */
export function addOrUpdateAccount(token, username, nickname, avatar) {
  const accounts = getAccounts()
  // 查找是否已存在相同 username 的账号（更新令牌）
  const existing = accounts.find(a => a.username === username)
  if (existing) {
    existing.token = token
    existing.nickname = nickname || username
    existing.avatar = avatar || ''
  } else {
    accounts.push({ token, username, nickname: nickname || username, avatar: avatar || '' })
  }
  saveAccounts(accounts)
  const idx = Math.max(0, accounts.findIndex(a => a.username === username))
  saveActiveIndex(idx)
  return accounts[idx]
}

/** 切换到指定账号（按索引） */
export function switchAccount(index) {
  const accounts = getAccounts()
  if (index < 0 || index >= accounts.length) return null
  saveActiveIndex(index)
  return accounts[index]
}

/** 移除指定账号（按索引） */
export function removeAccount(index) {
  const accounts = getAccounts()
  if (index < 0 || index >= accounts.length) return
  accounts.splice(index, 1)
  saveAccounts(accounts)
  // 调整活跃索引
  const activeIdx = getActiveIndex()
  if (activeIdx >= accounts.length) {
    saveActiveIndex(Math.max(0, accounts.length - 1))
  }
  return accounts.length > 0
}

/** 退出当前账号（移除活跃账号） */
export function logoutActiveAccount() {
  const accounts = getAccounts()
  const idx = getActiveIndex()
  if (idx < 0 || idx >= accounts.length) {
    clearAll()
    return false
  }
  accounts.splice(idx, 1)
  saveAccounts(accounts)
  if (accounts.length > 0) {
    saveActiveIndex(0)
    return true // 还有其他账号
  }
  clearAll()
  return false // 所有账号已清除
}

/** 清除所有账号数据 */
export function clearAll() {
  localStorage.removeItem(ACCOUNTS_KEY)
  localStorage.removeItem(ACTIVE_INDEX_KEY)
  // 兼容旧版单token存储
  localStorage.removeItem('token')
}

/** 迁移旧版单token到多账号格式 */
export function migrateLegacyToken() {
  const legacyToken = localStorage.getItem('token')
  if (!legacyToken) return false
  const accounts = getAccounts()
  if (accounts.length > 0) return false // 已有多账号，不覆盖

  // 创建默认账号（username 后续由 fetchUserInfo 填充）
  accounts.push({ token: legacyToken, username: '', nickname: '', avatar: '' })
  saveAccounts(accounts)
  saveActiveIndex(0)
  return true
}
