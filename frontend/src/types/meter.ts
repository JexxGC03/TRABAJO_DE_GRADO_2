// src/types/meter.ts
export interface MeterItemResponse {
  id: string;
  serialNumber: string;
  provider: string;
  serviceNumber: string;
  installationAddress: string;
  alias: string | null;
  status: string;
  type: string;
}

export interface RegisterMeterRequest {
  serialNumber: string;
  serviceNumber: string;
  installationAddress: string;
  provider: string;       // e.g. "ENEL"
  alias?: string | null;  // opcional
}

export interface MeterUpdateRequest {
  serialNumber: string;
  serviceNumber: string;
  installationAddress: string;
  provider: string;       // e.g. "ENEL"
  alias?: string | null;
}
