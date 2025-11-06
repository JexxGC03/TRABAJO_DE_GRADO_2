package com.ucdc.backend.infrastructure.web.dto.meter;

import com.ucdc.backend.infrastructure.web.validation.ValidProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterMeterRequest(
        @Schema(example = "SNR-ENE-00123", description = "Serial/placa único del medidor")
        @NotBlank @Size(max = 64)
        String serialNumber,

        @Schema(example = "1234565", description = "Número de servicio del contador")
        @NotBlank @Size(max = 20)
        String serviceNumber,

        @Schema(example = "Apto 402, Calle 123 #45-67", description = "Dirección de instalación")
        @NotBlank @Size(max = 160)
        String installationAddress,

        @Schema(example = "Energy Solutions", description = "Proveedor/Constructor del medidor")
        @NotBlank @Size(max = 32)
        @ValidProvider
        String provider,

        @Schema(example = "Mi medidor de casa", description = "Alias opcional visible para el usuario")
        @Size(max = 80)
        String alias
) {
}
