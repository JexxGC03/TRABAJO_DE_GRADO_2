package com.ucdc.backend.infrastructure.web.dto.meter;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record   MeterItemResponse(
        @Schema(example = "a01b2edc-51e9-45d2-8b34-f126c36e9c8d")
        UUID id,

        @Schema(example = "SNR-ENE-00123")
        String serialNumber,

        @Schema(example = "ENEL")
        String provider,

        @Schema(example = "1234565")
        String serviceNumber,

        @Schema(example = "Apto 402, Calle 123 #45-67")
        String installationAddress,

        @Schema(example = "Apartamento Centro")
        String alias,

        @Schema(example = "ACTIVE")
        String status,

        @Schema(example = "SMART")
        String type
) {
}
