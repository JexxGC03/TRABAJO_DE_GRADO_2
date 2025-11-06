package com.ucdc.backend.infrastructure.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Credenciales para iniciar sesión")
public record LoginRequest(
        @NotBlank @Email @Schema(example = "juan.perez@mail.com")
        String email,
        @NotBlank @Size(min = 8, max = 72) @Schema(example = "S3gura#2024")
        String password
) {
}
