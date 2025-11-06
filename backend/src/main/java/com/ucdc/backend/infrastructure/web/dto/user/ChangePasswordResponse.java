package com.ucdc.backend.infrastructure.web.dto.user;

public record ChangePasswordResponse(
        String message,
        String updatedAt
) {
}
