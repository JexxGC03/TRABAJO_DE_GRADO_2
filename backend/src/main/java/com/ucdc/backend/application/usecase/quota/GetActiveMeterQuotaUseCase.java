package com.ucdc.backend.application.usecase.quota;

import com.ucdc.backend.application.dto.quota.MeterQuotaResult;

import java.util.UUID;

public interface GetActiveMeterQuotaUseCase {
    record Query(UUID meterId) {}
    MeterQuotaResult handle(Query q);
}
