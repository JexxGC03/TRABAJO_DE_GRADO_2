package com.ucdc.backend.application.events;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

public record MonthlyConsumptionRecorded(
        UUID meterId,
        YearMonth period,
        BigDecimal kwh
) {
}
