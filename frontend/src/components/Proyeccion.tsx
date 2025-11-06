import { useEffect, useState } from 'react'
import { Calendar, TrendingUp, ArrowLeft } from 'lucide-react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, ResponsiveContainer, Legend } from 'recharts'
import { motion } from 'motion/react'
import { Breadcrumbs } from './Breadcrumbs'
import { useInmueble } from './InmuebleContext'
import { compareActualVsProjected } from '../services/consumoService'

interface ProyeccionProps {
  onNavigate?: (view: string) => void
}

export function Proyeccion({ onNavigate }: ProyeccionProps) {
  const { meterSeleccionado, meters } = useInmueble()
  const [viewType, setViewType] = useState<'Año' | 'Mes'>('Año')
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState<Array<{ month: string; actual: number; projected: number }>>([])

  useEffect(() => {
    const load = async () => {
      if (!meterSeleccionado) return
      setLoading(true)
      try {
        const now = new Date()
        const yyyy = now.getFullYear()
        const mm = String(now.getMonth() + 1).padStart(2, '0') // YearMonth requerido
        const periodYYYYMM = `${yyyy}-${mm}`

        const granularity = viewType === 'Año' ? 'MONTHLY' : 'DAILY'
        const model = 'LINEAR' // ajusta si tienes selección en UI

        const points = await compareActualVsProjected(meterSeleccionado.id, {
          periodYYYYMM,
          granularity,
          model,
        })

        // adaptar a la forma del chart
        setData(points.map(p => ({
          month: p.label,
          actual: p.actual,
          projected: p.projected,
        })))
      } catch (e) {
        console.error('Error cargando proyección:', e)
        setData([])
      } finally {
        setLoading(false)
      }
    }
    void load()
  }, [meterSeleccionado, viewType])

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
          <p className="text-gray-600 mb-6">Agrega un contador para ver proyecciones de consumo.</p>
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

  return (
    <motion.div 
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-6xl mx-auto space-y-4"
    >
      {onNavigate && (
        <Breadcrumbs 
          items={[
            { label: 'Home', onClick: () => onNavigate('dashboard') },
            { label: 'Consumo' },
            { label: 'Proyección' }
          ]} 
        />
      )}

      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 md:gap-3">
          <div className="w-8 h-8 md:w-10 md:h-10 bg-primary rounded-full flex items-center justify-center">
            <TrendingUp className="w-4 h-4 md:w-5 md:h-5 text-white" />
          </div>
          <h1 className="text-2xl md:text-3xl text-primary">Proyección</h1>
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

      <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-5 shadow-md">
        <div className="flex justify-center gap-3 mb-6">
          <button 
            onClick={() => setViewType('Año')}
            className={`flex items-center gap-2 px-6 py-3 rounded-xl transition-all ${
              viewType === 'Año' ? 'bg-primary text-white shadow-md' : 'bg-slate-200 text-slate-700 hover:bg-slate-300'
            }`}
          >
            <Calendar className="w-4 h-4" />
            Año
          </button>
          <button 
            onClick={() => setViewType('Mes')}
            className={`flex items-center gap-2 px-6 py-3 rounded-xl transition-all ${
              viewType === 'Mes' ? 'bg-primary text-white shadow-md' : 'bg-slate-200 text-slate-700 hover:bg-slate-300'
            }`}
          >
            <Calendar className="w-4 h-4" />
            Mes
          </button>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-4">
          <div className="lg:col-span-3">
            <div className="h-72 w-full">
              {loading ? (
                <div className="flex items-center justify-center h-full">
                  <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin" />
                </div>
              ) : (
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={data} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                    <XAxis dataKey="month" angle={-45} textAnchor="end" height={80} fontSize={12} stroke="#64748b" />
                    <YAxis fontSize={12} stroke="#64748b" />
                    <Legend />
                    <Line type="monotone" dataKey="actual"    name="Consumo Actual"     stroke="#ef4444"  strokeWidth={3} dot={{ fill: '#ef4444',  strokeWidth: 2, r: 5 }} />
                    <Line type="monotone" dataKey="projected" name="Consumo Proyectado" stroke="#f97316"  strokeWidth={3} dot={{ fill: '#f97316',  strokeWidth: 2, r: 5 }} />
                  </LineChart>
                </ResponsiveContainer>
              )}
            </div>
          </div>

          <div className="flex flex-col justify-center">
            <div className="bg-accent rounded-2xl p-6 text-center">
              <p className="text-primary italic leading-relaxed">
                Consumo proyectado según el modelo seleccionado
              </p>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  )
}
