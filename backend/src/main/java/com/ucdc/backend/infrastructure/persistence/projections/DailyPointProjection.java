package com.ucdc.backend.infrastructure.persistence.projections;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface DailyPointProjection {
    OffsetDateTime getDate();
    BigDecimal getKwh();
}
