package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.domain.value.MeterQuotaVersion;
import com.ucdc.backend.infrastructure.persistence.entity.MeterQuotaHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaMeterQuotaHistory  extends JpaRepository<MeterQuotaHistoryEntity, UUID> {
    List<MeterQuotaHistoryEntity> findByMeterIdOrderByValidFromDesc(UUID meterId);
}
