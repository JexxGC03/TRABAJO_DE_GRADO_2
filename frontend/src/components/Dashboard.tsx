import { BarChart3, LineChart, Settings, Zap } from 'lucide-react'
import { motion } from 'motion/react'
import { ImageWithFallback } from './figma/ImageWithFallback'
import { useInmueble } from './InmuebleContext'

interface DashboardProps {
  onNavigate: (view: string) => void
}

export function Dashboard({ onNavigate }: DashboardProps) {
  const { meterSeleccionado, meters } = useInmueble()
  
  // Si no hay contadores, mostrar pantalla vacía
  if (!meterSeleccionado || meters.length === 0) {
    return (
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="max-w-2xl mx-auto"
      >
        <div className="bg-white/60 backdrop-blur-sm rounded-3xl p-8 md:p-12 text-center shadow-lg">
          <div className="w-20 h-20 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-6">
            <Zap className="w-10 h-10 text-primary" />
          </div>
          <h2 className="text-2xl md:text-3xl text-gray-800 mb-4">No hay contadores asignados</h2>
          <p className="text-gray-600 mb-8">
            Para comenzar a monitorear tu consumo energético, necesitas agregar un contador.
          </p>
          <button
            onClick={() => onNavigate('gestion-inmuebles')}
            className="bg-primary hover:bg-primary/90 text-white px-8 py-3 rounded-xl transition-all duration-300 shadow-lg hover:shadow-xl inline-flex items-center gap-2"
          >
            <Settings className="w-5 h-5" />
            Agregar Contador
          </button>
        </div>
      </motion.div>
    )
  }
  
  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-5xl mx-auto space-y-4 md:space-y-6"
    >
      {/* Welcome Title */}
      <div className="text-center">
        <h1 className="text-2xl md:text-4xl text-primary mb-2 md:mb-3">Bienvenido a tu Suministro</h1>
        <p className="text-base md:text-lg text-primary/80">{meterSeleccionado.alias}</p>
      </div>

      {/* Main Content Grid */}
      <div className="flex flex-col lg:grid lg:grid-cols-2 gap-4 md:gap-6">
        {/* Consumo Card */}
        <motion.div 
          whileHover={{ scale: 1.02 }}
          transition={{ duration: 0.2 }}
          className="bg-white/60 backdrop-blur-sm rounded-2xl md:rounded-3xl overflow-hidden shadow-md"
        >
          {/* Image Header */}
          <div className="relative h-32 md:h-40 overflow-hidden">
            <ImageWithFallback
              src="https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=600&h=300&fit=crop"
              alt="Consumo Energético"
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-primary/80 to-transparent"></div>
            <div className="absolute bottom-3 left-4 md:bottom-4 md:left-6 flex items-center gap-2 md:gap-3">
              <div className="w-10 h-10 md:w-12 md:h-12 bg-white rounded-full flex items-center justify-center shadow-lg">
                <BarChart3 className="w-5 h-5 md:w-6 md:h-6 text-primary" />
              </div>
              <h2 className="text-xl md:text-2xl text-white">Consumo</h2>
            </div>
          </div>

          <div className="p-4 md:p-6">
            <p className="text-xs md:text-sm text-gray-600 mb-3 md:mb-4">
              Consulta tu gasto energético actual, pasado y proyectado
            </p>

            <div className="flex flex-col gap-2 md:gap-3">
              <button
                onClick={() => onNavigate('consumo')}
                className="w-full bg-secondary hover:bg-accent text-primary px-3 py-2 md:px-4 md:py-3 rounded-xl transition-all flex items-center justify-between group"
              >
                <div className="flex items-center gap-2">
                  <BarChart3 className="w-4 h-4 md:w-5 md:h-5" />
                  <span className="text-sm md:text-base">Mi Consumo</span>
                </div>
                <span className="text-xs group-hover:translate-x-1 transition-transform">→</span>
              </button>

              <button
                onClick={() => onNavigate('proyeccion')}
                className="w-full bg-secondary hover:bg-accent text-primary px-3 py-2 md:px-4 md:py-3 rounded-xl transition-all flex items-center justify-between group"
              >
                <div className="flex items-center gap-2">
                  <LineChart className="w-4 h-4 md:w-5 md:h-5" />
                  <span className="text-sm md:text-base">Proyección</span>
                </div>
                <span className="text-xs group-hover:translate-x-1 transition-transform">→</span>
              </button>
            </div>
          </div>
        </motion.div>

        {/* Datos Técnicos Card */}
        <motion.div 
          whileHover={{ scale: 1.02 }}
          transition={{ duration: 0.2 }}
          className="bg-white/60 backdrop-blur-sm rounded-2xl md:rounded-3xl overflow-hidden shadow-md"
        >
          {/* Image Header */}
          <div className="relative h-32 md:h-40 overflow-hidden">
            <ImageWithFallback
              src="https://images.unsplash.com/photo-1473341304170-971dccb5ac1e?w=600&h=300&fit=crop"
              alt="Datos Técnicos"
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-primary/80 to-transparent"></div>
            <div className="absolute bottom-3 left-4 md:bottom-4 md:left-6 flex items-center gap-2 md:gap-3">
              <div className="w-10 h-10 md:w-12 md:h-12 bg-white rounded-full flex items-center justify-center shadow-lg">
                <Settings className="w-5 h-5 md:w-6 md:h-6 text-primary" />
              </div>
              <h2 className="text-xl md:text-2xl text-white">Datos Técnicos</h2>
            </div>
          </div>

          <div className="p-4 md:p-6">
            <p className="text-xs md:text-sm text-gray-600 mb-3 md:mb-4">
              Accede a la información completa de tu contador
            </p>

            <div className="flex flex-col gap-2 md:gap-3">
              <button
                onClick={() => onNavigate('contador')}
                className="w-full bg-secondary hover:bg-accent text-primary px-3 py-2 md:px-4 md:py-3 rounded-xl transition-all flex items-center justify-between group"
              >
                <div className="flex items-center gap-2">
                  <Zap className="w-4 h-4 md:w-5 md:h-5" />
                  <span className="text-sm md:text-base">Mi Contador</span>
                </div>
                <span className="text-xs group-hover:translate-x-1 transition-transform">→</span>
              </button>
            </div>
          </div>
        </motion.div>
      </div>
    </motion.div>
  )
}
