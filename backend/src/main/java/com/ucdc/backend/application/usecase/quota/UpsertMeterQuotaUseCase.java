package com.ucdc.backend.application.usecase.quota;

import com.ucdc.backend.domain.model.MeterQuota;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpsertMeterQuotaUseCase {
    record Command(UUID meterId, MeterQuota.Periodicity periodicity, BigDecimal kwhLimit) {}
    record Result(UUID quotaId) {}
    Result handle(Command cmd);
}
