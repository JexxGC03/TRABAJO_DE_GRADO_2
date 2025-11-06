import { Wifi, Calendar, Zap, ArrowLeft, Info, MapPin, Hash, Activity, Tag, Settings } from 'lucide-react'
import { motion } from 'motion/react'
import { Breadcrumbs } from './Breadcrumbs'
import { useInmueble } from './InmuebleContext'

interface MiContadorProps {
  onNavigate?: (view: string) => void
}

export function MiContador({ onNavigate }: MiContadorProps) {
  const { meterSeleccionado, meters } = useInmueble()
  
  // Si no hay contador seleccionado
  if (!meterSeleccionado || meters.length === 0) {
    return (
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="max-w-2xl mx-auto"
      >
        <div className="bg-white/60 backdrop-blur-sm rounded-3xl p-8 md:p-12 text-center shadow-lg">
          <h2 className="text-2xl text-gray-800 mb-4">No hay contadores disponibles</h2>
          <p className="text-gray-600 mb-6">Agrega un contador para ver su información.</p>
          <button
            onClick={() => onNavigate?.('gestion-inmuebles')}
            className="bg-primary hover:bg-primary/90 text-white px-6 py-3 rounded-xl transition-all"
          >
            Agregar Contador
          </button>
        </div>
      </motion.div>
    )
  }

  // Mapear estado del contador a español
  const getEstadoTexto = (status: string) => {
    const estados: Record<string, string> = {
      'ACTIVE': 'Activo',
      'INACTIVE': 'Inactivo',
      'MAINTENANCE': 'Mantenimiento'
    }
    return estados[status] || status
  }

  // Mapear tipo de contador a español
  const getTipoTexto = (type: string) => {
    const tipos: Record<string, string> = {
      'SMART': 'Inteligente',
      'DIGITAL': 'Digital',
      'ANALOG': 'Análogo'
    }
    return tipos[type] || type
  }

  // Color del badge según estado
  const getEstadoColor = (status: string) => {
    const colores: Record<string, string> = {
      'ACTIVE': 'bg-emerald-500',
      'INACTIVE': 'bg-gray-500',
      'MAINTENANCE': 'bg-amber-500'
    }
    return colores[status] || 'bg-gray-500'
  }

  // Fecha de instalación (placeholder - idealmente vendría del backend)
  const fechaInstalacion = new Date().toLocaleDateString('es-ES', { 
    day: '2-digit', 
    month: 'long', 
    year: 'numeric' 
  })
  
  return (
    <motion.div 
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-4xl mx-auto space-y-4"
    >
      {/* Breadcrumbs */}
      {onNavigate && (
        <Breadcrumbs 
          items={[
            { label: 'Home', onClick: () => onNavigate('dashboard') },
            { label: 'Datos Técnicos' },
            { label: 'Mi Contador' }
          ]} 
        />
      )}

      {/* Title Section */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 md:gap-3">
          <div className="w-8 h-8 md:w-10 md:h-10 bg-primary rounded-full flex items-center justify-center">
            <Zap className="w-4 h-4 md:w-5 md:h-5 text-white" />
          </div>
          <h1 className="text-2xl md:text-3xl text-primary">Mi Contador</h1>
        </div>
        {onNavigate && (
          <button
            onClick={() => onNavigate('dashboard')}
            className="flex items-center gap-2 px-3 py-2 md:px-4 md:py-2 bg-secondary hover:bg-accent text-primary rounded-lg transition-colors text-sm md:text-base"
          >
            <ArrowLeft className="w-4 h-4" />
            Volver
          </button>
        )}
      </div>

      {/* Info Alert */}
      <div className="bg-blue-50 border border-blue-200 rounded-xl p-4 flex items-start gap-3">
        <Info className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
        <div className="flex-1">
          <p className="text-sm text-blue-900">
            Para modificar la información del contador, ve a{' '}
            <button
              onClick={() => onNavigate?.('configuracion')}
              className="text-primary hover:underline"
            >
              Ajustes
            </button>
            .
          </p>
        </div>
      </div>

      {/* Main Data Card */}
      <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-6 shadow-md">
        {/* Header con Estado */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-6 gap-3">
          <h2 className="text-2xl text-primary">Información del Contador</h2>
          <div className={`${getEstadoColor(meterSeleccionado.status)} text-white px-4 py-2 rounded-full flex items-center gap-2`}>
            <div className="w-2 h-2 bg-white rounded-full"></div>
            <span>{getEstadoTexto(meterSeleccionado.status)}</span>
          </div>
        </div>

        {/* Grid de Información */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Número de Serie */}
          <div className="bg-white border-2 border-slate-300 rounded-xl p-4">
            <div className="flex items-start gap-3">
              <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                <Hash className="w-5 h-5 text-primary" />
              </div>
              <div className="flex-1 min-w-0">
                <label className="block text-sm text-gray-600 mb-1">Número de Serie</label>
                <p className="text-gray-900 break-words">{meterSeleccionado.serialNumber}</p>
              </div>
            </div>
          </div>

          {/* Alias */}
          <div className="bg-white border-2 border-slate-300 rounded-xl p-4">
            <div className="flex items-start gap-3">
              <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                <Tag className="w-5 h-5 text-primary" />
              </div>
              <div className="flex-1 min-w-0">
                <label className="block text-sm text-gray-600 mb-1">Alias</label>
                <p className="text-gray-900 break-words">{meterSeleccionado.alias}</p>
              </div>
            </div>
          </div>

          {/* Dirección de Instalación */}
          <div className="bg-white border-2 border-slate-300 rounded-xl p-4 md:col-span-2">
            <div className="flex items-start gap-3">
              <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                <MapPin className="w-5 h-5 text-primary" />
              </div>
              <div className="flex-1 min-w-0">
                <label className="block text-sm text-gray-600 mb-1">Dirección de Instalación</label>
                <p className="text-gray-900 break-words">{meterSeleccionado.installationAddress}</p>
              </div>
            </div>
          </div>

          {/* Tipo de Contador */}
          <div className="bg-white border-2 border-slate-300 rounded-xl p-4">
            <div className="flex items-start gap-3">
              <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                <Activity className="w-5 h-5 text-primary" />
              </div>
              <div className="flex-1 min-w-0">
                <label className="block text-sm text-gray-600 mb-1">Tipo de Contador</label>
                <p className="text-gray-900">{getTipoTexto(meterSeleccionado.type)}</p>
              </div>
            </div>
          </div>

          {/* Fecha de Instalación */}
          <div className="bg-white border-2 border-slate-300 rounded-xl p-4">
            <div className="flex items-start gap-3">
              <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                <Calendar className="w-5 h-5 text-primary" />
              </div>
              <div className="flex-1 min-w-0">
                <label className="block text-sm text-gray-600 mb-1">Fecha de Instalación</label>
                <p className="text-gray-900">{fechaInstalacion}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Meter Illustration */}
        <div className="flex justify-center mt-8">
          <div className="relative">
            <div className="w-32 h-40 bg-primary rounded-2xl flex flex-col items-center justify-center p-4 shadow-lg">
              {/* Digital Display */}
              <div className="w-24 h-12 bg-gray-800 rounded-lg mb-2 flex items-center justify-center">
                <div className="grid grid-cols-6 gap-1 px-2">
                  {[...Array(6)].map((_, i) => (
                    <div key={i} className="w-2 h-8 bg-green-400 rounded"></div>
                  ))}
                </div>
              </div>
              
              {/* Second Display */}
              <div className="w-24 h-8 bg-gray-800 rounded-lg mb-2 flex items-center justify-center">
                <div className="grid grid-cols-5 gap-1 px-2">
                  {[...Array(5)].map((_, i) => (
                    <div key={i} className="w-2 h-5 bg-green-400 rounded"></div>
                  ))}
                </div>
              </div>

              {/* Power Indicator */}
              <div className="flex items-center gap-1 mb-1">
                <div className="w-3 h-1 bg-white rounded"></div>
                <Zap className="w-4 h-4 text-yellow-300" fill="yellow" />
                <div className="w-3 h-1 bg-white rounded"></div>
              </div>

              {/* Bottom Lights */}
              <div className="flex gap-1">
                <div className={`w-2 h-2 ${meterSeleccionado.status === 'ACTIVE' ? 'bg-green-400' : 'bg-gray-400'} rounded-full`}></div>
                <div className={`w-2 h-2 ${meterSeleccionado.status === 'MAINTENANCE' ? 'bg-amber-400' : 'bg-gray-400'} rounded-full`}></div>
                <div className={`w-2 h-2 ${meterSeleccionado.status === 'INACTIVE' ? 'bg-red-400' : 'bg-gray-400'} rounded-full`}></div>
              </div>
            </div>
          </div>
        </div>

        {/* Connect Button */}
        <div className="flex justify-center mt-6">
          <button className="bg-secondary hover:bg-accent text-primary px-6 py-3 rounded-xl transition-colors flex items-center gap-2 border-2 border-primary/30">
            <Wifi className="w-5 h-5" />
            Conectar al contador
          </button>
        </div>
      </div>
    </motion.div>
  )
}
