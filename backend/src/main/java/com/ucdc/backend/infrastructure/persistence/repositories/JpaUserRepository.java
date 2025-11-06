package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    /**Others Methods
     */
    Optional<UserEntity> findByEmail(Email email);
    boolean existsByCitizenId(String citizenId);
    boolean existsByEmail(String email);

    /**Methods for Type
     */
    @Query("SELECT u FROM UserEntity u WHERE u.role = 'CLIENT'")
    List<UserEntity> findAllClients();

    @Query("SELECT u FROM UserEntity u WHERE u.role = 'CLIENT' AND u.id = :id")
    Optional<UserEntity> findClientById(@Param("id") UUID id);

    @Query("SELECT u FROM UserEntity u WHERE u.role = 'ADMIN'")
    List<UserEntity> findAllAdmins();

    @Query("SELECT u FROM UserEntity u WHERE u.role = 'ADMIN' AND u.id = :id")
    Optional<UserEntity> findAdminById(@Param("id") UUID id);
}
