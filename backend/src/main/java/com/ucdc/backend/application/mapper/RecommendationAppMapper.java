package com.ucdc.backend.application.mapper;

import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsResult;
import com.ucdc.backend.domain.model.Recommendation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RecommendationAppMapper {

    /* ========= Command → Domain ========= */
    @ObjectFactory
    default Recommendation from(String message, UUID userId) {
        // genera una nueva recomendación activa con ID aleatorio
        return Recommendation.create(
                UUID.randomUUID(),
                userId,
                message
        );
    }

    /* ========= Domain → DTO (resultado) ========= */
    @Mapping(target = "message",  expression = "java(src.message())")
    @Mapping(target = "priority", expression = "java(src.status().name())")
    GenerateRecommendationsResult.RecommendationItem toItem(Recommendation src);
}
