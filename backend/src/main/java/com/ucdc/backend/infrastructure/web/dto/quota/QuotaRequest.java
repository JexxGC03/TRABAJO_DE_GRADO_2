package com.ucdc.backend.infrastructure.web.dto.quota;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record QuotaRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal kwhLimit,

        @NotBlank
        String periodicity // "MONTHLY" | "DAILY"
) {
}
