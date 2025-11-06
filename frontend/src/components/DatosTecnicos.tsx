import { Settings, Plug, Zap, Home as HomeIcon, Calendar, FileText, ArrowLeft } from 'lucide-react'
import { motion } from 'motion/react'
import { Breadcrumbs } from './Breadcrumbs'
import { useInmueble } from './InmuebleContext'

interface DatosTecnicosProps {
  onNavigate?: (view: string) => void
}

export function DatosTecnicos({ onNavigate }: DatosTecnicosProps) {
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
          <p className="text-gray-600 mb-6">Agrega un contador para ver sus datos técnicos.</p>
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

  // Usar datos reales del contador
  const datosTecnicos = {
    tipoInstalacion: meterSeleccionado.type === 'SMART' ? 'Inteligente' : 'Estándar',
    tension: 'Baja 230V',
    contador: meterSeleccionado.serialNumber,
    fechaCorte: '1 de cada mes',
    numeroFactura: meterSeleccionado.serviceNumber,
    tipoSuministro: 'Residencial'
  }
  
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
            { label: 'Información' }
          ]} 
        />
      )}

      {/* Title Section */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 md:gap-3">
          <div className="w-8 h-8 md:w-10 md:h-10 bg-primary rounded-full flex items-center justify-center">
            <Settings className="w-4 h-4 md:w-5 md:h-5 text-white" />
          </div>
          <h1 className="text-2xl md:text-3xl text-primary">Datos técnicos</h1>
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

      {/* Main Content Card */}
      <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-6 shadow-md">
        {/* Grid of Information */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          {/* Tipo de Instalación */}
          <div>
            <label className="block text-gray-600 mb-3">Tipo de instalación</label>
            <div className="flex items-center gap-3 bg-white border-2 border-slate-300 rounded-xl px-4 py-3">
              <Plug className="w-5 h-5 text-primary" />
              <span className="text-gray-700">{datosTecnicos.tipoInstalacion}</span>
            </div>
          </div>

          {/* Tensión y Subtensión */}
          <div>
            <label className="block text-gray-600 mb-3">Tensión y Subtensión</label>
            <div className="flex items-center gap-3 bg-white border-2 border-slate-300 rounded-xl px-4 py-3">
              <Zap className="w-5 h-5 text-primary" />
              <span className="text-gray-700">{datosTecnicos.tension}</span>
            </div>
          </div>

          {/* Contador Instalado */}
          <div>
            <label className="block text-gray-600 mb-3">Contador Instalado</label>
            <div className="flex items-center gap-3 bg-white border-2 border-slate-300 rounded-xl px-4 py-3">
              <HomeIcon className="w-5 h-5 text-primary" />
              <span className="text-gray-700">{datosTecnicos.contador}</span>
            </div>
          </div>

          {/* Fecha de corte */}
          <div>
            <label className="block text-gray-600 mb-3">Fecha de corte</label>
            <div className="flex items-center gap-3 bg-white border-2 border-slate-300 rounded-xl px-4 py-3">
              <Calendar className="w-5 h-5 text-primary" />
              <span className="text-gray-700">{datosTecnicos.fechaCorte}</span>
            </div>
          </div>

          {/* Número de factura */}
          <div>
            <label className="block text-gray-600 mb-3">Número de factura</label>
            <div className="flex items-center gap-3 bg-white border-2 border-slate-300 rounded-xl px-4 py-3">
              <FileText className="w-5 h-5 text-primary" />
              <span className="text-gray-700">{datosTecnicos.numeroFactura}</span>
            </div>
          </div>
        </div>

        {/* House Icon Section */}
        <div className="flex flex-col items-center gap-4">
          {/* House Icon */}
          <div className="relative">
            <svg width="120" height="100" viewBox="0 0 120 100" className="drop-shadow-md">
              {/* House structure */}
              <polygon points="60,20 20,50 100,50" fill="#ef4444" />
              <rect x="30" y="50" width="60" height="40" fill="#fca5a5" />
              
              {/* Door */}
              <rect x="50" y="65" width="20" height="25" fill="#dc2626" rx="2" />
              <circle cx="65" cy="77" r="2" fill="#fef3c7" />
              
              {/* Windows */}
              <rect x="37" y="58" width="12" height="12" fill="#dbeafe" rx="1" />
              <line x1="43" y1="58" x2="43" y2="70" stroke="#60a5fa" strokeWidth="1" />
              <line x1="37" y1="64" x2="49" y2="64" stroke="#60a5fa" strokeWidth="1" />
              
              <rect x="71" y="58" width="12" height="12" fill="#dbeafe" rx="1" />
              <line x1="77" y1="58" x2="77" y2="70" stroke="#60a5fa" strokeWidth="1" />
              <line x1="71" y1="64" x2="83" y2="64" stroke="#60a5fa" strokeWidth="1" />
              
              {/* Chimney */}
              <rect x="72" y="25" width="8" height="15" fill="#b91c1c" />
            </svg>
          </div>

          {/* Suministro Label */}
          <div className="bg-white border-2 border-slate-300 rounded-xl px-6 py-3 flex items-center gap-3">
            <HomeIcon className="w-5 h-5 text-primary" />
            <span className="text-gray-700">Suministro {datosTecnicos.tipoSuministro}</span>
          </div>
        </div>
      </div>
    </motion.div>
  )
}
