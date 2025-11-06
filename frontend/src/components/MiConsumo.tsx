import { useEffect, useRef, useState } from 'react'
import { Calendar, ArrowLeft } from 'lucide-react'
import { ComposedChart, Line, Bar, XAxis, YAxis, CartesianGrid, ResponsiveContainer } from 'recharts'
import { motion } from 'motion/react'
import { Breadcrumbs } from './Breadcrumbs'
import { useInmueble } from './InmuebleContext'
import { getConsumptionChart, type ConsumoData } from '../services/consumoService'

interface MiConsumoProps {
  onNavigate?: (view: string) => void
}

function fmt(d: Date) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}
function firstDayOfYear(anyYYYYMMDD: string) {
  const y = new Date(anyYYYYMMDD).getFullYear()
  return `${y}-01-01`
}
function lastDayOfYear(anyYYYYMMDD: string) {
  const y = new Date(anyYYYYMMDD).getFullYear()
  return `${y}-12-31`
}

export function MiConsumo({ onNavigate }: MiConsumoProps) {
  const { meterSeleccionado, meters } = useInmueble()
  const [viewType, setViewType] = useState<'Año' | 'Mes'>('Año')

  const [consumoData, setConsumoData] = useState<
    Array<{ month: string; consumption: number; cost?: number; kwh: number }>
  >([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string>('')

  // Fechas iniciales: últimos 7 días (se recalculan al cambiar a "Año")
  const today = new Date()
  const sevenAgo = new Date()
  sevenAgo.setDate(today.getDate() - 6)
  const [startDate, setStartDate] = useState<string>(fmt(sevenAgo))
  const [endDate, setEndDate] = useState<string>(fmt(today))

  const startRef = useRef<HTMLInputElement>(null)
  const endRef = useRef<HTMLInputElement>(null)

  // Cargar datos por vista
  useEffect(() => {
    const load = async () => {
      if (!meterSeleccionado) return
      setLoading(true)
      setError('')

      try {
        if (viewType === 'Año') {
          // Ajustar fechas al año completo
          const nowStr = fmt(new Date())
          const yFrom = firstDayOfYear(nowStr)
          const yTo = lastDayOfYear(nowStr)
          setStartDate(yFrom)
          setEndDate(yTo)

          const datos: ConsumoData[] = await getConsumptionChart(meterSeleccionado.id, {
            from: yFrom,
            to: yTo,
            granularity: 'MONTHLY',
          })
          setConsumoData(
            datos.map((d) => ({ month: d.fecha, consumption: d.kwh, cost: d.costo, kwh: d.kwh })),
          )
        } else {
          // Mes: últimos 30 días en DAILY
          const to = new Date()
          const from = new Date()
          from.setDate(to.getDate() - 30)
          const f = fmt(from)
          const t = fmt(to)
          setStartDate(f)
          setEndDate(t)

          const datos: ConsumoData[] = await getConsumptionChart(meterSeleccionado.id, {
            from: f,
            to: t,
            granularity: 'DAILY',
          })
          setConsumoData(
            datos.map((d) => ({ month: d.fecha, consumption: d.kwh, cost: d.costo, kwh: d.kwh })),
          )
        }
      } catch (e: any) {
        console.error('Error cargando consumo:', e)
        setError(e?.message || 'No se pudo cargar el consumo')
        setConsumoData([])
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
          <p className="text-gray-600 mb-6">Agrega un contador para ver tu consumo energético.</p>
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

  const openPicker = (ref: React.RefObject<HTMLInputElement>) => {
    const el = ref.current as (HTMLInputElement & { showPicker?: () => void }) | null
    if (!el) return
    if (typeof el.showPicker === 'function') el.showPicker()
    else el.focus()
  }

  // Forzar reglas cuando estás en "Año"
  const onChangeStart = (v: string) => {
    if (viewType === 'Año') {
      // Asegura primer/último día del MISMO año
      setStartDate(firstDayOfYear(v))
      setEndDate(lastDayOfYear(v))
    } else {
      setStartDate(v)
    }
  }
  const onChangeEnd = (v: string) => {
    if (viewType === 'Año') {
      setStartDate(firstDayOfYear(v))
      setEndDate(lastDayOfYear(v))
    } else {
      setEndDate(v)
    }
  }

  const handleMostrar = async () => {
    if (!meterSeleccionado) return
    setError('')

    if (!startDate || !endDate) {
      setError('Selecciona ambas fechas.')
      return
    }
    const from = new Date(startDate)
    const to = new Date(endDate)
    if (isNaN(from.getTime()) || isNaN(to.getTime())) {
      setError('Fechas inválidas.')
      return
    }
    if (from > to) {
      setError('La fecha inicial no puede ser mayor que la final.')
      return
    }

    const diffDays = Math.floor((to.getTime() - from.getTime()) / (1000 * 60 * 60 * 24)) + 1

    // Límites por pestaña
    if (viewType === 'Año' && diffDays > 366) {
      setError('En “Año” el rango máximo es de 365 días.')
      return
    }
    if (viewType === 'Mes' && diffDays > 31) {
      setError('En “Mes” el rango máximo es de 31 días.')
      return
    }

    setLoading(true)
    try {
      const datos = await getConsumptionChart(meterSeleccionado.id, {
        from: startDate,
        to: endDate,
        granularity: viewType === 'Año' ? 'MONTHLY' : 'DAILY',
      })
      setConsumoData(
        datos.map((d) => ({ month: d.fecha, consumption: d.kwh, cost: d.costo, kwh: d.kwh })),
      )
    } catch (e: any) {
      console.error(e)
      setError(e?.message || 'No se pudo cargar el consumo para el rango elegido.')
      setConsumoData([])
    } finally {
      setLoading(false)
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-6xl mx-auto space-y-3 md:space-y-4"
    >
      {/* Oculta el icono nativo del input date; deja solo nuestro icono azul */}
      <style>{`
        input[type="date"]::-webkit-calendar-picker-indicator { opacity: 0; }
        input[type="date"] { -webkit-appearance: none; }
      `}</style>

      {onNavigate && (
        <Breadcrumbs
          items={[
            { label: 'Home', onClick: () => onNavigate('dashboard') },
            { label: 'Consumo' },
            { label: 'Mi Consumo' },
          ]}
        />
      )}

      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 md:gap-3">
          <Calendar className="w-6 h-6 md:w-7 md:h-7 text-primary" />
          <h1 className="text-2xl md:text-3xl text-primary">Mi Consumo</h1>
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

      <div className="flex flex-col lg:grid lg:grid-cols-4 gap-4">
        {/* Gráfico */}
        <div className="lg:col-span-3 bg-white/60 backdrop-blur-sm rounded-2xl p-3 md:p-5 shadow-md">
          {/* Vistas rápidas */}
          <div className="flex flex-wrap gap-2 md:gap-3 mb-4">
            <button
              onClick={() => setViewType('Año')}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl transition-all shadow-sm ${
                viewType === 'Año' ? 'bg-primary text-white' : 'bg-slate-500 text-white hover:bg-slate-600'
              }`}
            >
              <Calendar className="w-4 h-4" />
              Año
            </button>
            <button
              onClick={() => setViewType('Mes')}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl transition-all shadow-sm ${
                viewType === 'Mes' ? 'bg-primary text-white' : 'bg-slate-500 text-white hover:bg-slate-600'
              }`}
            >
              <Calendar className="w-4 h-4" />
              Mes
            </button>
          </div>

          {/* Chart */}
          <div className="h-72 w-full">
            {loading ? (
              <div className="flex items-center justify-center h-full">
                <div className="text-center">
                  <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-3" />
                  <p className="text-gray-600">Cargando datos...</p>
                </div>
              </div>
            ) : consumoData.length === 0 ? (
              <div className="flex items-center justify-center h-full">
                <p className="text-gray-600">No hay datos de consumo disponibles</p>
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <ComposedChart data={consumoData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                  <XAxis dataKey="month" angle={-45} textAnchor="end" height={80} fontSize={12} />
                  <YAxis yAxisId="left" orientation="left" fontSize={12} />
                  <YAxis yAxisId="right" orientation="right" fontSize={12} />
                  <Bar yAxisId="left" dataKey="consumption" />
                  <Line yAxisId="right" type="monotone" dataKey="kwh" strokeWidth={2} dot={{ strokeWidth: 2, r: 4 }} />
                </ComposedChart>
              </ResponsiveContainer>
            )}
          </div>

          {error && (
            <div className="mt-3 px-3 py-2 rounded-md bg-red-50 border border-red-200 text-red-700 text-sm">
              {error}
            </div>
          )}
        </div>

        {/* Panel lateral: fechas + Mostrar */}
        <div className="space-y-4">
          <div>
            <label className="block text-primary mb-2">Fecha inicial</label>
            <div className="relative">
              <input
                ref={startRef}
                type="date"
                value={startDate}
                onChange={(e) => onChangeStart(e.target.value)}
                className="w-full bg-white/70 border-2 border-primary/30 rounded-xl px-3 py-2 pr-10 focus:border-primary focus:outline-none shadow-sm"
              />
              <button
                type="button"
                onClick={() => openPicker(startRef)}
                className="absolute right-3 top-2.5"
                aria-label="Abrir calendario"
              >
                <Calendar className="w-5 h-5 text-primary" />
              </button>
            </div>
          </div>

          <div>
            <label className="block text-primary mb-2">Fecha final</label>
            <div className="relative">
              <input
                ref={endRef}
                type="date"
                value={endDate}
                onChange={(e) => onChangeEnd(e.target.value)}
                className="w-full bg-white/70 border-2 border-primary/30 rounded-xl px-3 py-2 pr-10 focus:border-primary focus:outline-none shadow-sm"
              />
              <button
                type="button"
                onClick={() => openPicker(endRef)}
                className="absolute right-3 top-2.5"
                aria-label="Abrir calendario"
              >
                <Calendar className="w-5 h-5 text-primary" />
              </button>
            </div>
          </div>

          <button
            onClick={handleMostrar}
            className="w-full py-3 bg-primary text-white rounded-xl hover:opacity-90 transition-colors shadow-md"
          >
            Mostrar
          </button>
        </div>
      </div>
    </motion.div>
  )
}
