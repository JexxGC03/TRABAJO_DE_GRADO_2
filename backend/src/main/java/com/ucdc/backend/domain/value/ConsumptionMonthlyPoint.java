package com.ucdc.backend.domain.value;

import java.math.BigDecimal;
import java.time.YearMonth;

public record ConsumptionMonthlyPoint(
        Integer month,
        BigDecimal kwh
) {
}
