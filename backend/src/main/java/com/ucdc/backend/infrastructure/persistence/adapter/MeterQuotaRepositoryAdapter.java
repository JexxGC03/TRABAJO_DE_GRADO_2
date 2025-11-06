package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.model.MeterQuota;
import com.ucdc.backend.domain.repositories.MeterQuotaRepository;
import com.ucdc.backend.infrastructure.persistence.entity.MeterQuotaEntity;
import com.ucdc.backend.infrastructure.persistence.mapper.MeterQuotaHistoryJpaMapper;
import com.ucdc.backend.infrastructure.persistence.mapper.MeterQuotaJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaMeterQuotaRepository;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaMeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MeterQuotaRepositoryAdapter implements MeterQuotaRepository {

    private final JpaMeterQuotaRepository jpa;
    private final JpaMeterRepository meterJpa;
    private final MeterQuotaJpaMapper mapper;

    @Override
    public Optional<MeterQuota> findActiveByMeter(UUID meterId, OffsetDateTime at) {
        return jpa.findActive(meterId, at).map(mapper::toDomain);
    }

    @Override
    public MeterQuota save(MeterQuota quota) {
        var meterId = quota.meterId();

        // ¿Existe la vigente? Reusar la instancia administrada
        var existing = jpa.findById(meterId).orElse(null);
        if (existing != null) {
            existing.setPeriodicity(quota.periodicity());
            existing.setKwhLimit(quota.kwhLimit());
            existing.setValidFrom(quota.validFrom());
            existing.setValidTo(null); // la vigente no tiene validTo

            // Al estar managed, basta con devolverla (save es opcional, pero es inofensivo)
            var saved = jpa.save(existing);
            return mapper.toDomain(saved);
        }

        // No existe -> crear NUEVA y setear @MapsId con el meter
        var meterRef = meterJpa.getReferenceById(meterId);

        // ⚠️ No usar mapper.toEntity(...) aquí si pone ID o crea otro objeto conflictivo.
        var entity = new MeterQuotaEntity();
        entity.setMeter(meterRef);                       // @MapsId: asigna PK = meter.id
        entity.setPeriodicity(quota.periodicity());
        entity.setKwhLimit(quota.kwhLimit());
        entity.setValidFrom(quota.validFrom());
        entity.setValidTo(quota.validTo());              // normalmente null

        var saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MeterQuota> findByMeterId(UUID meterId) {
        return jpa.findById(meterId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByMeterId(UUID meterId) {
        return jpa.existsById(meterId);
    }
}
