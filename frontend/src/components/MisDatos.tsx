import { User, Mail, Phone, Lock, Edit, Save, ArrowLeft, Eye, EyeOff } from 'lucide-react'
import { motion } from 'motion/react'
import { Breadcrumbs } from './Breadcrumbs'
import { useState } from 'react'

interface MisDatosProps {
  onNavigate?: (view: string) => void
}

export function MisDatos({ onNavigate }: MisDatosProps) {
  const [isEditing, setIsEditing] = useState(false)
  const [isEditingPassword, setIsEditingPassword] = useState(false)
  const [showCurrentPassword, setShowCurrentPassword] = useState(false)
  const [showNewPassword, setShowNewPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  
  const [formData, setFormData] = useState({
    nombre: 'Nicolas',
    apellido: 'Estupiñan',
    email: 'nestupinan38@ucatolica.edu.co',
    telefono: '3045607805'
  })

  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })

  const handleSave = () => {
    setIsEditing(false)
    // Aquí se guardarían los datos
  }

  const handleSavePassword = () => {
    if (passwordData.newPassword === passwordData.confirmPassword) {
      setIsEditingPassword(false)
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' })
      // Aquí se guardaría la contraseña
    }
  }

  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-4xl mx-auto space-y-4"
    >
      {/* Breadcrumbs */}
      {onNavigate && (
        <Breadcrumbs 
          items={[
            { label: 'Configuración', onClick: () => onNavigate('configuracion') },
            { label: 'Privacidad y seguridad' },
            { label: 'Mis Datos' }
          ]} 
        />
      )}

      {/* Header with Back Button */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 md:gap-3">
          <div className="w-8 h-8 md:w-10 md:h-10 bg-primary rounded-full flex items-center justify-center">
            <User className="w-4 h-4 md:w-5 md:h-5 text-white" />
          </div>
          <h1 className="text-2xl md:text-3xl text-primary">Mi Perfil</h1>
        </div>
        {onNavigate && (
          <button
            onClick={() => onNavigate('configuracion')}
            className="flex items-center gap-2 px-3 py-2 md:px-4 md:py-2 bg-secondary hover:bg-accent text-primary rounded-lg transition-colors text-sm md:text-base"
          >
            <ArrowLeft className="w-4 h-4" />
            Volver
          </button>
        )}
      </div>

      {/* Profile Card */}
      <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-4 md:p-8 shadow-md">
        {/* Header with Avatar and Edit Button */}
        <div className="flex flex-col sm:flex-row items-center sm:items-start justify-between gap-4 mb-6 md:mb-8 pb-4 md:pb-6 border-b border-primary/20">
          <div className="flex flex-col sm:flex-row items-center gap-4 md:gap-6 w-full sm:w-auto">
            <div className="relative">
              <div className="w-20 h-20 md:w-24 md:h-24 bg-gradient-to-br from-primary to-primary/80 rounded-full flex items-center justify-center shadow-lg">
                <User className="w-10 h-10 md:w-12 md:h-12 text-white" />
              </div>
            </div>
            <div className="text-center sm:text-left">
              <h2 className="text-xl md:text-2xl text-gray-800">{formData.nombre} {formData.apellido}</h2>
              <p className="text-sm md:text-base text-gray-600">{formData.email}</p>
            </div>
          </div>
          
          {!isEditing && (
            <button 
              onClick={() => setIsEditing(true)}
              className="flex items-center gap-2 px-3 py-2 md:px-4 md:py-2 bg-primary text-white rounded-lg hover:opacity-90 transition-colors text-sm md:text-base w-full sm:w-auto justify-center"
            >
              <Edit className="w-4 h-4" />
              Cambiar Datos
            </button>
          )}
        </div>

        {/* Personal Information */}
        <div className="space-y-6">
          <h3 className="text-xl text-primary mb-4">Información Personal</h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Nombre */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-sm text-gray-700">
                <User className="w-4 h-4 text-primary" />
                Nombre
              </label>
              <input
                type="text"
                value={formData.nombre}
                onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                disabled={!isEditing}
                className={`w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary ${
                  !isEditing ? 'opacity-60 cursor-not-allowed' : ''
                }`}
              />
            </div>

            {/* Apellido */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-sm text-gray-700">
                <User className="w-4 h-4 text-primary" />
                Apellido
              </label>
              <input
                type="text"
                value={formData.apellido}
                onChange={(e) => setFormData({...formData, apellido: e.target.value})}
                disabled={!isEditing}
                className={`w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary ${
                  !isEditing ? 'opacity-60 cursor-not-allowed' : ''
                }`}
              />
            </div>

            {/* Email */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-sm text-gray-700">
                <Mail className="w-4 h-4 text-primary" />
                Correo electrónico
              </label>
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                disabled={!isEditing}
                className={`w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary ${
                  !isEditing ? 'opacity-60 cursor-not-allowed' : ''
                }`}
              />
            </div>

            {/* Teléfono */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-sm text-gray-700">
                <Phone className="w-4 h-4 text-primary" />
                Teléfono
              </label>
              <input
                type="tel"
                value={formData.telefono}
                onChange={(e) => setFormData({...formData, telefono: e.target.value})}
                disabled={!isEditing}
                className={`w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary ${
                  !isEditing ? 'opacity-60 cursor-not-allowed' : ''
                }`}
              />
            </div>
          </div>

          {/* Action Buttons */}
          {isEditing && (
            <div className="flex flex-col sm:flex-row gap-3 md:gap-4 pt-4">
              <button 
                onClick={handleSave}
                className="flex-1 flex items-center justify-center gap-2 px-4 py-2 md:px-6 md:py-3 bg-primary text-white rounded-lg hover:opacity-90 transition-colors shadow-md text-sm md:text-base"
              >
                <Save className="w-4 h-4 md:w-5 md:h-5" />
                Guardar Cambios
              </button>
              <button 
                onClick={() => setIsEditing(false)}
                className="px-4 py-2 md:px-6 md:py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm md:text-base"
              >
                Cancelar
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Security Section - Change Password */}
      <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-4 md:p-8 shadow-md">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-primary/20">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 md:w-10 md:h-10 bg-secondary rounded-full flex items-center justify-center">
              <Lock className="w-4 h-4 md:w-5 md:h-5 text-primary" />
            </div>
            <div>
              <h3 className="text-lg md:text-xl text-primary">Seguridad</h3>
              <p className="text-xs md:text-sm text-gray-600">Cambiar contraseña</p>
            </div>
          </div>
          
          {!isEditingPassword && (
            <button 
              onClick={() => setIsEditingPassword(true)}
              className="flex items-center gap-2 px-3 py-2 md:px-4 md:py-2 bg-primary text-white rounded-lg hover:opacity-90 transition-colors text-sm md:text-base w-full sm:w-auto justify-center"
            >
              <Lock className="w-4 h-4" />
              Cambiar Contraseña
            </button>
          )}
        </div>

        {isEditingPassword && (
          <div className="space-y-6">
            {/* Contraseña actual */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-sm text-gray-700">
                <Lock className="w-4 h-4 text-primary" />
                Contraseña actual
              </label>
              <div className="relative">
                <input
                  type={showCurrentPassword ? "text" : "password"}
                  value={passwordData.currentPassword}
                  onChange={(e) => setPasswordData({...passwordData, currentPassword: e.target.value})}
                  placeholder="Ingresa tu contraseña actual"
                  className="w-full px-4 py-3 pr-12 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
                <button
                  type="button"
                  onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                >
                  {showCurrentPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            {/* Nueva contraseña */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-sm text-gray-700">
                <Lock className="w-4 h-4 text-primary" />
                Nueva contraseña
              </label>
              <div className="relative">
                <input
                  type={showNewPassword ? "text" : "password"}
                  value={passwordData.newPassword}
                  onChange={(e) => setPasswordData({...passwordData, newPassword: e.target.value})}
                  placeholder="Ingresa tu nueva contraseña"
                  className="w-full px-4 py-3 pr-12 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
                <button
                  type="button"
                  onClick={() => setShowNewPassword(!showNewPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                >
                  {showNewPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            {/* Confirmar contraseña */}
            <div className="space-y-2">
              <label className="flex items-center gap-2 text-sm text-gray-700">
                <Lock className="w-4 h-4 text-primary" />
                Confirmar nueva contraseña
              </label>
              <div className="relative">
                <input
                  type={showConfirmPassword ? "text" : "password"}
                  value={passwordData.confirmPassword}
                  onChange={(e) => setPasswordData({...passwordData, confirmPassword: e.target.value})}
                  placeholder="Confirma tu nueva contraseña"
                  className="w-full px-4 py-3 pr-12 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                >
                  {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            {/* Validation Message */}
            {passwordData.newPassword && passwordData.confirmPassword && 
             passwordData.newPassword !== passwordData.confirmPassword && (
              <p className="text-sm text-red-600">Las contraseñas no coinciden</p>
            )}

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row gap-3 md:gap-4 pt-4">
              <button 
                onClick={handleSavePassword}
                disabled={!passwordData.currentPassword || !passwordData.newPassword || 
                         passwordData.newPassword !== passwordData.confirmPassword}
                className="flex-1 flex items-center justify-center gap-2 px-4 py-2 md:px-6 md:py-3 bg-primary text-white rounded-lg hover:opacity-90 transition-colors shadow-md disabled:opacity-50 disabled:cursor-not-allowed text-sm md:text-base"
              >
                <Save className="w-4 h-4 md:w-5 md:h-5" />
                Actualizar Contraseña
              </button>
              <button 
                onClick={() => {
                  setIsEditingPassword(false)
                  setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' })
                }}
                className="px-4 py-2 md:px-6 md:py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm md:text-base"
              >
                Cancelar
              </button>
            </div>
          </div>
        )}

        {!isEditingPassword && (
          <p className="text-sm text-gray-600">
            Para mayor seguridad, te recomendamos cambiar tu contraseña periódicamente.
          </p>
        )}
      </div>
    </motion.div>
  )
}
