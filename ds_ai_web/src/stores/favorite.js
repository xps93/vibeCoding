import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  addFavorite, removeFavorite, listFavorites, checkFavorites, moveToGroup,
  listGroups, createGroup, updateGroup, deleteGroup
} from '@/api/favorite'
import { ElMessage } from 'element-plus'

export const useFavoriteStore = defineStore('favorite', () => {
  const favoritedIds = ref(new Set())
  const items = ref([])
  const loading = ref(false)

  // ──────────────── 分组 ────────────────
  const groups = ref([])
  const activeGroupId = ref(null)   // null = 全部分组
  const searchKeyword = ref('')

  function isFavorited(conversationId) {
    return favoritedIds.value.has(conversationId)
  }

  /** 获取收藏列表（支持分组和关键词） */
  async function fetchList() {
    loading.value = true
    try {
      const params = {}
      if (activeGroupId.value) params.groupId = activeGroupId.value
      if (searchKeyword.value) params.keyword = searchKeyword.value
      const data = await listFavorites(params)
      items.value = data || []
      favoritedIds.value = new Set(items.value.map(f => f.conversationId))
    } catch (e) {
      items.value = []
      favoritedIds.value = new Set()
    } finally {
      loading.value = false
    }
  }

  /** 批量检查收藏状态 */
  async function check(conversationIds) {
    if (!conversationIds || conversationIds.length === 0) return
    try {
      const data = await checkFavorites(conversationIds)
      if (data) {
        Object.entries(data).forEach(([id, val]) => {
          if (val) favoritedIds.value.add(Number(id))
          else favoritedIds.value.delete(Number(id))
        })
      }
    } catch (e) { /* 静默 */ }
  }

  /** 切换收藏状态 */
  async function toggle(conversationId) {
    const fav = isFavorited(conversationId)
    try {
      if (fav) {
        await removeFavorite(conversationId)
        favoritedIds.value.delete(conversationId)
        items.value = items.value.filter(f => f.conversationId !== conversationId)
        ElMessage.success('已取消收藏')
      } else {
        await addFavorite(conversationId)
        favoritedIds.value.add(conversationId)
        await fetchList()
        ElMessage.success('已收藏')
      }
    } catch (e) {
      ElMessage.error(fav ? '取消收藏失败' : '收藏失败')
    }
  }

  // ──────────────── 分组 CRUD ────────────────

  async function fetchGroups() {
    try {
      const data = await listGroups()
      groups.value = data || []
    } catch (e) {
      groups.value = []
    }
  }

  async function addGroup(name) {
    try {
      await createGroup(name)
      await fetchGroups()
      ElMessage.success('分组已创建')
    } catch (e) {
      ElMessage.error('创建分组失败')
    }
  }

  async function renameGroup(id, name) {
    try {
      await updateGroup(id, name)
      await fetchGroups()
      ElMessage.success('已重命名')
    } catch (e) {
      ElMessage.error('重命名失败')
    }
  }

  async function removeGroup(id) {
    try {
      await deleteGroup(id)
      if (activeGroupId.value === id) activeGroupId.value = null
      await fetchGroups()
      await fetchList()
      ElMessage.success('分组已删除')
    } catch (e) {
      ElMessage.error('删除分组失败')
    }
  }

  /** 移动收藏到分组 */
  async function moveFavoriteToGroup(conversationId, groupId) {
    try {
      await moveToGroup(conversationId, groupId)
      // 更新本地状态
      const item = items.value.find(f => f.conversationId === conversationId)
      if (item) item.groupId = groupId
      ElMessage.success('已移动')
    } catch (e) {
      ElMessage.error('移动失败')
    }
  }

  function clear() {
    items.value = []
    favoritedIds.value = new Set()
    groups.value = []
    activeGroupId.value = null
    searchKeyword.value = ''
  }

  return {
    favoritedIds, items, loading, groups, activeGroupId, searchKeyword,
    isFavorited, fetchList, check, toggle,
    fetchGroups, addGroup, renameGroup, removeGroup,
    moveFavoriteToGroup, clear
  }
})
