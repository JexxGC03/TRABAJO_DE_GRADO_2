package com.ucdc.backend.domain.services.detectors;

import com.ucdc.backend.domain.enums.AlertType;
import com.ucdc.backend.domain.services.StatisticalAlertPolicy;

import java.math.BigDecimal;
import java.util.Optional;

public final class ZScoreDetector {
    public static Optional<Finding> check(BigDecimal current,
                                          StatisticalAlertPolicy.BaselineStats s,
                                          BigDecimal kSigma) {
        var thr = StatisticalAlertPolicy.upperBound(s, kSigma);
        return current.compareTo(thr) > 0
                ? Optional.of(new Finding(AlertType.STATISTICAL_ANOMALY, current, thr,
                "Valor supera media + " + kSigma + "·σ"))
                : Optional.empty();
    }
}
