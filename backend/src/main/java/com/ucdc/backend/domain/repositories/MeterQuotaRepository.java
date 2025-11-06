package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.model.MeterQuota;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface MeterQuotaRepository {
    Optional<MeterQuota> findActiveByMeter(UUID meterId, OffsetDateTime at);
    public MeterQuota save(MeterQuota quota);
    Optional<MeterQuota> findByMeterId(UUID meterId);                  // crea/actualiza vigente
    boolean existsByMeterId(UUID meterId);
}
