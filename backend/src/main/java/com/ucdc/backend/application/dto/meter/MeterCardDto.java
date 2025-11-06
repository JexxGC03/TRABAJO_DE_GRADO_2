package com.ucdc.backend.application.dto.meter;

import com.ucdc.backend.domain.enums.MeterStatus;
import com.ucdc.backend.domain.enums.MeterType;
import com.ucdc.backend.domain.enums.Provider;

import java.util.UUID;

public record MeterCardDto(
        UUID id,
        String alias,
        String installationAddress,
        String serialNumber,
        MeterStatus status,
        MeterType type,
        Provider provider
) {
}
