package com.ucdc.backend.application.dto.quota;

import com.ucdc.backend.domain.model.MeterQuota;

import java.math.BigDecimal;

public record MeterQuotaCommand(
        BigDecimal kwhLimit,
        MeterQuota.Periodicity periodicity
) {
}
