package com.ucdc.backend.infrastructure.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Resumen de usuario")
public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String role,
        String status
) {
}
