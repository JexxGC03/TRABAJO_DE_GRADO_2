package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.model.MeterQuota;
import com.ucdc.backend.domain.repositories.MeterQuotaRepository;
import com.ucdc.backend.infrastructure.persistence.mapper.MeterQuotaJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaMeterQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MeterQuotaRepositoryAdapter implements MeterQuotaRepository {

    private final JpaMeterQuotaRepository jpa;
    private final MeterQuotaJpaMapper mapper;

    @Override
    public Optional<MeterQuota> findActiveByMeter(UUID meterId, OffsetDateTime at) {
        return jpa.findActive(meterId, at).map(mapper::toDomain);
    }

    @Override
    public void save(MeterQuota quota) {
        jpa.save(mapper.toEntity(quota));
    }
}
