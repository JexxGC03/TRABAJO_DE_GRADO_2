package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.domain.enums.Provider;
import com.ucdc.backend.domain.model.Meter;
import com.ucdc.backend.infrastructure.persistence.entity.MeterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaMeterRepository extends JpaRepository<MeterEntity, UUID> {

    @Query("""
    SELECT m
    FROM MeterEntity m
    WHERE m.serialNumber = :serialNumber
""")
    Optional<Meter> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serial);
    boolean existsByProviderAndServiceNumber(Provider provider, String serviceNumber);
    boolean existsByUserIdAndAlias(UUID userId, String alias);
    List<MeterEntity> findByUserId(UUID userId);
    boolean existsBySerialNumberIgnoreCaseAndIdNot(String serialNumber, UUID excludeId);
}
