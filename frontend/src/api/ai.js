const API_BASE = '/api'

export async function getAiSummary(postId) {
  const response = await fetch(`${API_BASE}/ai/summary/${postId}`)
  if (!response.ok) {
    throw new Error('获取AI总结失败')
  }
  return response.json()
}

export async function generateAiSummary(postId, title, content) {
  const response = await fetch(`${API_BASE}/ai/summary`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ postId, title, content })
  })
  if (!response.ok) {
    throw new Error('生成AI总结失败')
  }
  return response.json()
}

export async function recommendTags(title, content) {
  const response = await fetch(`${API_BASE}/ai/recommend-tags`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, content })
  })
  if (!response.ok) {
    throw new Error('标签推荐失败')
  }
  return response.json()
}

export async function aiAssist(mode, title, content) {
  const response = await fetch(`${API_BASE}/ai/assist`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ mode, title, content })
  })
  if (!response.ok) {
    throw new Error('内容生成失败')
  }
  return response.json()
}

export async function askAiAboutPost(postId, title, content, question, history = []) {
  const response = await fetch(`${API_BASE}/ai/ask`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ postId, title, content, question, history })
  })
  if (!response.ok) {
    throw new Error('问答失败')
  }
  return response.json()
}