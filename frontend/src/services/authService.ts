// src/services/authService.ts
import api from './api'

export type LoginResponse = {
  accessToken: string
  refreshToken?: string
  user?: { id: string; fullName?: string; email?: string; role?: string }
}

export async function login(
  email: string,
  password: string
): Promise<{ success: boolean; error?: string; user?: LoginResponse['user'] }> {
  const { data, error } = await api.post<LoginResponse>('/auth/login', { email, password })

  if (error) {
    return { success: false, error }
  }

  if (!data?.accessToken) {
    return { success: false, error: 'Respuesta inválida del servidor (sin accessToken).' }
  }

  localStorage.setItem('accessToken', data.accessToken)
  if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken)

  return { success: true, user: data.user }
}

/** Registro: ahora ENVIAMOS confirmPassword (el back lo está validando) */
export async function register(payload: {
  nombreCompleto: string
  cedula: string
  correo: string
  numeroServicio: string
  telefono: string
  password: string
  confirmPassword: string
}): Promise<{ success: boolean; error?: string }> {
  const body = {
    fullName: payload.nombreCompleto,
    citizenId: payload.cedula,
    email: payload.correo,
    serviceNumber: payload.numeroServicio,
    phone: payload.telefono,
    password: payload.password,
    confirmPassword: payload.confirmPassword, // <— CLAVE
  }

  const { error } = await api.post<void>('/auth/register', body)
  if (error) return { success: false, error }
  return { success: true }
}

export function logout() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
}
