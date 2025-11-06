package com.ucdc.backend.application.dto.consumption;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GetConsumptionChartResult(
        UUID meterId,
        LocalDate from,
        LocalDate to,
        String granularity,
        List<?> series
) {
    // Para charts diarios
    public record ChartDailyPoint(LocalDate date, BigDecimal kwh) {}

    // Para charts mensuales ← AQUÍ
    public record ChartMonthlyPoint(int month, BigDecimal kwh) {}
}
