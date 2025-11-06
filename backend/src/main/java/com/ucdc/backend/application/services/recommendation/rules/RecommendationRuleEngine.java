package com.ucdc.backend.application.services.recommendation.rules;

import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsCommand;
import com.ucdc.backend.domain.model.Alert;

import java.util.List;

public interface RecommendationRuleEngine {
    List<String> build(GenerateRecommendationsCommand cmd,
                       List<? extends MonthlyPointEngine> monthly,
                       List<Alert> recentAlerts);
}
