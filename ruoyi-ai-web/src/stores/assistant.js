import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { listAssistants } from '@/api/assistant'

export const useAssistantStore = defineStore('assistant', () => {
  const categories = ref([])
  const groupedAssistants = ref({})
  const selectedAssistantId = ref(null)
  const loading = ref(false)

  const selectedAssistant = computed(() => {
    if (!selectedAssistantId.value) return null
    for (const list of Object.values(groupedAssistants.value)) {
      const found = list.find(a => a.id === selectedAssistantId.value)
      if (found) return found
    }
    return null
  })

  const flatAssistants = computed(() => {
    const result = []
    for (const list of Object.values(groupedAssistants.value)) {
      result.push(...list)
    }
    return result
  })

  async function fetchAssistants() {
    loading.value = true
    try {
      const data = await listAssistants()
      categories.value = data.categories || []
      // 后端返回的JSON对象key为字符串，转换为数字键以匹配cat.id
      const normalized = {}
      const raw = data.assistants || {}
      for (const key of Object.keys(raw)) {
        normalized[Number(key)] = raw[key]
      }
      groupedAssistants.value = normalized
    } catch (e) {
      categories.value = []
      groupedAssistants.value = {}
    } finally {
      loading.value = false
    }
  }

  function selectAssistant(id) {
    selectedAssistantId.value = id
  }

  function clearSelection() {
    selectedAssistantId.value = null
  }

  return {
    categories,
    groupedAssistants,
    selectedAssistantId,
    selectedAssistant,
    flatAssistants,
    loading,
    fetchAssistants,
    selectAssistant,
    clearSelection
  }
})
