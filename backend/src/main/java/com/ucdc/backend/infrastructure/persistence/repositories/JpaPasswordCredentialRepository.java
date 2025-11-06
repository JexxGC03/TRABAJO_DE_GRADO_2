package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.infrastructure.persistence.entity.PasswordCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaPasswordCredentialRepository extends JpaRepository<PasswordCredentialEntity, UUID> {
    // Proyección directa al hash (String) por userId
    @Query("SELECT p.passwordHash FROM PasswordCredentialEntity p WHERE p.userId = :userId")
    Optional<String> findHashByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
