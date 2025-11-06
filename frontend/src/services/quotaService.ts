// src/services/quotaService.ts (UPDATED)
import api from './api'

export type QuotaResponse = {
  kwhLimit: number;
  validFrom?: string;
  validTo?: string;
  status?: string;
};

export type QuotaPeriodicity = "MONTHLY" | "DAILY"; 

export async function getActiveQuota(meterId: string): Promise<QuotaResponse> {
  const urlActive = `/meters/${meterId}/quota/active`;
  try {
    const { data } = await api.get(urlActive);
    return normalizeQuota(data);
  } catch (e: any) {
    // Algunos back exponen /quota sin /active
    const fallback = `/meters/${meterId}/quota`;
    const { data } = await api.get(fallback);
    return normalizeQuota(data);
  }
}


/** Crea o actualiza el límite mensual (kWh) del medidor. */
export async function updateQuota(
  meterId: string,
  kwhLimit: number,
  periodicity: QuotaPeriodicity = "MONTHLY"
): Promise<QuotaResponse> {
  const payload = { kwhLimit: Number(kwhLimit), periodicity }; // 👈 ahora enviamos periodicity
  const { data } = await api.put(`/meters/${meterId}/quota`, payload, {
    headers: { "Content-Type": "application/json" },
  });
  return normalizeQuota(data);
}

function normalizeQuota(data: any): QuotaResponse {
  const kwhLimit = Number(
    data?.kwhLimit ?? data?.limit ?? data?.monthlyLimitKwh ?? data?.kwh ?? 0
  );
  return {
    kwhLimit,
    validFrom: data?.validFrom ?? data?.start ?? data?.from ?? undefined,
    validTo: data?.validTo ?? data?.end ?? data?.to ?? undefined,
    status: data?.status ?? undefined,
  };
}
