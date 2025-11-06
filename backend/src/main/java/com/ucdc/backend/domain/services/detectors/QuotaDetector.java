package com.ucdc.backend.domain.services.detectors;

import com.ucdc.backend.domain.enums.AlertType;
import com.ucdc.backend.domain.model.MeterQuota;

import java.math.BigDecimal;
import java.util.Optional;

public final class QuotaDetector {
    public static Optional<Finding> checkMonthly(BigDecimal monthToDate, MeterQuota quota) {
        if (quota.periodicity() != MeterQuota.Periodicity.MONTHLY) return Optional.empty();
        if (monthToDate.compareTo(quota.kwhLimit()) > 0) {
            return Optional.of(new Finding(AlertType.QUOTA_OVERUSE, monthToDate, quota.kwhLimit(),
                    "Consumo acumulado del mes excede la cuota"));
        }
        return Optional.empty();
    }
}
