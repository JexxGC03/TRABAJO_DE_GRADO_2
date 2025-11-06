package com.ucdc.backend.application.dto.consumption;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public record GetMonthlyConsumptionResult(
        UUID meterId,
        YearMonth period,
        BigDecimal totalKwh,
        List<com.ucdc.backend.domain.value.ConsumptionDailyPoint> dailySeries
) {
    public record PointDTO(LocalDate date, BigDecimal kwh) {}
}
