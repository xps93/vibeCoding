<template>
  <el-dropdown trigger="click" @command="localeStore.setLocale">
    <el-button circle text size="small" title="切换语言">
      <span class="lang-flag">{{ currentFlag }}</span>
    </el-button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="lang in localeStore.languages"
          :key="lang.value"
          :command="lang.value"
          :class="{ 'is-active': localeStore.current === lang.value }"
        >
          <span>{{ lang.label }}</span>
          <el-icon v-if="localeStore.current === lang.value" :size="14" style="margin-left:8px;color:var(--accent-color)">
            <Check />
          </el-icon>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { Check } from '@element-plus/icons-vue'
import { useLocaleStore } from '@/stores/locale'

const localeStore = useLocaleStore()

const flagMap = { 'zh-CN': '中', en: 'EN', ja: '日' }
const currentFlag = computed(() => flagMap[localeStore.current] || '中')
</script>
