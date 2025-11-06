package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.enums.Provider;
import com.ucdc.backend.domain.model.Meter;
import com.ucdc.backend.domain.repositories.MeterRepository;
import com.ucdc.backend.infrastructure.persistence.entity.MeterEntity;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaMeterRepository;
import com.ucdc.backend.infrastructure.persistence.mapper.MeterJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class MeterRepositoryAdapter implements MeterRepository {

    private final JpaMeterRepository jpa;
    private final MeterJpaMapper mapper;


    @Override
    public List<Meter> findAll() {
        return jpa.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Meter> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Meter save(Meter meter) {
        MeterEntity saved = jpa.save(mapper.toEntity(meter));
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public Optional<Meter> findBySerialNumber(String serialNumber) {
        return jpa.findBySerialNumber(serialNumber);
    }

    @Override
    public boolean existsById(UUID meterId) {
        return jpa.existsById(meterId);
    }

    @Override
    public boolean existsBySerialNumber(String serial) {
        return jpa.existsBySerialNumber(serial);
    }

    @Override
    public boolean existsByProviderAndServiceNumber(Provider provider, String serviceNumber) {
        return jpa.existsByProviderAndServiceNumber(provider, serviceNumber);
    }

    @Override
    public boolean existsByUserAndAlias(UUID userId, String alias) {
        return jpa.existsByUserIdAndAlias(userId, alias);
    }

    @Override
    public List<Meter> findByUserId(UUID userId) {
        return jpa.findByUserId(userId)               // List<MeterEntity>
                .stream()
                .map(mapper::toDomain)               // MeterEntity -> Meter
                .toList();
    }

    @Override
    public boolean existsBySerialNumberIgnoreCaseAndIdNot(String serialNumber, UUID excludeId) {
        return jpa.existsBySerialNumberIgnoreCaseAndIdNot(serialNumber, excludeId);
    }
}
