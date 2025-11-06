import { Settings, Bell, Moon, Volume2, Lock, Wifi, Database, Home } from 'lucide-react'
import { motion } from 'motion/react'

interface ConfiguracionProps {
  onNavigate?: (view: string) => void
  darkMode?: boolean
  onToggleDarkMode?: () => void
}

export function Configuracion({ onNavigate, darkMode = false, onToggleDarkMode }: ConfiguracionProps) {
  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-4xl mx-auto space-y-3 md:space-y-4"
    >
      {/* Title Section */}
      <div className="flex items-center gap-3">
        <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center">
          <Settings className="w-5 h-5 text-white" />
        </div>
        <h1 className="text-3xl text-primary">Configuración</h1>
      </div>

      {/* Main Content Card */}
      <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-6 shadow-md">
        <div className="space-y-4">
          {/* Notificaciones */}
          <div className="border-b border-primary/20 pb-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center">
                  <Bell className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="text-lg text-gray-800">Notificaciones</h3>
                  <p className="text-sm text-gray-600">Recibir alertas por correo electrónico</p>
                </div>
              </div>
              <label className="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" defaultChecked className="sr-only peer" />
                <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/30 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
              </label>
            </div>
          </div>

          {/* Tema */}
          <div className="border-b border-primary/20 pb-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center">
                  <Moon className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="text-lg text-gray-800">Modo oscuro</h3>
                  <p className="text-sm text-gray-600">Cambiar tema de la interfaz</p>
                </div>
              </div>
              <label className="relative inline-flex items-center cursor-pointer">
                <input 
                  type="checkbox" 
                  checked={darkMode}
                  onChange={onToggleDarkMode ? onToggleDarkMode : undefined}
                  className="sr-only peer" 
                />
                <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/30 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
              </label>
            </div>
          </div>

          {/* Sonido */}
          <div className="border-b border-primary/20 pb-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center">
                  <Volume2 className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="text-lg text-gray-800">Sonido de alertas</h3>
                  <p className="text-sm text-gray-600">Activar sonido para notificaciones</p>
                </div>
              </div>
              <label className="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" defaultChecked className="sr-only peer" />
                <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/30 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
              </label>
            </div>
          </div>

          {/* Contadores */}
          <div className="border-b border-primary/20 pb-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center">
                  <Home className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="text-lg text-gray-800">Mis Contadores</h3>
                  <p className="text-sm text-gray-600">Agregar o editar contadores</p>
                </div>
              </div>
              <button 
                onClick={() => onNavigate && onNavigate('gestion-inmuebles')}
                className="px-4 py-2 bg-secondary text-primary rounded-lg hover:bg-accent transition-colors"
              >
                Gestionar
              </button>
            </div>
          </div>

          {/* Privacidad */}
          <div className="border-b border-primary/20 pb-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center">
                  <Lock className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="text-lg text-gray-800">Privacidad y seguridad</h3>
                  <p className="text-sm text-gray-600">Gestionar datos personales</p>
                </div>
              </div>
              <button 
                onClick={() => onNavigate && onNavigate('perfil')}
                className="px-4 py-2 bg-secondary text-primary rounded-lg hover:bg-accent transition-colors"
              >
                Gestionar
              </button>
            </div>
          </div>

          {/* Conexión */}
          <div className="border-b border-primary/20 pb-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center">
                  <Wifi className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="text-lg text-gray-800">Conexión con contador</h3>
                  <p className="text-sm text-gray-600">Estado: Conectado</p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                <span className="text-sm text-gray-600">Activo</span>
              </div>
            </div>
          </div>

          {/* Datos */}
          <div className="pb-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center">
                  <Database className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <h3 className="text-lg text-gray-800">Gestión de datos</h3>
                  <p className="text-sm text-gray-600">Exportar o eliminar datos históricos</p>
                </div>
              </div>
              <button className="px-4 py-2 bg-primary text-white rounded-lg hover:opacity-90 transition-colors">
                Exportar
              </button>
            </div>
          </div>
        </div>

        {/* Version Info */}
        <div className="mt-8 pt-6 border-t border-primary/20 text-center">
          <p className="text-sm text-gray-500">Energy Management v1.0.0</p>
          <p className="text-xs text-gray-400 mt-1">© 2025 Todos los derechos reservados</p>
        </div>
      </div>
    </motion.div>
  )
}
