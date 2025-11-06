package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.infrastructure.persistence.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaAlertRepository extends JpaRepository<AlertEntity, UUID> {
    Optional<AlertEntity> findFirstByMeterIdAndStatusOrderByCreatedAtDesc(UUID meterId, AlertStatus status);
    List<AlertEntity> findTop10ByMeterIdOrderByCreatedAtDesc(UUID meterId);

    @Query("""
      select case when count(a)>0 then true else false end
      from AlertEntity a
      where a.meter = :meterId
        and a.type = :type
        and a.granularity = :granularity
        and a.period = :period
        and a.status = 'ACTIVE'
      """)
    boolean existsActive(@Param("meterId") UUID meterId,
                         @Param("type") String type,
                         @Param("granularity") String granularity,
                         @Param("period") String period);

    @Query("""
      select a from AlertEntity a
      where (:meterId is null or a.meter = :meterId)
        and (:status is null or a.status = :status)
        and (:type is null or a.type = :type)
        and (:granularity is null or a.granularity = :granularity)
        and (:from is null or a.period >= :from)
        and (:to   is null or a.period <= :to)
      order by a.createdAt desc
    """)
    List<AlertEntity> search(@Param("meterId") UUID meterId,
                             @Param("status") String status,
                             @Param("type") String type,
                             @Param("granularity") String granularity,
                             @Param("from") String fromInclusive,
                             @Param("to") String toInclusive);

}
