<template>
  <el-button
    size="small"
    :icon="copied ? Check : CopyDocument"
    :type="copied ? 'success' : 'default'"
    text
    class="code-copy-btn"
    @click="handleCopy"
  >
    {{ copied ? '已复制' : '复制' }}
  </el-button>
</template>

<script setup>
import { ref } from 'vue'
import { CopyDocument, Check } from '@element-plus/icons-vue'

const props = defineProps({
  code: { type: String, required: true }
})

const copied = ref(false)

async function handleCopy() {
  try {
    await navigator.clipboard.writeText(props.code)
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  } catch (e) {
    // 降级方案
    const textarea = document.createElement('textarea')
    textarea.value = props.code
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  }
}
</script>

<style lang="scss" scoped>
.code-copy-btn {
  position: absolute;
  top: 6px;
  right: 8px;
  font-size: var(--font-size-xs);
}
</style>
