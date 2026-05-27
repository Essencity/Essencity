/**
 * 统一的 API 请求工具，自动附加 JWT 认证头
 * 开发环境：Vite proxy 将 /api 代理到 localhost:8080
 * 生产环境：通过 VITE_API_BASE_URL 环境变量指定后端地址
 */
const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

function getToken() {
  return localStorage.getItem('token')
}

export function setAuth(token, user) {
  localStorage.setItem('token', token)
  localStorage.setItem('user', JSON.stringify(user))
}

export function clearAuth() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
}

export function getCurrentUser() {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : null
}

export function isLoggedIn() {
  return !!getToken()
}

export async function apiFetch(url, options = {}) {
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE}${url}`, {
    ...options,
    headers
  })

  if (response.status === 401) {
    clearAuth()
    window.location.reload()
  }

  return response
}

export async function apiGet(url) {
  const response = await apiFetch(url)
  return response.json()
}

export async function apiPost(url, body) {
  const response = await apiFetch(url, {
    method: 'POST',
    body: JSON.stringify(body)
  })
  return response.json()
}

export async function apiPut(url, body) {
  const response = await apiFetch(url, {
    method: 'PUT',
    body: JSON.stringify(body)
  })
  return response.json()
}

export async function apiDelete(url, body) {
  const options = { method: 'DELETE' }
  if (body) {
    options.body = JSON.stringify(body)
  }
  const response = await apiFetch(url, options)
  return response.json()
}
