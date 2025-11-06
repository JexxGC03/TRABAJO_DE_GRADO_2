import { createContext, useContext, useEffect, useState, ReactNode } from 'react'
import { login as authLogin, register as authRegister, logout as authLogout } from '../services/authService'

type User = {
  id?: string
  fullName?: string
  email?: string
  role?: string
} | null

interface AuthContextType {
  isAuthenticated: boolean
  user: User
  login: (email: string, password: string) => Promise<{ success: boolean; error?: string }>
  register: (userData: {
    nombreCompleto: string
    cedula: string
    correo: string
    numeroServicio: string
    telefono: string
    password: string
    confirmPassword: string
  }) => Promise<{ success: boolean; error?: string }>
  logout: () => void
  loading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

function hasToken() {
  return !!localStorage.getItem('accessToken')
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false)
  const [user, setUser] = useState<User>(null)
  const [loading, setLoading] = useState<boolean>(true)

  useEffect(() => {
    setIsAuthenticated(hasToken())
    setLoading(false)
  }, [])

  const login = async (email: string, password: string) => {
    const result = await authLogin(email, password)
    if (result.success) {
      setIsAuthenticated(true)
      setUser(result.user ?? null)
      return { success: true }
    }
    return { success: false, error: result.error }
  }

  const register = async (userData: {
    nombreCompleto: string
    cedula: string
    correo: string
    numeroServicio: string
    telefono: string
    password: string
    confirmPassword: string
  }) => {
    const result = await authRegister(userData)
    if (result.success) {
      return { success: true }
    }
    return { success: false, error: result.error }
  }

  const logout = () => {
    authLogout()
    setIsAuthenticated(false)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, register, logout, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe ser usado dentro de un AuthProvider')
  return ctx
}
