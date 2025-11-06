package com.ucdc.backend.application.dto.meter;

import com.ucdc.backend.domain.enums.MeterStatus;
import com.ucdc.backend.domain.enums.Provider;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegisterMeterResult(
        UUID id,
        String serialNumber,
        Provider provider,
        String serviceNumber,
        String installationAddress,
        String alias,
        OffsetDateTime createdAt
) {
}
