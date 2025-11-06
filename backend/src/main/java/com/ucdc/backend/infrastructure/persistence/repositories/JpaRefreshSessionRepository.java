package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.infrastructure.persistence.entity.RefreshSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaRefreshSessionRepository extends JpaRepository<RefreshSessionEntity, UUID> {
    Optional<RefreshSessionEntity> findByRefreshToken(String token);
    boolean existsByRefreshToken(String token);
    void deleteByRefreshToken(String token);
}
