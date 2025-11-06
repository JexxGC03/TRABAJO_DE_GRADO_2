import { Mail, Lock, Zap, AlertCircle } from 'lucide-react'
import { useState } from 'react'
import { useAuth } from './AuthContext'

interface LoginProps {
  onLogin: () => void
  onSignUp: () => void
}

export function Login({ onLogin, onSignUp }: LoginProps) {
  const { login } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const result = await login(email, password)
      if (result.success) onLogin()
      else setError(result.error || 'Error al iniciar sesión')
    } catch (err: any) {
      setError(err?.message || 'Error de conexión con el servidor')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#E6F7FF] via-[#F0F8FF] to-[#D1EFFF] flex items-center justify-center p-4">
      <div className="w-full max-w-6xl">
        <div className="bg-white/70 backdrop-blur-md rounded-3xl shadow-2xl overflow-hidden">
          <div className="flex flex-col lg:flex-row min-h-[600px]">
            {/* Left Section - Form */}
            <div className="w-full lg:w-1/2 p-6 sm:p-8 md:p-12 flex flex-col justify-center">
              <div className="max-w-md mx-auto w-full">
                {/* Logo/Title */}
                <div className="text-center mb-6 md:mb-8">
                  <div className="inline-flex items-center justify-center w-16 h-16 md:w-20 md:h-20 bg-gradient-to-br from-[#0089CF] to-[#0070A8] rounded-full mb-4 shadow-lg">
                    <Zap className="w-8 h-8 md:w-10 md:h-10 text-white" />
                  </div>
                  <h1 className="text-3xl md:text-4xl text-[#0089CF] mb-2">Bienvenido</h1>
                  <p className="text-sm md:text-base text-gray-600">Inicia sesión en tu cuenta</p>
                </div>

                {/* Error Message */}
                {error && (
                  <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg flex items-start gap-2">
                    <AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
                    <div className="text-sm text-red-700 whitespace-pre-line flex-1">{error}</div>
                  </div>
                )}

                {/* Form */}
                <form onSubmit={handleSubmit} className="space-y-4 md:space-y-5">
                  {/* Email Input */}
                  <div>
                    <label className="block text-sm md:text-base text-gray-700 mb-2">Correo electrónico</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]">
                        <Mail className="w-5 h-5" />
                      </div>
                      <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="correo@ejemplo.com"
                        required
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 md:py-4 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm md:text-base disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  {/* Password Input */}
                  <div>
                    <label className="block text-sm md:text-base text-gray-700 mb-2">Contraseña</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]">
                        <Lock className="w-5 h-5" />
                      </div>
                      <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••"
                        required
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 md:py-4 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm md:text-base disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  {/* Remember Me & Forgot Password */}
                  <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input type="checkbox" className="w-4 h-4 rounded border-[#0089CF]/30 text-[#0089CF] focus:ring-[#0089CF]" />
                      <span className="text-sm text-gray-600">Recordarme</span>
                    </label>
                    <a href="#" className="text-sm text-[#0089CF] hover:text-[#0070A8] transition-colors">
                      ¿Olvidaste tu contraseña?
                    </a>
                  </div>

                  {/* Login Button */}
                  <button
                    type="submit"
                    disabled={loading}
                    className="w-full py-3 md:py-4 bg-gradient-to-r from-[#0089CF] to-[#0070A8] text-white rounded-xl hover:shadow-lg transition-all duration-300 shadow-md text-sm md:text-base disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
                  </button>

                  {/* Sign Up Link */}
                  <p className="text-center text-sm md:text-base text-gray-600">
                    ¿No tienes una cuenta?{' '}
                    <button
                      type="button"
                      onClick={onSignUp}
                      disabled={loading}
                      className="text-[#0089CF] hover:text-[#0070A8] transition-colors disabled:opacity-50"
                    >
                      Regístrate
                    </button>
                  </p>
                </form>
              </div>
            </div>

            {/* Right Section - Visual */}
            <div className="hidden lg:block w-1/2 bg-gradient-to-br from-[#0089CF] to-[#0070A8] p-12 flex-col justify-center items-center relative overflow-hidden">
              <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImdyaWQiIHdpZHRoPSI2MCIgaGVpZ2h0PSI2MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTSAxMCAwIEwgMCAwIDAgMTAiIGZpbGw9Im5vbmUiIHN0cm9rZT0id2hpdGUiIHN0cm9rZS1vcGFjaXR5PSIwLjA1IiBzdHJva2Utd2lkdGg9IjEiLz48L3BhdHRlcm4+PC9kZWZzPjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz48L3N2Zz4=')] opacity-20"></div>
              <div className="relative z-10 flex flex-col justify-center items-center h-full text-white">
                <div className="mb-8">
                  <Zap className="w-24 h-24 text-white/90" />
                </div>
                <h2 className="text-4xl mb-4 text-center">Gestión Energética</h2>
                <p className="text-lg text-center text-white/90 max-w-md">
                  Monitorea y optimiza tu consumo eléctrico de manera inteligente
                </p>
                <div className="mt-12 grid grid-cols-3 gap-8 w-full max-w-md">
                  <div className="text-center">
                    <div className="text-3xl mb-2">⚡</div>
                    <p className="text-sm text-white/80">Consumo en tiempo real</p>
                  </div>
                  <div className="text-center">
                    <div className="text-3xl mb-2">📊</div>
                    <p className="text-sm text-white/80">Estadísticas detalladas</p>
                  </div>
                  <div className="text-center">
                    <div className="text-3xl mb-2">💡</div>
                    <p className="text-sm text-white/80">Consejos de ahorro</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
