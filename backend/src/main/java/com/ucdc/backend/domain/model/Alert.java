package com.ucdc.backend.domain.model;

import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.enums.AlertType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Objects;
import java.util.UUID;

public final class Alert {

    public enum Granularity { MONTHLY, DAILY }

    private final UUID id;
    private final UUID meterId;

    private BigDecimal thresholdKwh; // “esperado/umbral” (p.ej., mean+k·σ o cuota)
    private BigDecimal currentKwh;   // “observado”
    private AlertStatus status;
    private AlertType type;          // nuevo
    private String reason;           // nuevo (breve explicación)
    private Granularity granularity; // nuevo
    private YearMonth period;        // nuevo (para DAILY, puedes no usarlo o mapear yyyy-mm del día)

    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Alert(UUID id, UUID meterId,
                  BigDecimal thresholdKwh, BigDecimal currentKwh,
                  AlertStatus status, AlertType type, String reason,
                  Granularity granularity, YearMonth period,
                  OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = requireNonNull(id, "id");
        this.meterId = requireNonNull(meterId, "meterId");
        this.thresholdKwh = requireNonNegative(thresholdKwh, "thresholdKwh");
        this.currentKwh = requireNonNegative(currentKwh, "currentKwh");
        this.status = requireNonNull(status, "status");
        this.type = requireNonNull(type, "type");
        this.reason = (reason != null && reason.length() > 300) ? reason.substring(0, 300) : reason;
        this.granularity = requireNonNull(granularity, "granularity");
        this.period = requireNonNull(period, "period");
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    // Fábricas
    public static Alert create(UUID id, UUID meterId,
                               BigDecimal thresholdKwh, BigDecimal currentKwh,
                               AlertType type, String reason,
                               Granularity granularity, YearMonth period) {
        return new Alert(id, meterId, thresholdKwh, currentKwh, AlertStatus.ACTIVE,
                type, reason, granularity, period, null, null);
    }

    public static Alert rehydrate(UUID id, UUID meterId,
                                  BigDecimal thresholdKwh, BigDecimal currentKwh,
                                  AlertStatus status, AlertType type, String reason,
                                  Granularity granularity, YearMonth period,
                                  OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new Alert(id, meterId, thresholdKwh, currentKwh, status,
                type, reason, granularity, period, createdAt, updatedAt);
    }

    /* Reglas existentes */
    public boolean updateCurrentKwh(BigDecimal newCurrent) {
        this.currentKwh = requireNonNegative(newCurrent, "currentKwh");
        touch();
        return shouldEarlyWarn();
    }

    public boolean shouldEarlyWarn() {
        if (thresholdKwh.signum() == 0) return false;
        // current / threshold >= 0.8
        return currentKwh.multiply(BigDecimal.TEN)
                .compareTo(thresholdKwh.multiply(BigDecimal.valueOf(8))) >= 0;
    }

    public void resolve() {
        if (this.status == AlertStatus.RESOLVED) throw new IllegalStateException("Alert already resolved");
        this.status = AlertStatus.RESOLVED;
        touch();
    }

    public void changeThreshold(BigDecimal newThreshold) {
        this.thresholdKwh = requireNonNegative(newThreshold, "thresholdKwh");
        touch();
    }

    /* Getters */
    public UUID id() { return id; }
    public UUID meterId() { return meterId; }
    public BigDecimal thresholdKwh() { return thresholdKwh; }
    public BigDecimal currentKwh() { return currentKwh; }
    public AlertStatus status() { return status; }
    public AlertType type() { return type; }
    public String reason() { return reason; }
    public Granularity granularity() { return granularity; }
    public YearMonth period() { return period; }
    public OffsetDateTime createdAt() { return createdAt; }
    public OffsetDateTime updatedAt() { return updatedAt; }

    /* Utils */
    private void touch() { this.updatedAt = OffsetDateTime.now(); }
    private static <T> T requireNonNull(T v, String name) { return Objects.requireNonNull(v, name + " is required"); }
    private static BigDecimal requireNonNegative(BigDecimal v, String name) {
        if (v == null) throw new NullPointerException(name + " is required");
        if (v.signum() < 0) throw new IllegalArgumentException(name + " must be >= 0");
        return v;
    }

    @Override public boolean equals(Object o) { return (this == o) || (o instanceof Alert other && id.equals(other.id)); }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() {
        return "Alert{id=%s, type=%s, status=%s, current=%s/%s, period=%s}"
                .formatted(id, type, status, currentKwh, thresholdKwh, period);
    }
}
