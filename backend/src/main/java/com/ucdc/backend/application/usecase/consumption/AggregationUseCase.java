package com.ucdc.backend.application.usecase.consumption;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.UUID;

public interface AggregationUseCase {
    /** Recalcula todas las ventanas que cubren el timestamp dado. */
    void bucketizeAt(UUID meterId, OffsetDateTime ts);

    /** Recalcula por ventanas (min→hour→day→month) en el rango [from, to]. */
    void bucketizeRange(UUID meterId, OffsetDateTime from, OffsetDateTime to);

    /** Utilidad batch: fuerza el recálculo de un día completo (idempotente). */
    void rebuildDay(UUID meterId, LocalDate day);

    /** Utilidad batch: fuerza el recálculo de un mes completo (idempotente). */
    void rebuildMonth(UUID meterId, YearMonth month);
}
