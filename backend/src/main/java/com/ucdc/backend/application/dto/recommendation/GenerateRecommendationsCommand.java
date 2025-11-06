package com.ucdc.backend.application.dto.recommendation;

import java.time.YearMonth;
import java.util.UUID;

public record GenerateRecommendationsCommand(
        UUID userId,
        UUID meterId,
        YearMonth analysisPeriod
) {}
