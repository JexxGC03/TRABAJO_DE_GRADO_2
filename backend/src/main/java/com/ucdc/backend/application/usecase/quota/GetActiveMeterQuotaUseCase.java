package com.ucdc.backend.application.usecase.quota;

import com.ucdc.backend.domain.model.MeterQuota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface GetActiveMeterQuotaUseCase {
    record Query(UUID meterId) {}
    record Result(UUID quotaId, BigDecimal kwhLimit, MeterQuota.Periodicity periodicity, OffsetDateTime validFrom) {}
    Result handle(Query q);
}
