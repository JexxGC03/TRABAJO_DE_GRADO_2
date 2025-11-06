package com.ucdc.backend.domain.services.detectors;

import com.ucdc.backend.domain.enums.AlertType;

import java.math.BigDecimal;

public record Finding(
        AlertType type,
        BigDecimal observed,
        BigDecimal expectedOrThreshold,
        String reason
) {}
