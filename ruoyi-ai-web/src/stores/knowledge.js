import { defineStore } from 'pinia'
import { ref } from 'vue'
import { listKnowledgeBases } from '@/api/knowledge'

export const useKnowledgeStore = defineStore('knowledge', () => {
  const items = ref([])
  const selectedIds = ref([])
  const loading = ref(false)

  async function fetchKnowledgeBases() {
    loading.value = true
    try {
      const data = await listKnowledgeBases()
      items.value = data || []
    } catch (e) {
      items.value = []
    } finally {
      loading.value = false
    }
  }

  function toggle(id) {
    const idx = selectedIds.value.indexOf(id)
    if (idx === -1) {
      selectedIds.value.push(id)
    } else {
      selectedIds.value.splice(idx, 1)
    }
  }

  function selectAll() {
    selectedIds.value = items.value.map(i => i.id)
  }

  function clearSelection() {
    selectedIds.value = []
  }

  return { items, selectedIds, loading, fetchKnowledgeBases, toggle, selectAll, clearSelection }
})
