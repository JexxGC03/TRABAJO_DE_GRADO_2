package com.ucdc.backend.application.dto.meter;

import com.ucdc.backend.domain.enums.Provider;

import java.util.UUID;

public record RegisterMeterCommand(
        UUID userId,
        String serialNumber,
        Provider provider,
        String serviceNumber,
        String installationAddress,
        String alias
){}
