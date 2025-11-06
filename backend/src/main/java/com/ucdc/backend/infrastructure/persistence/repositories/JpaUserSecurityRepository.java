package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.infrastructure.persistence.entity.UserSecurityEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserSecurityRepository extends JpaRepository<UserSecurityEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserSecurityEntity u where u.userId = :id")
    Optional<UserSecurityEntity> findForUpdate(@Param("id") UUID userId);
}
