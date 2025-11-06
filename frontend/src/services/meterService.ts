// src/services/meterService.ts
import api from './api'

/** === Tipos expuestos por el backend (ajusta si difieren) === */
export type MeterItemResponse = {
  id: string
  alias: string
  serialNumber: string
  type: string            // p.ej. "SMART"
  provider: string        // p.ej. "ENEL"
  installationAddress: string
  status: string          // p.ej. "ACTIVE"
}

export type RegisterMeterRequest = {
  alias: string
  serialNumber: string
  serviceNumber: string
  installationAddress: string
  provider: string        // Debe coincidir con enum del backend
  type: string            // Debe coincidir con enum del backend
}

export type UpdateMeterRequest = Partial<{
  alias: string
  installationAddress: string
  provider: string
  type: string
  status: string
}>

export type MeterResponse = MeterItemResponse & {
  serviceNumber?: string
  createdAt?: string
  updatedAt?: string
}

/** === Listar MIS medidores === GET /api/my/meters */
export async function listMyMeters(): Promise<MeterItemResponse[]> {
  const { data, error } = await api.get<MeterItemResponse[]>('/my/meters')
  if (error) throw new Error(error)
  return data ?? []
}

/** === Crear medidor === POST /api/meters */
export async function createMeter(payload: RegisterMeterRequest): Promise<MeterResponse> {
  const { data, error } = await api.post<MeterResponse>('/meters', payload)
  if (error) throw new Error(error)
  return data as MeterResponse
}

/** === Actualizar medidor === PUT /api/meters/{id} (si tu API es PATCH, usa api.patch) */
export async function updateMeter(id: string, payload: UpdateMeterRequest): Promise<MeterResponse> {
  const { data, error } = await api.put<MeterResponse>(`/meters/${id}`, payload)
  if (error) throw new Error(error)
  return data as MeterResponse
}

/** === Eliminar medidor === DELETE /api/meters/{id} */
export async function deleteMeter(id: string): Promise<void> {
  const { error } = await api.delete<void>(`/meters/${id}`)
  if (error) throw new Error(error)
}
