const API_BASE = '/api'

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem('token')
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers
  })

  if (!response.ok) {
    const payload = await response.json().catch(() => ({ message: 'Solicitud no completada' }))
    throw new Error(payload.message || 'Solicitud no completada')
  }

  return response.status === 204 ? null : response.json()
}
