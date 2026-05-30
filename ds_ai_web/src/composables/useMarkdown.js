import { marked } from 'marked'
import hljs from 'highlight.js'
import katex from 'katex'

let configured = false

function setupMarked() {
  if (configured) return
  configured = true

  marked.setOptions({
    breaks: true,
    gfm: true
  })

  const renderer = new marked.Renderer()

  renderer.code = function ({ text, lang }) {
    const validLang = lang && hljs.getLanguage(lang) ? lang : 'plaintext'
    const highlighted = hljs.highlight(text, { language: validLang }).value
    return `<pre><code class="hljs language-${validLang}">${highlighted}</code></pre>`
  }

  marked.use({ renderer })
}

/**
 * 渲染行内数学公式 $...$
 */
function renderInlineMath(text) {
  return text.replace(/\$([^$]+)\$/g, (_, formula) => {
    try {
      return katex.renderToString(formula.trim(), { throwOnError: false, displayMode: false })
    } catch (e) {
      return `<code>${formula}</code>`
    }
  })
}

/**
 * 渲染块级数学公式 $$...$$
 */
function renderBlockMath(text) {
  return text.replace(/\$\$([^$]+)\$\$/g, (_, formula) => {
    try {
      return katex.renderToString(formula.trim(), { throwOnError: false, displayMode: true })
    } catch (e) {
      return `<pre><code>${formula}</code></pre>`
    }
  })
}

export function useMarkdown() {
  setupMarked()

  function render(text) {
    if (!text) return ''

    // 1. 保护代码块，避免其中的数学符号被误处理
    const codeBlocks = []
    const protectedText = text.replace(/```[\s\S]*?```/g, (match) => {
      codeBlocks.push(match)
      return `%%CODEBLOCK_${codeBlocks.length - 1}%%`
    })

    // 2. 保护行内代码
    const inlineCodes = []
    const withProtectedInline = protectedText.replace(/`([^`]+)`/g, (match) => {
      inlineCodes.push(match)
      return `%%INLINECODE_${inlineCodes.length - 1}%%`
    })

    // 3. 先渲染块级数学公式
    let processed = renderBlockMath(withProtectedInline)
    // 4. 再渲染行内数学公式
    processed = renderInlineMath(processed)

    // 5. 恢复行内代码
    processed = processed.replace(/%%INLINECODE_(\d+)%%/g, (_, i) => inlineCodes[parseInt(i)])

    // 6. 恢复代码块
    processed = processed.replace(/%%CODEBLOCK_(\d+)%%/g, (_, i) => codeBlocks[parseInt(i)])

    // 7. Markdown 解析
    return marked.parse(processed)
  }

  return { render }
}
