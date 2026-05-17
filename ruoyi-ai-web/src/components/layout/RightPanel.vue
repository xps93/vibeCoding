<template>
  <el-drawer
    v-if="rightDrawer"
    v-model="drawerVisible"
    direction="rtl"
    :size="320"
    :with-header="false"
    :before-close="emitClose"
  >
    <div class="panel-content">
      <ModelSelector />
      <TemperatureSlider />
      <MaxTokensSlider />
      <KnowledgeSelector />
      <SystemPromptEditor />
    </div>
  </el-drawer>
  <aside v-else class="right-panel">
    <div class="panel-header">对话设置</div>
    <div class="panel-content">
      <ModelSelector />
      <TemperatureSlider />
      <MaxTokensSlider />
      <KnowledgeSelector />
      <SystemPromptEditor />
    </div>
  </aside>
</template>

<script setup>
import { ref, watch } from 'vue'
import ModelSelector from '@/components/settings/ModelSelector.vue'
import TemperatureSlider from '@/components/settings/TemperatureSlider.vue'
import MaxTokensSlider from '@/components/settings/MaxTokensSlider.vue'
import KnowledgeSelector from '@/components/settings/KnowledgeSelector.vue'
import SystemPromptEditor from '@/components/settings/SystemPromptEditor.vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  rightDrawer: { type: Boolean, default: false }
})

const emit = defineEmits(['close'])

const drawerVisible = ref(false)

watch(() => props.visible, (val) => {
  drawerVisible.value = val
})

function emitClose() {
  emit('close')
}
</script>

<style lang="scss" scoped>
.right-panel {
  width: var(--right-panel-width);
  height: 100%;
  border-left: 1px solid var(--border-color);
  background: var(--bg-sidebar);
  flex-shrink: 0;
  overflow-y: auto;
}

.panel-header {
  padding: 16px;
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-light);
}

.panel-content {
  padding: 16px;
}
</style>
