package com.ucdc.backend.domain.value;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConsumptionDailyPoint(
        LocalDate date,
        BigDecimal kwh
) {
}
