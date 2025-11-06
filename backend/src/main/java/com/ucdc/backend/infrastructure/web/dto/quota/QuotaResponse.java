package com.ucdc.backend.infrastructure.web.dto.quota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record QuotaResponse(
        String quotaId,
        String meterId,
        String periodicity,
        BigDecimal kwhLimit,
        OffsetDateTime validFrom,
        OffsetDateTime validTo
) {
}
