package com.ucdc.backend.application.usecase.recommendation;

import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsCommand;
import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsResult;

public interface GenerateRecommendationsUseCase {
    GenerateRecommendationsResult handle(GenerateRecommendationsCommand cmd);
}
