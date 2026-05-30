import { defineStore } from 'pinia'
import { ref } from 'vue'
import { listDatasets, uploadDataset, deleteDataset } from '@/api/dataset'
import { ElMessage } from 'element-plus'

export const useDatasetStore = defineStore('dataset', () => {
  const items = ref([])
  const loading = ref(false)
  const datasetMode = ref(false)       // 是否启用数据集查询模式
  const selectedDatasetId = ref(null)  // 当前选中的数据集ID

  async function fetchDatasets() {
    loading.value = true
    try {
      const data = await listDatasets()
      items.value = data || []
    } catch (e) {
      items.value = []
    } finally {
      loading.value = false
    }
  }

  async function upload(file) {
    const data = await uploadDataset(file)
    if (data && data.id) {
      ElMessage.success(`数据集"${data.name}"上传成功，共${data.rowCount}行`)
      await fetchDatasets()
      return data
    }
    return null
  }

  async function remove(id) {
    await deleteDataset(id)
    ElMessage.success('数据集已删除')
    if (selectedDatasetId.value === id) {
      selectedDatasetId.value = null
    }
    await fetchDatasets()
  }

  function selectDataset(id) {
    if (selectedDatasetId.value === id) {
      selectedDatasetId.value = null
    } else {
      selectedDatasetId.value = id
    }
  }

  function toggleMode() {
    datasetMode.value = !datasetMode.value
  }

  function clearSelection() {
    selectedDatasetId.value = null
    datasetMode.value = false
  }

  return {
    items, loading, datasetMode, selectedDatasetId,
    fetchDatasets, upload, remove, selectDataset, toggleMode, clearSelection
  }
})
