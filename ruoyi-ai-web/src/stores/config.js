import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getConfig, saveConfig } from '@/api/config'
import {
  DEFAULT_TEMPERATURE,
  DEFAULT_MAX_TOKENS,
  DEFAULT_SYSTEM_PROMPT
} from '@/utils/constants'

export const useConfigStore = defineStore('config', () => {
  const temperature = ref(DEFAULT_TEMPERATURE)
  const maxTokens = ref(DEFAULT_MAX_TOKENS)
  const systemPrompt = ref(DEFAULT_SYSTEM_PROMPT)
  const loading = ref(false)
  let saveTimer = null

  async function fetchConfig() {
    try {
      const data = await getConfig()
      if (data) {
        temperature.value = data.temperature ?? DEFAULT_TEMPERATURE
        maxTokens.value = data.maxTokens ?? DEFAULT_MAX_TOKENS
        systemPrompt.value = data.systemPrompt || DEFAULT_SYSTEM_PROMPT
      }
    } catch (e) {
      // 使用默认值
    }
  }

  async function save() {
    try {
      await saveConfig({
        temperature: temperature.value,
        maxTokens: maxTokens.value,
        systemPrompt: systemPrompt.value
      })
    } catch (e) {
      // 静默失败
    }
  }

  /** 延迟保存（防抖800ms），避免频繁请求 */
  function debouncedSave() {
    if (saveTimer) clearTimeout(saveTimer)
    saveTimer = setTimeout(() => { save(); saveTimer = null }, 800)
  }

  function updateTemperature(val) {
    temperature.value = val
    debouncedSave()
  }

  function updateMaxTokens(val) {
    maxTokens.value = val
    debouncedSave()
  }

  function updateSystemPrompt(val) {
    systemPrompt.value = val
    debouncedSave()
  }

  return {
    temperature, maxTokens, systemPrompt, loading,
    fetchConfig, save,
    updateTemperature, updateMaxTokens, updateSystemPrompt
  }
})
