package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.enums.Provider;
import com.ucdc.backend.domain.model.Meter;
import com.ucdc.backend.infrastructure.persistence.entity.MeterEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeterRepository {

    /**
     * Metodos Crud
    **/
    List<Meter> findAll();
    Optional<Meter> findById(UUID id);
    Meter save(Meter alert);
    void deleteById(UUID id);

    /**Otros Metodos
     */
    // Lookups/validaciones
    boolean existsById(UUID id);
    Optional<Meter> findBySerialNumber(String serialNumber);
    boolean existsBySerialNumber(String serialNumber);
    boolean existsByProviderAndServiceNumber(Provider provider, String serviceNumber);
    boolean existsByUserAndAlias(UUID userId, String alias);
    List<Meter> findByUserId(UUID userId);
    boolean existsBySerialNumberIgnoreCaseAndIdNot(String serialNumber, UUID excludeId);
}
