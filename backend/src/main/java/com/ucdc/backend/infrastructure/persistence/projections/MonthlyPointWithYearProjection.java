package com.ucdc.backend.infrastructure.persistence.projections;

import java.math.BigDecimal;

public interface MonthlyPointWithYearProjection {
    Integer getYear();
    Integer getMonth();
    BigDecimal getKwh();
}
