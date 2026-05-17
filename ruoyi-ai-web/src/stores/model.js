import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { listModels } from '@/api/model'

export const useModelStore = defineStore('model', () => {
  const items = ref([])
  const selectedId = ref(null)
  const loading = ref(false)

  const selectedModel = computed(() =>
    items.value.find(m => m.id === selectedId.value) || null
  )

  /** 将后端返回的能力标签字符串转为数组 */
  function normalizeCapabilities(item) {
    if (!item.capabilities) return []
    if (Array.isArray(item.capabilities)) return item.capabilities
    return item.capabilities.split(',').map(s => s.trim()).filter(Boolean)
  }

  async function fetchModels() {
    loading.value = true
    try {
      const data = await listModels()
      items.value = (data || []).map(item => ({
        ...item,
        capabilities: normalizeCapabilities(item)
      }))
      if (!selectedId.value && items.value.length > 0) {
        selectedId.value = items.value[0].id
      }
    } catch (e) {
      // 使用默认模型（ID与后端ai_model表种子数据一致）
      items.value = [
        { id: 1, modelKey: 'deepseek-chat', name: 'DeepSeek Chat', provider: 'DeepSeek', capabilities: ['chat', 'code', 'reasoning'] },
        { id: 2, modelKey: 'deepseek-reasoner', name: 'DeepSeek Reasoner', provider: 'DeepSeek', capabilities: ['chat', 'reasoning'] }
      ]
      if (!selectedId.value) selectedId.value = 1
    } finally {
      loading.value = false
    }
  }

  function selectModel(id) {
    selectedId.value = id
  }

  return { items, selectedId, loading, selectedModel, fetchModels, selectModel }
})
