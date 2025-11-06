import { Bell, Mail, Lightbulb, Droplet, Thermometer, Clock, Sun, AlertTriangle, Settings } from 'lucide-react'
import { motion } from 'motion/react'
import { useInmueble } from './InmuebleContext'
import { useEffect, useMemo, useState } from 'react'
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from './ui/dialog'
import { Slider } from './ui/slider'
import { getActiveQuota, updateQuota } from '../services/quotaService'
import { getMonthlyConsumption } from '../services/consumoService'
//import { getMyEmail } from '../services/userService'

const COP = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })

export function Alertas() {
  const { meterSeleccionado, meters } = useInmueble()
  const meterId = meterSeleccionado?.id ?? meters?.[0]?.id

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Datos reales
  const [limiteKwh, setLimiteKwh] = useState<number>(450)
  const [consumoActual, setConsumoActual] = useState<number>(0)
  const [correo, setCorreo] = useState<string>('')

  // UI dialog
  const [isLimitDialogOpen, setIsLimitDialogOpen] = useState(false)
  const [tempLimite, setTempLimite] = useState(450)

  // Tarifa (puedes extraerla a config si tu back la entrega)
  const tarifaPorKwh = 800

  // ===== CARGA INICIAL =====
  useEffect(() => {
  let ignore = false
  async function load() {
    if (!meterId) return
    setLoading(true)
    setError(null)
    try {
      const now = new Date()
      const period = `${now.getUTCFullYear()}-${String(now.getUTCMonth() + 1).padStart(2, '0')}`

      const [quota, kwhMes] = await Promise.all([
        getActiveQuota(meterId),
        getMonthlyConsumption(meterId, period),
      ])

      if (ignore) return
      setLimiteKwh(quota.kwhLimit ?? 0)
      setTempLimite(quota.kwhLimit ?? 0)
      setConsumoActual(kwhMes ?? 0)
    } catch (e) {
      console.error(e)
      if (!ignore) setError('No fue posible cargar tus datos de alertas.')
    } finally {
      if (!ignore) setLoading(false)
    }
  }
  load()
  return () => { ignore = true }
}, [meterId])

  // ===== DERIVADOS =====
  const porcentajeConsumo = useMemo(() => {
    if (!limiteKwh || limiteKwh <= 0) return 0
    return Math.min(100, Math.round((consumoActual / limiteKwh) * 100))
  }, [consumoActual, limiteKwh])

  const kwhRestantes = Math.max(0, limiteKwh - consumoActual)
  const alertStatus = porcentajeConsumo >= 80 ? 'Alerta activa' : porcentajeConsumo >= 60 ? 'Precaución' : 'Normal'
  const costoEstimado = tempLimite * tarifaPorKwh

  // ===== ACCIONES =====
  const handleOpenLimitDialog = () => {
    setTempLimite(limiteKwh)
    setIsLimitDialogOpen(true)
  }

  const handleSaveLimite = async () => {
    if (!meterId) return
    try {
      setSaving(true)
      const updated = await updateQuota(meterId, tempLimite)
      setLimiteKwh(updated.kwhLimit)
      setIsLimitDialogOpen(false)
    } catch (e) {
      console.error(e)
      alert('No se pudo guardar el límite. Intenta de nuevo.')
    } finally {
      setSaving(false)
    }
  }

  // ===== LAYOUT CENTRADO =====
  // - contenedor principal: max-w-4xl mx-auto
  // - el bloque lateral “Alertas” se reduce a sm:hidden (o shrink) para no empujar el grid
  // - márgenes/padding ajustados
  if (!meterId) {
    return (
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}
        className="max-w-xl mx-auto">
        <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-8 text-center shadow-lg">
          <h2 className="text-xl text-gray-800 mb-2">No hay contadores disponibles</h2>
          <p className="text-gray-600 mb-6">Agrega un contador para recibir alertas de consumo.</p>
          <a href="#" className="inline-block bg-primary hover:bg-primary/90 text-white px-5 py-2.5 rounded-xl">Ir a Configuración</a>
        </div>
      </motion.div>
    )
  }

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto space-y-4">
        <div className="h-28 bg-white/60 rounded-2xl animate-pulse" />
        <div className="h-80 bg-white/60 rounded-2xl animate-pulse" />
      </div>
    )
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-4xl mx-auto space-y-4"
    >
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl p-4">{error}</div>
      )}

      {/* Estado de Alertas (compacto y centrado) */}
      <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-5 shadow-md">
        <div className="flex items-start gap-4">
          <div className="flex-1">
            <h3 className="text-lg text-primary mb-3">Estado de Alertas</h3>

            {/* Barra de progreso */}
            <div className="relative w-full h-4 bg-slate-200/70 rounded-full overflow-hidden mb-3 shadow-inner">
              <div
                className={`absolute inset-y-0 left-0 ${
                  porcentajeConsumo >= 80
                    ? 'bg-gradient-to-r from-orange-400 to-red-500'
                    : porcentajeConsumo >= 60
                    ? 'bg-gradient-to-r from-yellow-400 to-orange-500'
                    : 'bg-gradient-to-r from-green-400 to-blue-500'
                }`}
                style={{ width: `${porcentajeConsumo}%` }}
              />
            </div>

            {/* Stats centradas */}
            <div className="grid grid-cols-3 text-center">
              <div>
                <div className="text-base text-gray-800">{limiteKwh} kWh</div>
                <div className="text-xs text-gray-600">Límite mensual</div>
              </div>
              <div>
                <div className="text-base text-gray-800">{consumoActual} kWh</div>
                <div className="text-xs text-gray-600">Consumido</div>
              </div>
              <div>
                <div className="text-base text-primary">{porcentajeConsumo}%</div>
                <div className="text-xs text-primary/80">Este mes llevas</div>
              </div>
            </div>
          </div>

          {/* Lado derecho: compacto, no desplaza el centro */}
          <div className="flex flex-col items-center gap-2 shrink-0">
            <div
              className={`w-12 h-12 ${
                porcentajeConsumo >= 80 ? 'bg-yellow-400' : porcentajeConsumo >= 60 ? 'bg-blue-400' : 'bg-green-400'
              } rounded-full flex items-center justify-center shadow`}
            >
              <AlertTriangle className="w-6 h-6 text-white" />
            </div>
            <span className="text-xs text-gray-700">{alertStatus}</span>
            <button
              onClick={handleOpenLimitDialog}
              className="flex items-center gap-1.5 px-3 py-1.5 bg-primary hover:bg-primary/90 text-white rounded-lg text-xs"
            >
              <Settings className="w-4 h-4" />
              Cambiar límite
            </button>
          </div>
        </div>
      </div>

      {/* Panel principal (centrado) */}
      <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-6 shadow-md">
        <h1 className="text-xl text-primary text-center mb-1">Tus Alertas de Consumo Energético</h1>
        <p className="text-gray-600 text-sm text-center mb-5">
          Recibe notificaciones cuando tu consumo se acerque al límite mensual y aprende cómo reducirlo.
        </p>

        {/* Tarjetas rápidas */}
        <div className="grid grid-cols-3 gap-3 mb-5">
          <InfoCard title="Límite mensual" value={`${limiteKwh} kWh/mes`} />
          <InfoCard title="Disponible" value={`${kwhRestantes} kWh`} />
          <InfoCard title="Estado" value={alertStatus} />
        </div>

        {/* Email */}
        <div className="flex items-center gap-2 bg-accent p-3 rounded-xl justify-center">
          <Mail className="w-4 h-4 text-primary" />
          <div className="text-sm">
            <span className="text-gray-700">Recibirás la alerta en tu correo registrado: </span>
            <span className="text-primary">{correo || '—'}</span>
          </div>
        </div>

        {/* Recomendaciones */}
        <div className="mt-6">
          <h2 className="text-lg text-gray-800 mb-3">Recomendaciones de Ahorro</h2>
          <div className="space-y-2">
            {RECS.map((rec, i) => {
              const Icon = rec.icon
              return (
                <div key={i} className="flex items-start gap-3 bg-white/80 p-3 rounded-xl border border-primary/20">
                  <Icon className={`w-5 h-5 ${rec.color} flex-shrink-0 mt-0.5`} />
                  <p className="text-gray-700 text-sm">{rec.text}</p>
                </div>
              )
            })}
          </div>
        </div>
      </div>

      {/* Dialog para ajustar límite */}
      <Dialog open={isLimitDialogOpen} onOpenChange={setIsLimitDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle className="text-xl text-primary">Ajustar Límite Mensual</DialogTitle>
            <DialogDescription>
              Modifica tu límite de consumo mensual y visualiza el costo estimado según tu configuración.
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-5 py-2">
            {/* Valor actual */}
            <div className="bg-gradient-to-r from-primary/10 to-primary/5 rounded-xl p-5 text-center">
              <div className="text-4xl text-primary mb-1">{tempLimite}</div>
              <div className="text-xs text-gray-600">kWh/mes</div>
            </div>

            {/* Slider */}
            <div className="space-y-3">
              <label className="text-sm text-gray-700">Desliza para ajustar tu límite (100 – 1000 kWh)</label>
              <Slider
                value={[tempLimite]}
                onValueChange={(v) => setTempLimite(v[0])}
                min={100}
                max={1000}
                step={10}
                className="w-full"
              />
              <div className="flex justify-between text-xs text-gray-500">
                <span>100 kWh</span>
                <span>1000 kWh</span>
              </div>
            </div>

            {/* Costo estimado */}
            <div className="bg-accent rounded-xl p-4 border-2 border-primary/20">
              <div className="flex items-center justify-between">
                <div>
                  <div className="text-sm text-gray-600 mb-1">Costo estimado mensual</div>
                  <div className="text-xs text-gray-500">Basado en tarifa de {COP.format(tarifaPorKwh)}/kWh</div>
                </div>
                <div className="text-2xl text-primary">{COP.format(costoEstimado)}</div>
              </div>
            </div>

            {/* Diferencia contra actual */}
            {tempLimite !== limiteKwh && (
              <div className="bg-blue-50 border border-blue-200 rounded-xl p-3 text-sm text-blue-900">
                {tempLimite > limiteKwh ? (
                  <>Aumentas <b>{tempLimite - limiteKwh} kWh</b> ({COP.format((tempLimite - limiteKwh) * tarifaPorKwh)} adicionales)</>
                ) : (
                  <>Reduces <b>{limiteKwh - tempLimite} kWh</b> (ahorras aprox. {COP.format((limiteKwh - tempLimite) * tarifaPorKwh)})</>
                )}
              </div>
            )}

            {/* Botones */}
            <div className="flex gap-3 pt-1">
              <button
                onClick={() => setIsLimitDialogOpen(false)}
                className="flex-1 px-4 py-2.5 bg-gray-200 hover:bg-gray-300 text-gray-800 rounded-xl"
                disabled={saving}
              >
                Cancelar
              </button>
              <button
                onClick={handleSaveLimite}
                className="flex-1 px-4 py-2.5 bg-primary hover:bg-primary/90 text-white rounded-xl"
                disabled={saving}
              >
                {saving ? 'Guardando…' : 'Guardar Límite'}
              </button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </motion.div>
  )
}

/* ========== Auxiliares ========== */
function InfoCard({ title, value }: { title: string; value: string }) {
  return (
    <div className="bg-white/80 rounded-xl p-4 border-2 border-primary/20 shadow-sm text-center">
      <div className="text-primary text-sm mb-0.5">{title}</div>
      <div className="text-gray-800">{value}</div>
    </div>
  )
}

const RECS = [
  { icon: Lightbulb, text: 'Usa bombillas LED en lugar de incandescentes', color: 'text-yellow-600' },
  { icon: Droplet, text: 'Desconecta cargadores y aparatos en desuso', color: 'text-primary' },
  { icon: Thermometer, text: 'Regula la nevera: mantén temperatura entre 2°C y 5°C', color: 'text-red-500' },
  { icon: Clock, text: 'Limita el uso del aire acondicionado', color: 'text-primary' },
  { icon: Sun, text: 'Aprovecha la luz natural y reduce el uso de iluminación artificial', color: 'text-orange-500' },
]
