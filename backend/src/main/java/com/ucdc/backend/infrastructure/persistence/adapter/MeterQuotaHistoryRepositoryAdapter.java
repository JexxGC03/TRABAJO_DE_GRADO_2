package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.repositories.MeterQuotaHistoryRepository;
import com.ucdc.backend.domain.value.MeterQuotaVersion;
import com.ucdc.backend.infrastructure.persistence.mapper.MeterQuotaHistoryJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaMeterQuotaHistory;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaMeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MeterQuotaHistoryRepositoryAdapter implements MeterQuotaHistoryRepository {

    private final JpaMeterQuotaHistory historyJpa;
    private final JpaMeterRepository meterJpa;
    private final MeterQuotaHistoryJpaMapper mapper;

    @Override
    public void save(MeterQuotaVersion version) {
        var e = mapper.toEntity(version);
        e.setMeter(meterJpa.getReferenceById(version.meterId()));
        historyJpa.save(e);
    }

    @Override
    public List<MeterQuotaVersion> findByMeterId(UUID meterId) {
        return historyJpa.findByMeterIdOrderByValidFromDesc(meterId)
                .stream().map(mapper::toDomain).toList();
    }
}
