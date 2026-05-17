import { marked } from 'marked'
import hljs from 'highlight.js'

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

export function useMarkdown() {
  setupMarked()

  function render(text) {
    if (!text) return ''
    return marked.parse(text)
  }

  return { render }
}
