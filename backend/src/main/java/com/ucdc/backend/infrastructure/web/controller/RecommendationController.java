package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsCommand;
import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsResult;
import com.ucdc.backend.application.usecase.recommendation.GenerateRecommendationsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final GenerateRecommendationsUseCase generate;

    @PostMapping("/generate")
    public GenerateRecommendationsResult generate(@RequestBody GenerateRecommendationsCommand cmd) {
        return generate.handle(cmd);
    }
}
