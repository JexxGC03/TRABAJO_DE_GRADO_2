import { api } from './api';

export type LoginResponse = {
  accessToken?: string;
  refreshToken?: string;
  access_token?: string;
  refresh_token?: string;
  tokenType?: string;
  userId?: string;
  role?: string;
  expiresIn?: number;
};

export async function login(email: string, password: string): Promise<LoginResponse> {
  console.debug('[AuthApi] POST /auth/login', { email });
  const { data } = await api.post<LoginResponse>('/auth/login', { email, password });
  const at = data.accessToken ?? data.access_token;
  const rt = data.refreshToken ?? data.refresh_token;
  if (!at) throw new Error('Login sin accessToken');
  storeTokens(at, rt ?? null);
  return data;
}

export async function register(fullName: string, email: string, password: string): Promise<LoginResponse> {
  console.debug('[AuthApi] POST /auth/register', { fullName, email });
  const { data } = await api.post<LoginResponse>('/auth/register', { fullName, email, password });
  // Algunos back devuelven token al registrar; si viene, lo guardamos.
  const at = data?.accessToken ?? data?.access_token;
  const rt = data?.refreshToken ?? data?.refresh_token;
  if (at) storeTokens(at, rt ?? null);
  return data;
}

export async function logout(): Promise<void> {
  const refreshToken = localStorage.getItem('refresh_token');
  try {
    console.debug('[AuthApi] POST /auth/logout');
    await api.post('/auth/logout', { refreshToken });
  } catch (e) {
    // si falla, no bloqueamos el cierre de sesión
    console.warn('[AuthApi] logout falló, limpiamos igual:', e);
  } finally {
    clearTokens();
  }
}

export function storeTokens(accessToken: string, refreshToken: string | null) {
  localStorage.setItem('access_token', accessToken);
  if (refreshToken) localStorage.setItem('refresh_token', refreshToken);
}

export function clearTokens() {
  localStorage.removeItem('access_token');
  localStorage.removeItem('refresh_token');
}
