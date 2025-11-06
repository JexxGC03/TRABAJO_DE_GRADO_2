import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react'
import { listMyMeters } from '../services/meterService' // <- ahora usamos el servicio real

export interface Meter {
  id: string
  alias: string
  serialNumber?: string
  type?: string
  provider?: string
  installationAddress?: string
  status?: string
}

interface MeterContextType {
  meterSeleccionado: Meter | null
  setMeterSeleccionado: (meter: Meter | null) => void
  meters: Meter[]
  agregarMeter: (meter: Omit<Meter, 'id'> | Meter) => void
  eliminarMeter: (id: string) => void
  actualizarMeter: (id: string, datos: Omit<Meter, 'id'>) => void
  loading: boolean
  recargarMeters: () => Promise<void>
}

const InmuebleContext = createContext<MeterContextType | undefined>(undefined)

export function InmuebleProvider({ children }: { children: ReactNode }) {
  const [meters, setMeters] = useState<Meter[]>([])
  const [meterSeleccionado, setMeterSeleccionado] = useState<Meter | null>(null)
  const [loading, setLoading] = useState<boolean>(true)

  const recargarMeters = async () => {
    setLoading(true)
    try {
      const metersFromAPI = await listMyMeters()  // <- GET /api/my/meters
      setMeters(metersFromAPI ?? [])

      // Mantén el seleccionado si aún existe; si no, toma el primero; si está vacío, null
      setMeterSeleccionado(prev => {
        if (!metersFromAPI || metersFromAPI.length === 0) return null
        if (prev && metersFromAPI.find(m => m.id === prev.id)) return prev
        return metersFromAPI[0]
      })
    } catch (e) {
      console.error('❌ Error cargando contadores:', e)
      setMeters([])
      setMeterSeleccionado(null)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void recargarMeters()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const agregarMeter = (meter: Omit<Meter, 'id'> | Meter) => {
    const nuevo: Meter = { id: (meter as Meter).id ?? crypto.randomUUID(), ...meter } as Meter
    setMeters(prev => {
      const lista = [...prev, nuevo]
      if (!meterSeleccionado) setMeterSeleccionado(nuevo)
      return lista
    })
  }

  const eliminarMeter = (id: string) => {
    setMeters(prev => {
      const nuevos = prev.filter(m => m.id !== id)
      if (meterSeleccionado?.id === id) setMeterSeleccionado(nuevos[0] ?? null)
      return nuevos
    })
  }

  const actualizarMeter = (id: string, datos: Omit<Meter, 'id'>) => {
    setMeters(prev => {
      const nuevos = prev.map(m => (m.id === id ? { ...m, ...datos } : m))
      if (meterSeleccionado?.id === id) {
        const actualizado = nuevos.find(m => m.id === id) ?? null
        setMeterSeleccionado(actualizado)
      }
      return nuevos
    })
  }

  return (
    <InmuebleContext.Provider
      value={{
        meterSeleccionado,
        setMeterSeleccionado,
        meters,
        agregarMeter,
        eliminarMeter,
        actualizarMeter,
        loading,
        recargarMeters,
      }}
    >
      {children}
    </InmuebleContext.Provider>
  )
}

export function useInmueble(): MeterContextType {
  const ctx = useContext(InmuebleContext)
  if (!ctx) throw new Error('useInmueble must be used within InmuebleProvider')
  return ctx
}
