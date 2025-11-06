package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.repositories.RefreshSessionRepository;
import com.ucdc.backend.domain.value.RefreshSession;
import com.ucdc.backend.infrastructure.config.AuthTokensProperties;
import com.ucdc.backend.infrastructure.persistence.mapper.SessionJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaRefreshSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshSessionRepositoryAdapter implements RefreshSessionRepository {

    private final JpaRefreshSessionRepository jpa;
    private final SessionJpaMapper mapper;
    private final AuthTokensProperties props;

    @Override
    public RefreshSession save(RefreshSession session) {
        var saved = jpa.save(mapper.toEntity(session));
        return mapper.toDomain(saved);
    }

    @Override
    public void save(UUID userId, String refreshToken, OffsetDateTime createdAt) {
        var now = (createdAt != null) ? createdAt : OffsetDateTime.now();
        var exp = now.plusSeconds(defaultRefreshTtlSeconds());
        var rs = new RefreshSession(userId, refreshToken, now, exp);
        jpa.save(mapper.toEntity(rs));
    }

    @Override
    public Optional<RefreshSession> findByRefreshToken(String token) {
        return jpa.findByRefreshToken(token).map(mapper::toDomain);
    }

    @Override
    public boolean existsByRefreshToken(String token) {
        return jpa.existsByRefreshToken(token);
    }

    @Override
    public void deleteByRefreshToken(String token) {
        jpa.deleteByRefreshToken(token);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        jpa.deleteAllByUserId(userId);
    }

    // helpers usados por services
    public void save(UUID userId, String token) {
        var now = OffsetDateTime.now();
        var exp = now.plusSeconds(defaultRefreshTtlSeconds());
        var rs = new RefreshSession(userId, token, now, exp);
        jpa.save(mapper.toEntity(rs));
    }

    @Override
    public int defaultAccessTtlSeconds() {
        return props.getAccessTtlSeconds();
    }

    @Override
    public int defaultRefreshTtlSeconds() {
        return props.getRefreshTtlSeconds();
    }
}
