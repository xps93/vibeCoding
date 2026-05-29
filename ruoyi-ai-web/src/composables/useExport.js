import { ElMessage } from 'element-plus'

/**
 * 对话导出工具 — 支持 Markdown / Text / JSON 格式
 */
export function useExport() {
  const FORMATS = [
    { value: 'md', label: 'Markdown (.md)' },
    { value: 'txt', label: '纯文本 (.txt)' },
    { value: 'json', label: 'JSON (.json)' }
  ]

  /** 导出对话消息到文件 */
  function exportConversation(messages, title, format = 'md') {
    if (!messages || messages.length === 0) {
      ElMessage.warning('没有可导出的消息')
      return
    }

    const safeTitle = sanitizeFileName(title || '对话')
    let content, mimeType, extension

    switch (format) {
      case 'md':
        content = toMarkdown(messages, title)
        mimeType = 'text/markdown;charset=utf-8'
        extension = 'md'
        break
      case 'txt':
        content = toPlainText(messages, title)
        mimeType = 'text/plain;charset=utf-8'
        extension = 'txt'
        break
      case 'json':
        content = toJson(messages, title)
        mimeType = 'application/json;charset=utf-8'
        extension = 'json'
        break
      default:
        content = toMarkdown(messages, title)
        mimeType = 'text/markdown;charset=utf-8'
        extension = 'md'
    }

    const blob = new Blob(['﻿' + content], { type: mimeType })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `${safeTitle}.${extension}`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    ElMessage.success(`已导出为 ${extension.toUpperCase()} 文件`)
  }

  return { FORMATS, exportConversation }
}

/** 生成 Markdown 格式 */
function toMarkdown(messages, title) {
  let md = `# ${title}\n\n`
  md += `> 导出时间: ${new Date().toLocaleString()}\n\n---\n\n`

  for (const msg of messages) {
    const role = msg.role === 'user' ? '🧑 用户' : '🤖 AI助手'
    md += `### ${role}\n\n`
    md += `${msg.content}\n\n`
    if (msg.reasoningContent) {
      md += `<details>\n<summary>思考过程</summary>\n\n${msg.reasoningContent}\n\n</details>\n\n`
    }
    md += `---\n\n`
  }
  return md
}

/** 生成纯文本格式 */
function toPlainText(messages, title) {
  let txt = `${title}\n`
  txt += `${'='.repeat(50)}\n`
  txt += `导出时间: ${new Date().toLocaleString()}\n\n`

  for (const msg of messages) {
    const role = msg.role === 'user' ? '[用户]' : '[AI助手]'
    txt += `${role}\n`
    txt += `${msg.content}\n\n`
  }
  return txt
}

/** 生成 JSON 格式 */
function toJson(messages, title) {
  const data = {
    title,
    exportTime: new Date().toISOString(),
    messages: messages.map(m => ({
      role: m.role,
      content: m.content,
      reasoningContent: m.reasoningContent || null,
      timestamp: m.timestamp || null
    }))
  }
  return JSON.stringify(data, null, 2)
}

/** 清理文件名中的非法字符 */
function sanitizeFileName(name) {
  return name.replace(/[\\/:*?"<>|]/g, '_').trim() || '未命名对话'
}
