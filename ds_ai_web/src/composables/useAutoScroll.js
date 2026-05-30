import { ref, nextTick, watch } from 'vue'

export function useAutoScroll(containerRef, content) {
  const isAtBottom = ref(true)

  function scrollToBottom() {
    nextTick(() => {
      const el = containerRef.value
      if (el && isAtBottom.value) {
        el.scrollTop = el.scrollHeight
      }
    })
  }

  function onScroll() {
    const el = containerRef.value
    if (!el) return
    const threshold = 100
    isAtBottom.value =
      el.scrollTop >= el.scrollHeight - el.clientHeight - threshold
  }

  // 监听内容变化自动滚动
  watch(content, () => {
    scrollToBottom()
  }, { deep: false })

  return { isAtBottom, scrollToBottom, onScroll }
}
