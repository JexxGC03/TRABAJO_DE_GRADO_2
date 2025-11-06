package com.ucdc.backend.infrastructure.web.dto.meter;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MeterResponse(
        UUID id,
        String serialNumber,
        String provider,
        String serviceNumber,
        String installationAddress,
        String alias,
        OffsetDateTime createdAt
) {
}
