package com.ucdc.backend.domain.value;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Política de muestreo: decide si una nueva lectura debe persistirse.
 * - minInterval: intervalo mínimo entre escrituras
 * - maxInterval: intervalo máximo para forzar una escritura
 * - minDeltaWh: umbral mínimo de cambio en Wh para persistir
 * - jitterPct: ruido ±% para desincronizar escrituras
 */
public record SamplingPolicy(Duration minInterval,
                             Duration maxInterval,
                             BigDecimal minDeltaWh,
                             double jitterPct) {

    public SamplingPolicy {
        if (minInterval == null || maxInterval == null || minDeltaWh == null)
            throw new IllegalArgumentException("Missing required fields");
        if (minInterval.isNegative() || maxInterval.isNegative())
            throw new IllegalArgumentException("Intervals must be >= 0");
        if (minDeltaWh.signum() < 0) throw new IllegalArgumentException("minDeltaWh must be >= 0");
        if (jitterPct < 0) throw new IllegalArgumentException("jitterPct must be >= 0");
    }

    public boolean shouldPersist(Reading prev, Reading now) {
        if (prev == null) return true; // primera lectura
        var dt = Duration.between(prev.timestamp(), now.timestamp());
        if (dt.compareTo(minIntervalWithJitter()) < 0) return false;

        // kWh → Wh
        var deltaWh = now.kwhAccum()
                .subtract(prev.kwhAccum())
                .max(BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(1000));

        if (deltaWh.compareTo(minDeltaWh) >= 0) return true;
        return dt.compareTo(maxInterval) >= 0;
    }

    private Duration minIntervalWithJitter() {
        if (jitterPct <= 0) return minInterval;
        var factor = 1 + (Math.random() * 2 - 1) * jitterPct; // [1-j, 1+j]
        long nanos = (long) (minInterval.toNanos() * factor);
        return Duration.ofNanos(Math.max(1, nanos));
    }
}
