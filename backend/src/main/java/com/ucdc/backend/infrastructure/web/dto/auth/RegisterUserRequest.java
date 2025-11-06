package com.ucdc.backend.infrastructure.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Payload de registro de usuario")
public record RegisterUserRequest(
        @NotBlank @Schema(example = "Juan Pérez")
        String fullName,

        @NotBlank @Schema(example = "1032456789", description = "Cédula (única)")
        String citizenId,

        @NotBlank @Email @Schema(example = "juan.perez@mail.com")
        String email,

        @NotBlank @Schema(example = "SV-123456", description = "Número de servicio (operador)")
        String serviceNumber,

        @NotBlank @Size(min = 8, max = 72)
        @Schema(example = "S3gura#2024")
        String password,

        @NotBlank @Schema(example = "S3gura#2024")
        String confirmPassword,

        @Pattern(regexp = "\\+?[0-9\\- ]{7,20}")
        @Schema(example = "+57 3001234567")
        String phone
) {
}
