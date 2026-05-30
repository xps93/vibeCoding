<template>
  <div ref="renderRef" class="markdown-body" v-html="renderedHtml" />
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { useMarkdown } from '@/composables/useMarkdown'

const props = defineProps({
  content: { type: String, default: '' }
})

const { render } = useMarkdown()
const renderRef = ref(null)

const renderedHtml = computed(() => render(props.content))

// 渲染完成后给代码块添加复制按钮
watch(renderedHtml, async () => {
  await nextTick()
  if (renderRef.value) {
    const codeBlocks = renderRef.value.querySelectorAll('pre')
    codeBlocks.forEach(pre => {
      if (pre.querySelector('.code-copy-injected')) return
      const code = pre.querySelector('code')
      if (!code) return

      const lang = code.className.replace('hljs', '').replace('language-', '').trim()
      if (lang) {
        const langLabel = document.createElement('span')
        langLabel.className = 'code-lang'
        langLabel.textContent = lang
        pre.appendChild(langLabel)
      }

      const btn = document.createElement('button')
      btn.className = 'code-copy-injected el-button el-button--small is-plain'
      btn.innerHTML = '<span>复制</span>'
      btn.addEventListener('click', () => {
        const text = code.textContent || ''
        navigator.clipboard.writeText(text).then(() => {
          btn.innerHTML = '<span>已复制</span>'
          setTimeout(() => { btn.innerHTML = '<span>复制</span>' }, 2000)
        }).catch(() => {
          const ta = document.createElement('textarea')
          ta.value = text
          document.body.appendChild(ta)
          ta.select()
          document.execCommand('copy')
          document.body.removeChild(ta)
          btn.innerHTML = '<span>已复制</span>'
          setTimeout(() => { btn.innerHTML = '<span>复制</span>' }, 2000)
        })
      })
      pre.style.position = 'relative'
      pre.appendChild(btn)
    })
  }
})
</script>

<style lang="scss" scoped>
.markdown-body :deep(.code-copy-injected) {
  position: absolute;
  top: 6px;
  right: 8px;
  font-size: 12px;
  padding: 2px 8px;
  height: auto;
  border: 1px solid var(--border-color);
  background: var(--bg-primary);
  border-radius: 4px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
  color: var(--text-secondary);
}

.markdown-body :deep(pre:hover .code-copy-injected) {
  opacity: 1;
}

.markdown-body :deep(pre .code-lang) {
  position: absolute;
  top: 8px;
  right: 64px;
  font-size: 12px;
  color: var(--text-tertiary);
}
</style>
