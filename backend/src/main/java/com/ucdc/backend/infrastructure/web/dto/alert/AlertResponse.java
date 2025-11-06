package com.ucdc.backend.infrastructure.web.dto.alert;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AlertResponse(
        String id,
        String meterId,
        BigDecimal thresholdKwh,
        BigDecimal currentKwh,
        String status,
        String type,
        String granularity,
        String period,          // "YYYY-MM"
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
