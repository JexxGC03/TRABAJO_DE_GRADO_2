package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.model.Recommendation;
import com.ucdc.backend.domain.repositories.RecommendationRepository;
import com.ucdc.backend.infrastructure.persistence.mapper.RecommendationJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class RecommendationRepositoryAdapter implements RecommendationRepository {

    private final JpaRecommendationRepository jpa;
    private final RecommendationJpaMapper mapper;

    @Override
    public List<Recommendation> findByUserAndMessages(UUID userId, Set<String> messages) {
        if (messages == null || messages.isEmpty()) return List.of();
        var entities = jpa.findByUserAndMessages(userId, messages);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Recommendation> saveAll(List<Recommendation> recos) {
        if (recos == null || recos.isEmpty()) return List.of();
        var saved = jpa.saveAll(recos.stream().map(mapper::toEntity).toList());
        return mapper.toDomainList(saved);
    }



}
