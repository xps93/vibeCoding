const BASE = '/api'

function postJson(url, body, token) {
  const headers = { 'Content-Type': 'application/json' }
  if (token) headers['Authorization'] = `Bearer ${token}`
  return fetch(url, { method: 'POST', headers, body: JSON.stringify(body) }).then(res => res.json())
}

export function login(username, password) {
  return postJson(`${BASE}/login`, { username, password })
}

export function register(data) {
  return postJson(`${BASE}/register`, data)
}

export function sendSmsCode(phone) {
  return postJson(`${BASE}/send-code`, { phone })
}

export function verifyIdentity(phone, code) {
  return postJson(`${BASE}/verify-identity`, { phone, code })
}

export function resetPassword(data) {
  return postJson(`${BASE}/reset-password`, data)
}

export function logout(token) {
  return fetch(`${BASE}/logout`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  }).then(res => res.json())
}
