import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  listConversations,
  createConversation,
  updateConversation,
  deleteConversation,
  batchDeleteConversations
} from '@/api/conversation'
import { listMessages } from '@/api/message'
import { useChatStore } from './chat'
import { ElMessage } from 'element-plus'

export const useConversationStore = defineStore('conversation', () => {
  const items = ref([])
  const activeId = ref(null)
  const searchQuery = ref('')
  const loading = ref(false)
  const selectMode = ref(false)
  const selectedIds = ref([])

  const filteredItems = computed(() => {
    if (!searchQuery.value) return items.value
    const q = searchQuery.value.toLowerCase()
    return items.value.filter(c =>
      c.title && c.title.toLowerCase().includes(q)
    )
  })

  const activeConversation = computed(() =>
    items.value.find(c => c.id === activeId.value) || null
  )

  const allSelected = computed(() =>
    filteredItems.value.length > 0 && filteredItems.value.every(c => selectedIds.value.includes(c.id))
  )

  async function fetchList() {
    loading.value = true
    try {
      const data = await listConversations()
      items.value = data || []
    } catch (e) {
      items.value = []
    } finally {
      loading.value = false
    }
  }

  async function create(title) {
    try {
      const data = await createConversation({ title: title || '新对话' })
      if (data) {
        items.value.unshift(data)
        activeId.value = data.id
        const chatStore = useChatStore()
        chatStore.clearMessages()
      }
    } catch (e) {
      ElMessage.error('创建对话失败')
    }
  }

  async function rename(id, title) {
    try {
      await updateConversation({ id, title })
      const item = items.value.find(c => c.id === id)
      if (item) item.title = title
    } catch (e) {
      ElMessage.error('重命名失败')
    }
  }

  async function remove(id) {
    try {
      await deleteConversation(id)
      items.value = items.value.filter(c => c.id !== id)
      selectedIds.value = selectedIds.value.filter(sid => sid !== id)
      if (activeId.value === id) {
        activeId.value = null
        const chatStore = useChatStore()
        chatStore.clearMessages()
      }
    } catch (e) {
      ElMessage.error('删除对话失败')
    }
  }

  /** 批量删除 */
  async function batchRemove() {
    if (selectedIds.value.length === 0) return
    try {
      const ids = [...selectedIds.value]
      await batchDeleteConversations(ids)
      items.value = items.value.filter(c => !ids.includes(c.id))
      if (activeId.value && ids.includes(activeId.value)) {
        activeId.value = null
        const chatStore = useChatStore()
        chatStore.clearMessages()
      }
      selectedIds.value = []
      selectMode.value = false
      ElMessage.success(`已删除 ${ids.length} 个对话`)
    } catch (e) {
      ElMessage.error('批量删除失败')
    }
  }

  function toggleSelect(id) {
    const idx = selectedIds.value.indexOf(id)
    if (idx === -1) {
      selectedIds.value.push(id)
    } else {
      selectedIds.value.splice(idx, 1)
    }
  }

  function toggleSelectAll() {
    if (allSelected.value) {
      selectedIds.value = []
    } else {
      selectedIds.value = filteredItems.value.map(c => c.id)
    }
  }

  function enterSelectMode() {
    selectMode.value = true
    selectedIds.value = []
  }

  function exitSelectMode() {
    selectMode.value = false
    selectedIds.value = []
  }

  async function setActive(id) {
    if (selectMode.value) {
      toggleSelect(id)
      return
    }
    if (activeId.value === id) return
    const chatStore = useChatStore()
    if (chatStore.isStreaming) {
      chatStore.stopGeneration()
    }

    activeId.value = id
    chatStore.clearMessages()

    if (id) {
      try {
        const msgs = await listMessages(id)
        chatStore.setMessages(msgs || [])
      } catch (e) {
        chatStore.setMessages([])
      }
    }
  }

  function setSearchQuery(query) {
    searchQuery.value = query
  }

  return {
    items, activeId, searchQuery, loading,
    selectMode, selectedIds, allSelected,
    filteredItems, activeConversation,
    fetchList, create, rename, remove, batchRemove,
    toggleSelect, toggleSelectAll, enterSelectMode, exitSelectMode,
    setActive, setSearchQuery
  }
})
