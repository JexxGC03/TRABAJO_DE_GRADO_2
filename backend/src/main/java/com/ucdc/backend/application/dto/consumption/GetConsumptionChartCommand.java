package com.ucdc.backend.application.dto.consumption;

import java.time.LocalDate;
import java.util.UUID;

public record GetConsumptionChartCommand(
        UUID meterId,
        LocalDate from,
        LocalDate to,
        Granularity granularity
) {
    public enum Granularity { DAILY, MONTHLY }
}
