package com.ucdc.backend.application.dto.meter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ReadingDto(
        OffsetDateTime timestamp,
        BigDecimal kwhAccum
) {
}
