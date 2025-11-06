package com.ucdc.backend.infrastructure.config;

import com.ucdc.backend.application.services.recommendation.rules.DefaultRecommendationRuleEngine;
import com.ucdc.backend.application.services.recommendation.rules.RecommendationRuleEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecommendationConfig {

    @Bean
    RecommendationRuleEngine recommendationRuleEngine() {
        return new DefaultRecommendationRuleEngine();
    }
}
