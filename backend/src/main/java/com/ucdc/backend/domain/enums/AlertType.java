package com.ucdc.backend.domain.enums;

public enum AlertType {
    QUOTA_OVERUSE,          // Excedió cuota/plan
    STATISTICAL_ANOMALY,    // Media + k·σ
    SPIKE,                  // % respecto al periodo anterior
    EARLY_THRESHOLD;        // Tu regla >=80% del umbral

    public boolean isStatistical() { return this == STATISTICAL_ANOMALY; }
    public boolean isQuota() { return this == QUOTA_OVERUSE; }
}
