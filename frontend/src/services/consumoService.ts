// src/services/consumoService.ts
import api from './api'

/** Granularidades soportadas por tu backend */
export type ChartGranularity = 'DAILY' | 'MONTHLY'
export type ProjectionModel = 'LINEAR' | 'SEASONAL' | 'ARIMA' | 'HOLT_WINTERS' | string

export type ConsumoData = {
  /** Etiqueta legible (día/mes) */
  fecha: string
  /** kWh del periodo */
  kwh: number
  /** Costo (si lo trae el back) */
  costo?: number
}


function ymNowUTC(): string {
  const d = new Date()
  const y = d.getUTCFullYear()
  const m = String(d.getUTCMonth() + 1).padStart(2, '0')
  return `${y}-${m}` // yyyy-MM
}

/** Devuelve el consumo mensual (kWh) como número. SIEMPRE envía ?period=yyyy-MM */
export async function getMonthlyConsumption(meterId: string, periodISO?: string): Promise<number> {
  const period = periodISO ?? ymNowUTC()

  // 🔒 Forzamos el query en la URL (sin confiar en { params })
  const url = `/meters/${meterId}/consumption/monthly?period=${encodeURIComponent(period)}`
  const res = await api.get(url)

  const data: any = (res as any)?.data ?? res // por si tu wrapper no usa axios puro

  const n =
    Number(data?.totalKwh) ??
    Number(data?.kwh) ??
    Number(data?.total) ??
    (typeof data === 'number' ? data : 0)

  return Number.isFinite(n) ? n : 0
}

/** Alias seguro: si el mensual ya entrega un número, solo lo retorna. */
export async function getMonthlyTotalKwh(meterId: string, periodYYYYMM: string): Promise<number> {
  return getMonthlyConsumption(meterId, periodYYYYMM)
}

/** === ANNUAL: /api/meters/{id}/consumption/annual?year=YYYY === */
export async function getAnnualConsumption(
  meterId: string,
  year: number
): Promise<ConsumoData[]> {
  const { data, error } = await api.get<any>(`/meters/${meterId}/consumption/annual?year=${year}`)
  if (error) throw new Error(error)

  const items: any[] = data?.items || data?.points || data?.months || data || []
  return items.map((it) => ({
    fecha: it.label ?? it.month ?? it.date ?? it.ts ?? '',
    kwh:   it.kwh  ?? it.value ?? it.amount ?? 0,
    costo: it.cost ?? it.costo,
  }))
}

/** === CHART (solo MONTHLY o DAILY): /api/meters/{id}/consumption/chart?... === */
export async function getConsumptionChart(
  meterId: string,
  params: { from: string; to: string; granularity: ChartGranularity }
): Promise<ConsumoData[]> {
  const q = new URLSearchParams({
    from: params.from,
    to: params.to,
    granularity: params.granularity, // 'MONTHLY' | 'DAILY'
  }).toString()

  const { data, error } = await api.get<any>(`/meters/${meterId}/consumption/chart?${q}`)
  if (error) throw new Error(error)

  const items: any[] = data?.items || data?.points || data?.series || data || []
  return items.map((it) => ({
    fecha: it.label ?? it.bucket ?? it.date ?? it.ts ?? '',
    kwh:   it.kwh  ?? it.value ?? it.amount ?? 0,
    costo: it.cost ?? it.costo,
  }))
}

/** === COMPARE: /api/meters/{id}/consumption/compare?... (granularity solo MONTHLY o DAILY) === */
export async function compareActualVsProjected(
  meterId: string,
  params: { periodYYYYMM: string; granularity: ChartGranularity; model: ProjectionModel }
): Promise<Array<{ label: string; actual: number; projected: number }>> {
  const q = new URLSearchParams({
    period: params.periodYYYYMM,
    granularity: params.granularity, // 'MONTHLY' | 'DAILY'
    model: params.model,
  }).toString()

  const { data, error } = await api.get<any>(`/meters/${meterId}/consumption/compare?${q}`)
  if (error) throw new Error(error)

  const items: any[] = data?.items || data?.points || data?.series || data || []
  return items.map((it) => ({
    label:     it.label ?? it.bucket ?? it.date ?? it.ts ?? '',
    actual:    it.actual ?? it.real ?? it.kwh ?? it.value ?? 0,
    projected: it.projected ?? it.projection ?? it.forecast ?? 0,
  }))
}

/**
 * Helper para tu UI:
 *  - 'mes'     -> YEAR actual, granularidad MONTHLY
 *  - 'semana'  -> últimos 7 días, granularidad DAILY (tu back no soporta WEEKLY)
 *  - 'dia'     -> últimos 30 días, granularidad DAILY
 */
export async function getConsumoHistorico(
  meterId: string,
  periodo: 'mes' | 'semana' | 'dia'
): Promise<ConsumoData[]> {
  const now = new Date()

  if (periodo === 'mes') {
    const year = now.getFullYear()
    return getConsumptionChart(meterId, {
      from: `${year}-01-01`,
      to:   `${year}-12-31`,
      granularity: 'MONTHLY',
    })
  }

  if (periodo === 'semana') {
    const to = now.toISOString().slice(0, 10)
    const d = new Date(now)
    d.setDate(d.getDate() - 6) // últimos 7 días (incluye hoy)
    const from = d.toISOString().slice(0, 10)
    return getConsumptionChart(meterId, { from, to, granularity: 'DAILY' })
  }

  // 'dia' -> últimos 30 días en DAILY
  const to = now.toISOString().slice(0, 10)
  const d = new Date(now)
  d.setDate(d.getDate() - 30)
  const from = d.toISOString().slice(0, 10)
  return getConsumptionChart(meterId, { from, to, granularity: 'DAILY' })
}


