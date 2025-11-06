package com.ucdc.backend.application.service.recommendation;


import com.ucdc.backend.application.services.recommendation.rules.DefaultRecommendationRuleEngine;
import com.ucdc.backend.application.services.recommendation.rules.MonthlyPointEngine;
import com.ucdc.backend.application.services.recommendation.rules.RecommendationRuleEngine;

class DefaultRecommendationRuleEngineTest {

    RecommendationRuleEngine engine = new DefaultRecommendationRuleEngine();

    static record MP(java.time.YearMonth period, java.math.BigDecimal kwh)
            implements MonthlyPointEngine {}

    @org.junit.jupiter.api.Test
    void rulesShouldGenerateMessages() {
        var ym = java.time.YearMonth.of(2025, 10);
        var monthly = java.util.List.of(
                new MP(ym.minusMonths(1), new java.math.BigDecimal("100")),
                new MP(ym,             new java.math.BigDecimal("130")) // +30% MoM
        );
        var cmd = new com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsCommand(
                java.util.UUID.randomUUID(), java.util.UUID.randomUUID(), ym);

        var msgs = engine.build(cmd, monthly, java.util.List.of());
        org.junit.jupiter.api.Assertions.assertFalse(msgs.isEmpty());
    }
}
