package com.ucdc.backend.application.dto.auth;

import java.util.UUID;

public record RegisterUserResult(
        UUID id,
        String fullName,
        String email,
        String role,   // "ADMIN" | "CLIENT"
        String status  // "ACTIVE" | "BLOCKED"
) {
}
