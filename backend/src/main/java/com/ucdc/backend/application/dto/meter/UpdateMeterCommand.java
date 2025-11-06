package com.ucdc.backend.application.dto.meter;

import com.ucdc.backend.domain.enums.MeterType;
import com.ucdc.backend.domain.enums.Provider;

import java.util.UUID;

public record UpdateMeterCommand(
        UUID userId,
        UUID meterId,
        String alias,
        String installationAddress,
        String serialNumber,
        Provider provider
) {
}
