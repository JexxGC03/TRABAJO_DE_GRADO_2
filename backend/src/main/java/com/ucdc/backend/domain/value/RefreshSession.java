package com.ucdc.backend.domain.value;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record RefreshSession(
        UUID userId,
        String refreshToken,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt
) {
    public RefreshSession {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(refreshToken, "refreshToken");
        createdAt = (createdAt != null) ? createdAt : OffsetDateTime.now();
        expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
    }

    /** Crea una nueva sesión con expiración según TTL (en segundos). */
    public static RefreshSession forUser(UUID userId, String refreshToken, long ttlSeconds) {
        var now = OffsetDateTime.now();
        var exp = now.plusSeconds(ttlSeconds);
        return new RefreshSession(userId, refreshToken, now, exp);
    }

    /** Rota el token, manteniendo la misma expiración. */
    public RefreshSession rotate(String newToken) {
        return new RefreshSession(this.userId, newToken, OffsetDateTime.now(), this.expiresAt);
    }
}
