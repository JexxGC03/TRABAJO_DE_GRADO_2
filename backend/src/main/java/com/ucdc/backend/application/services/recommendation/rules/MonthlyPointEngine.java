package com.ucdc.backend.application.services.recommendation.rules;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface MonthlyPointEngine {
    YearMonth period();          // ej. 2025-10
    BigDecimal kwh();
}
