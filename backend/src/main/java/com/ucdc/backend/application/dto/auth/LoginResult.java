package com.ucdc.backend.application.dto.auth;

import java.time.OffsetDateTime;
import java.util.UUID;


public record LoginResult(
        String tokenType,
        String accessToken,
        int accessTtl,
        String refreshToken,
        OffsetDateTime refreshExp,
        UserSummary user
) {
    public record UserSummary(
            UUID id,
            String fullName,
            String email,
            String role,
            String status
    ) {}
}
