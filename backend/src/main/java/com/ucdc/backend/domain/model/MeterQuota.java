package com.ucdc.backend.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class MeterQuota {

    private final UUID id;
    private final UUID meterId;
    private Periodicity periodicity; // MONTHLY, DAILY (extensible)
    private BigDecimal kwhLimit;     // ≥ 0
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;  // null = vigente

    private MeterQuota(UUID id, UUID meterId, Periodicity periodicity,
                       BigDecimal kwhLimit, OffsetDateTime validFrom, OffsetDateTime validTo) {
        this.id = Objects.requireNonNull(id);
        this.meterId = Objects.requireNonNull(meterId);
        this.periodicity = Objects.requireNonNull(periodicity);
        if (kwhLimit == null || kwhLimit.signum() < 0) throw new IllegalArgumentException("kwhLimit >= 0");
        this.kwhLimit = kwhLimit;
        this.validFrom = Objects.requireNonNull(validFrom);
        this.validTo = validTo;
    }

    public static MeterQuota create(UUID id, UUID meterId, Periodicity p, BigDecimal limitKwh) {
        return new MeterQuota(id, meterId, p, limitKwh, OffsetDateTime.now(), null);
    }

    public static MeterQuota rehydrate(UUID id, UUID meterId, Periodicity periodicity, BigDecimal kwhLimit, OffsetDateTime validFrom, OffsetDateTime validTo) {
        return new MeterQuota(id, meterId, periodicity, kwhLimit, validFrom, validTo);
    }

    public MeterQuota close(OffsetDateTime untilExclusive) {
        if (validTo != null) throw new IllegalStateException("Already closed");
        if (!untilExclusive.isAfter(validFrom)) throw new IllegalArgumentException("validTo after validFrom");
        return new MeterQuota(id, meterId, periodicity, kwhLimit, validFrom, untilExclusive);
    }

    public boolean isActive(OffsetDateTime at) {
        return !at.isBefore(validFrom) && (validTo == null || at.isBefore(validTo));
    }
    public enum Periodicity { MONTHLY, DAILY }

    public UUID id() { return id; }
    public UUID meterId() { return meterId; }
    public Periodicity periodicity() { return periodicity; }
    public BigDecimal kwhLimit() { return kwhLimit; }
    public OffsetDateTime validFrom() { return validFrom; }
    public OffsetDateTime validTo() { return validTo; }
}
