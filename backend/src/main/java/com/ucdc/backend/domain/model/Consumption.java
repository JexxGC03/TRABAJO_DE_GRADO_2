package com.ucdc.backend.domain.model;

import com.ucdc.backend.domain.enums.ConsumptionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Consumo agregado en una ventana (minuto, hora, día, mes).
 * periodStart representa el inicio exacto de la ventana.
 */
public final class Consumption {
    private final UUID id;
    private final UUID meterId;
    private final ConsumptionType type;
    private final OffsetDateTime periodStart;
    private BigDecimal kwh; // total de la ventana (>= 0)

    private Consumption(UUID id,
                        UUID meterId,
                        ConsumptionType type,
                        OffsetDateTime periodStart,
                        BigDecimal kwh) {
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.meterId = Objects.requireNonNull(meterId, "meterId is required");
        this.type = Objects.requireNonNull(type, "type is required");
        this.periodStart = Objects.requireNonNull(periodStart, "periodStart is required");
        setKwh(kwh);

    }

    public static Consumption create(UUID meterId,
                                     ConsumptionType type,
                                     OffsetDateTime periodStart,
                                     BigDecimal kwh) {
        return new Consumption(
                null,            // id → generado por JPA
                meterId,
                type,
                periodStart,
                kwh
        );
    }

    /* Fábricas por tipo */
    public static Consumption minutely(UUID id, UUID meterId, OffsetDateTime minuteStart, BigDecimal kwh) {
        return new Consumption(id, meterId, ConsumptionType.MINUTELY, minuteStart, kwh);
    }
    public static Consumption hourly(UUID id, UUID meterId, OffsetDateTime hourStart, BigDecimal kwh) {
        return new Consumption(id, meterId, ConsumptionType.HOURLY, hourStart, kwh);
    }
    public static Consumption daily(UUID id, UUID meterId, OffsetDateTime dayStart, BigDecimal kwh) {
        return new Consumption(id, meterId, ConsumptionType.DAILY, dayStart, kwh);
    }
    public static Consumption monthly(UUID id, UUID meterId, OffsetDateTime monthStart, BigDecimal kwh) {
        return new Consumption(id, meterId, ConsumptionType.MONTHLY, monthStart, kwh);
    }

    /* Comportamiento */
    public void add(BigDecimal deltaKwh) {
        if (deltaKwh == null) return;
        if (deltaKwh.signum() < 0) throw new IllegalArgumentException("deltaKwh must be >= 0");
        this.kwh = this.kwh.add(deltaKwh);
    }

    public void setKwh(BigDecimal newKwh) {
        Objects.requireNonNull(newKwh, "kwh is required");
        if (newKwh.signum() < 0) throw new IllegalArgumentException("kwh must be >= 0");
        this.kwh = newKwh;
    }

    /* Consultas de apoyo */
    public boolean isMinutely() { return type == ConsumptionType.MINUTELY; }
    public boolean isHourly()   { return type == ConsumptionType.HOURLY; }
    public boolean isDaily()    { return type == ConsumptionType.DAILY; }
    public boolean isMonthly()  { return type == ConsumptionType.MONTHLY; }

    /* Getters */
    public UUID getId() { return id; }
    public UUID getMeterId() { return meterId; }
    public ConsumptionType getType() { return type; }
    public OffsetDateTime getPeriodStart() { return periodStart; }
    public BigDecimal getKwh() { return kwh; }

    /* Igualdad por identidad */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consumption c)) return false;
        return id.equals(c.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() {
        return "Consumption{id=%s, meterId=%s, type=%s, start=%s, kwh=%s}"
                .formatted(id, meterId, type, periodStart, kwh);
    }
}
