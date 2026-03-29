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