package com.ucdc.backend.infrastructure.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Par de tokens de autenticación")
public record TokenPairResponse(
        @Schema(description = "JWT de acceso (corto plazo)")
        String accessToken,

        @Schema(description = "JWT de refresco (largo plazo)")
        String refreshToken,

        @Schema(example = "Bearer")
        String tokenType,

        @Schema(description = "Expiración del access token (segundos)", example = "3600")
        long expiresIn
) {
}
