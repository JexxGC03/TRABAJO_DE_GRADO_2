package com.ucdc.backend.application.dto.meter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdateMeterReadingCommand(
        UUID meterId,
        OffsetDateTime timestamp,
        BigDecimal kwhAccum
) {
}
