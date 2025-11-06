package com.ucdc.backend.application.usecase.quota;

import com.ucdc.backend.application.dto.quota.MeterQuotaCommand;
import com.ucdc.backend.application.dto.quota.MeterQuotaResult;

import java.util.UUID;

public interface UpsertMeterQuotaUseCase {
    MeterQuotaResult handle(UUID meterId, MeterQuotaCommand cmd);
}
