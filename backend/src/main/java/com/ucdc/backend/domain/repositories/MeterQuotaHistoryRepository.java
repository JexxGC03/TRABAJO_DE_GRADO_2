package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.value.MeterQuotaVersion;

import java.util.List;
import java.util.UUID;

public interface MeterQuotaHistoryRepository {
    void save(MeterQuotaVersion version);
    List<MeterQuotaVersion> findByMeterId(UUID meterId);
}
