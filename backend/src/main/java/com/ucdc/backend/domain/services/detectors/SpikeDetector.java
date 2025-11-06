package com.ucdc.backend.domain.services.detectors;

import com.ucdc.backend.domain.enums.AlertType;

import java.math.BigDecimal;
import java.util.Optional;

public final class SpikeDetector {
    /** pct = 0.30 equivale a +30% */
    public static Optional<Finding> check(BigDecimal current, BigDecimal previous, BigDecimal pct) {
        if (previous.signum() <= 0) return Optional.empty();
        var thr = previous.multiply(BigDecimal.ONE.add(pct));
        return current.compareTo(thr) > 0
                ? Optional.of(new Finding(AlertType.SPIKE, current, thr,
                "Aumento mayor a " + pct.multiply(BigDecimal.valueOf(100)) + "%"))
                : Optional.empty();
    }
}
