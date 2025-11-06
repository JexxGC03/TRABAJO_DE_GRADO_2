package com.ucdc.backend.application.dto.consumption;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CompareActualVsProjectedResult(
        UUID meterId,
        String periodLabel,
        String granularity,
        List<PointDTO> real,
        List<PointDTO> projected,
        List<DeltaDTO> delta,
        SummaryDTO summary
) {
    public record PointDTO(String label, BigDecimal kwh) {}
    public record DeltaDTO(String label, BigDecimal kwhDiff, BigDecimal percent) {}
    public record SummaryDTO(boolean overBudget, BigDecimal deviationPercent) {}
}
