package com.ucdc.backend.infrastructure.persistence.projections;

import java.math.BigDecimal;

public interface MonthlyPointProjection {
    Integer getMonth();
    BigDecimal getKwh();
}
