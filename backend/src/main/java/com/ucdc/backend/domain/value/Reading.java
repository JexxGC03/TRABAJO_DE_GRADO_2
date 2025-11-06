package com.ucdc.backend.domain.value;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;                // ✅ usa este tipo real
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Lectura cruda del medidor (energía acumulada a un instante). */
public record Reading(
        @NotNull UUID meterId,        // ahora es java.util.UUID
        @NotNull OffsetDateTime timestamp,
        @NotNull BigDecimal kwhAccum
) {}

