package com.ucdc.backend.application.dto.consumption;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GetAnnualConsumptionResult(
        UUID meterId,
        int year,
        BigDecimal totalKwh,
        List<MonthlyDTO> monthlySeries,
        List<HistoricalYearDTO> historicalComparison
) {
    public record MonthlyDTO(int month, BigDecimal kwh) {}
    public record HistoricalYearDTO(int year, BigDecimal totalKwh) {}
}
