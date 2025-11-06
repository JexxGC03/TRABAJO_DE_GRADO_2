package com.ucdc.backend.application.dto.recommendation;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public record GenerateRecommendationsResult(
        List<RecommendationItem> recommendations
) {
    public record RecommendationItem(String message, String priority) {}
}
