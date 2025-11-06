package com.ucdc.backend.application.dto.quota;

import com.ucdc.backend.domain.model.MeterQuota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MeterQuotaResult(
        UUID meterId, MeterQuota.Periodicity periodicity, BigDecimal kwhLimit,
        OffsetDateTime validFrom, OffsetDateTime validTo
) {
}
