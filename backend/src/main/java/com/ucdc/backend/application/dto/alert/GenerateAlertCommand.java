package com.ucdc.backend.application.dto.alert;

import com.ucdc.backend.domain.model.Alert;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record GenerateAlertCommand(
        UUID meterId,
        BigDecimal thresholdKwh,
        BigDecimal currentKwh,
        Alert.Granularity granularity,   // MONTHLY o DAILY
        OffsetDateTime timestamp,        // de aquí se deriva YearMonth
        String reason

) {
    public GenerateAlertCommand {
        Objects.requireNonNull(meterId, "meterId");
        Objects.requireNonNull(thresholdKwh, "thresholdKwh");
        Objects.requireNonNull(currentKwh, "currentKwh");
        Objects.requireNonNull(granularity, "granularity");
        Objects.requireNonNull(timestamp, "timestamp");
        if (thresholdKwh.signum() < 0) throw new IllegalArgumentException("thresholdKwh >= 0");
        if (currentKwh.signum() < 0) throw new IllegalArgumentException("currentKwh >= 0");
    }
}
