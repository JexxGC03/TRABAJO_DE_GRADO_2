package com.ucdc.backend.infrastructure.persistence.projections;

import java.math.BigDecimal;

public interface YearAggregateProjection {
    Integer getYear();
    BigDecimal getTotalKwh();
}
