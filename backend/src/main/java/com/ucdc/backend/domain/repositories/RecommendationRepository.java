package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.model.Recommendation;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RecommendationRepository {

    List<Recommendation> findByUserAndMessages(UUID userId, Set<String> messages);
    List<Recommendation> saveAll(List<Recommendation> recos);
}
