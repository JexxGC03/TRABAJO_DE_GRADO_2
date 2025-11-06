package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.value.RefreshSession;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshSessionRepository {
    RefreshSession save(RefreshSession session);
    void save(UUID userId, String refreshToken, OffsetDateTime createdAt);
    Optional<RefreshSession> findByRefreshToken(String token);
    boolean existsByRefreshToken(String token);
    void deleteByRefreshToken(String token);

    // usados por tu servicio:
    int defaultAccessTtlSeconds();
    int defaultRefreshTtlSeconds();

}
