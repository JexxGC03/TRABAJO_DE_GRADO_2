package com.ucdc.backend.domain.value;

import com.ucdc.backend.domain.model.MeterQuota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record MeterQuotaVersion(
        UUID meterId,
        MeterQuota.Periodicity periodicity,
        BigDecimal kwhLimit,
        OffsetDateTime validFrom,
        OffsetDateTime validTo
) {
    public MeterQuotaVersion {
        Objects.requireNonNull(meterId);
        Objects.requireNonNull(periodicity);
        Objects.requireNonNull(kwhLimit);
        if (kwhLimit.signum() < 0) throw new IllegalArgumentException("kwhLimit >= 0");
        Objects.requireNonNull(validFrom);
        Objects.requireNonNull(validTo);
        if (validTo.isBefore(validFrom)) throw new IllegalArgumentException("validTo >= validFrom");
    }
}
