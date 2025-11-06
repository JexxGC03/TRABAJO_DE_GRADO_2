package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.infrastructure.persistence.entity.AlertEntity;
import com.ucdc.backend.infrastructure.persistence.entity.RecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface JpaRecommendationRepository extends JpaRepository<RecommendationEntity, UUID> {

    @Query("""
           select r
           from RecommendationEntity r
           where r.user = :userId
             and r.message in (:messages)
           """)
    List<RecommendationEntity> findByUserAndMessages(UUID userId, Collection<String> messages);
}
