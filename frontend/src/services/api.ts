// src/services/api.ts
export interface ApiResponse<T> { data?: T; message?: string; error?: string }

const API_BASE_URL = 'http://localhost:8081/api'

function authHeader(): Record<string, string> {
  const token = localStorage.getItem('accessToken')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

// Normaliza cualquier HeadersInit a Headers
function mergeHeaders(base?: HeadersInit, extra?: Record<string, string>): Headers {
  const h = new Headers()
  if (base instanceof Headers) {
    base.forEach((v, k) => h.set(k, v))
  } else if (Array.isArray(base)) {
    base.forEach(([k, v]) => h.set(k, v))
  } else if (base && typeof base === 'object') {
    Object.entries(base).forEach(([k, v]) => h.set(k, String(v)))
  }
  if (extra) Object.entries(extra).forEach(([k, v]) => h.set(k, v))
  return h
}

async function parseBody(res: Response): Promise<any> {
  if (res.status === 204) return undefined
  const ct = res.headers.get('content-type') || ''
  try {
    if (ct.includes('application/json')) return await res.json()
    return await res.text()
  } catch { return undefined }
}

// Extrae mensajes útiles del body de error del back (Spring Boot 3 / Bean Validation)
function extractServerError(body: any): string | undefined {
  if (!body) return undefined
  if (typeof body === 'string') return body

  // RFC7807 ProblemDetail
  if (body.title || body.detail) {
    return [body.title, body.detail].filter(Boolean).join(' — ')
  }

  const pickMsg = (e: any) =>
    e?.defaultMessage || e?.message || (e?.field ? `${e.field}: ${e.error || e.code || 'inválido'}` : undefined)

  if (Array.isArray(body.errors)) {
    const msgs = body.errors.map(pickMsg).filter(Boolean)
    if (msgs.length) return msgs.join('\n')
  }
  if (Array.isArray(body.fieldErrors)) {
    const msgs = body.fieldErrors.map(pickMsg).filter(Boolean)
    if (msgs.length) return msgs.join('\n')
  }

  const v = body.violations || body['constraint-violations']
  if (Array.isArray(v)) {
    const msgs = v.map((e: any) => e?.message || (e?.field ? `${e.field}: ${e?.message}` : undefined)).filter(Boolean)
    if (msgs.length) return msgs.join('\n')
  }

  return body.message || body.error || body.reason
}

async function apiRequest<T>(endpoint: string, options: RequestInit = {}): Promise<ApiResponse<T>> {
  const url = `${API_BASE_URL}${endpoint}`
  const headers = mergeHeaders(options.headers, {
    'Content-Type': 'application/json',
    ...authHeader(),
  })

  try {
    const res = await fetch(url, {
      method: options.method ?? 'GET',
      headers,
      body: options.body,
      credentials: 'omit', // cambia a 'include' si usas cookies
      mode: 'cors',
    })

    const body = await parseBody(res)

    if (!res.ok) {
      const serverMsg = extractServerError(body)
      const errorText = `HTTP ${res.status} ${res.statusText}${serverMsg ? ` — ${serverMsg}` : ''}`
      console.error(`❌ API ERROR: ${options.method ?? 'GET'} ${endpoint}\n`, errorText, body)
      return { error: errorText }
    }

    return { data: body as T, message: (body && (body.message as string)) || undefined }
  } catch (err: any) {
    const msg = err?.message?.includes('Failed to fetch')
      ? `Fallo de red al llamar ${url}. Posible CORS o servidor caído.`
      : `Error de red: ${err?.message || err}`
    console.error(`🌐 NETWORK ERROR: ${options.method ?? 'GET'} ${endpoint}\n`, err)
    return { error: msg }
  }
}

export const api = {
  get:  <T,>(endpoint: string, options?: RequestInit) =>
    apiRequest<T>(endpoint, { ...options, method: 'GET' }),
  post: <T,>(endpoint: string, body: any, options?: RequestInit) =>
    apiRequest<T>(endpoint, { ...options, method: 'POST', body: JSON.stringify(body) }),
  put:  <T,>(endpoint: string, body: any, options?: RequestInit) =>
    apiRequest<T>(endpoint, { ...options, method: 'PUT', body: JSON.stringify(body) }),
  patch:<T,>(endpoint: string, body: any, options?: RequestInit) =>
    apiRequest<T>(endpoint, { ...options, method: 'PATCH', body: JSON.stringify(body) }),
  delete:<T,>(endpoint: string, options?: RequestInit) =>
    apiRequest<T>(endpoint, { ...options, method: 'DELETE' }),
}

export default api
