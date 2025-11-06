import { User, Mail, Lock, UserPlus, CreditCard, Phone, Hash, AlertCircle, CheckCircle2 } from 'lucide-react'
import { useState } from 'react'
import { useAuth } from './AuthContext'

interface SignUpProps {
  onSignUp: () => void  // por compatibilidad
  onBack: () => void    // navegar a login
}

export function SignUp({ onSignUp, onBack }: SignUpProps) {
  const { register } = useAuth()
  const [formData, setFormData] = useState({
    nombreCompleto: '',
    cedula: '',
    correo: '',
    numeroServicio: '',
    telefono: '',
    password: '',
    confirmPassword: ''
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [successMsg, setSuccessMsg] = useState('')

  const handleChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccessMsg('')

    if (formData.password !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }
    if (formData.password.length < 8 || formData.password.length > 72) {
      setError('La contraseña debe tener entre 8 y 72 caracteres')
      return
    }

    setLoading(true)
    try {
      const result = await register({
        nombreCompleto: formData.nombreCompleto,
        cedula: formData.cedula,
        correo: formData.correo,
        numeroServicio: formData.numeroServicio,
        telefono: formData.telefono,
        password: formData.password,
        confirmPassword: formData.confirmPassword, // <— AHORA SÍ SE ENVÍA
      })

      if (result.success) {
        setSuccessMsg('Usuario creado ✅. Ahora inicia sesión para continuar.')
      } else {
        // Verás validaciones tipo: "HTTP 400 Bad Request — password: el tamaño debe estar entre 8 y 72\nconfirmPassword: no debe estar vacío"
        setError(result.error || 'Error al crear la cuenta')
      }
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
            {/* Form */}
            <div className="w-full lg:w-1/2 p-6 sm:p-8 md:p-12 flex flex-col justify-center">
              <div className="max-w-md mx-auto w-full">
                <div className="text-center mb-6 md:mb-8">
                  <div className="inline-flex items-center justify-center w-16 h-16 md:w-20 md:h-20 bg-gradient-to-br from-[#0089CF] to-[#0070A8] rounded-full mb-4 shadow-lg">
                    <UserPlus className="w-8 h-8 md:w-10 md:h-10 text-white" />
                  </div>
                  <h1 className="text-3xl md:text-4xl text-[#0089CF] mb-2">Crear Cuenta</h1>
                  <p className="text-sm md:text-base text-gray-600">Regístrate para comenzar</p>
                </div>

                {successMsg && (
                  <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg flex items-start gap-2">
                    <CheckCircle2 className="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" />
                    <div className="text-sm text-green-800 whitespace-pre-line flex-1">
                      {successMsg}
                      <div className="mt-2">
                        <button type="button" onClick={onBack} className="text-[#0089CF] hover:text-[#0070A8] underline">
                          Ir a iniciar sesión
                        </button>
                      </div>
                    </div>
                  </div>
                )}

                {error && (
                  <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg flex items-start gap-2">
                    <AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
                    {/* whitespace-pre-line para múltiples líneas desde el back */}
                    <div className="text-sm text-red-700 whitespace-pre-line flex-1">{error}</div>
                  </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm text-gray-700 mb-2">Nombre completo</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]"><User className="w-5 h-5" /></div>
                      <input
                        type="text"
                        value={formData.nombreCompleto}
                        onChange={(e) => handleChange('nombreCompleto', e.target.value)}
                        placeholder="Juan Pérez"
                        required
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm text-gray-700 mb-2">Cédula</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]"><CreditCard className="w-5 h-5" /></div>
                      <input
                        type="text"
                        value={formData.cedula}
                        onChange={(e) => handleChange('cedula', e.target.value)}
                        placeholder="1234567890"
                        required
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm text-gray-700 mb-2">Correo electrónico</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]"><Mail className="w-5 h-5" /></div>
                      <input
                        type="email"
                        value={formData.correo}
                        onChange={(e) => handleChange('correo', e.target.value)}
                        placeholder="correo@ejemplo.com"
                        required
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm text-gray-700 mb-2">Número de servicio</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]"><Hash className="w-5 h-5" /></div>
                      <input
                        type="text"
                        value={formData.numeroServicio}
                        onChange={(e) => handleChange('numeroServicio', e.target.value)}
                        placeholder="000000000000"
                        required
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm text-gray-700 mb-2">Teléfono</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]"><Phone className="w-5 h-5" /></div>
                      <input
                        type="tel"
                        value={formData.telefono}
                        onChange={(e) => handleChange('telefono', e.target.value)}
                        placeholder="0987654321"
                        required
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm text-gray-700 mb-2">Contraseña</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]"><Lock className="w-5 h-5" /></div>
                      <input
                        type="password"
                        value={formData.password}
                        onChange={(e) => handleChange('password', e.target.value)}
                        placeholder="••••••••"
                        required
                        minLength={8}
                        maxLength={72}
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm text-gray-700 mb-2">Confirmar contraseña</label>
                    <div className="relative">
                      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-[#0089CF]"><Lock className="w-5 h-5" /></div>
                      <input
                        type="password"
                        value={formData.confirmPassword}
                        onChange={(e) => handleChange('confirmPassword', e.target.value)}
                        placeholder="••••••••"
                        required
                        minLength={8}
                        maxLength={72}
                        disabled={loading}
                        className="w-full pl-12 pr-4 py-3 bg-white border-2 border-[#0089CF]/30 rounded-xl focus:outline-none focus:border-[#0089CF] transition-colors shadow-sm text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      />
                    </div>
                  </div>

                  <label className="flex items-start gap-2 cursor-pointer">
                    <input type="checkbox" required disabled={loading}
                      className="mt-1 w-4 h-4 rounded border-[#0089CF]/30 text-[#0089CF] focus:ring-[#0089CF] disabled:opacity-50" />
                    <span className="text-sm text-gray-600">Acepto los términos y condiciones de uso</span>
                  </label>

                  <button
                    type="submit"
                    disabled={loading}
                    className="w-full py-3 md:py-4 bg-gradient-to-r from-[#0089CF] to-[#0070A8] text-white rounded-xl hover:shadow-lg transition-all duration-300 shadow-md text-sm md:text-base disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {loading ? 'Creando cuenta...' : 'Crear Cuenta'}
                  </button>

                  <p className="text-center text-sm md:text-base text-gray-600">
                    ¿Ya tienes cuenta?{' '}
                    <button type="button" onClick={onBack} disabled={loading}
                      className="text-[#0089CF] hover:text-[#0070A8] transition-colors disabled:opacity-50">
                      Inicia sesión
                    </button>
                  </p>
                </form>
              </div>
            </div>

            {/* Visual */}
            <div className="hidden lg:block w-1/2 bg-gradient-to-br from-[#00B140] to-[#008A33] p-12 relative overflow-hidden">
              {/* decorativo */}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
