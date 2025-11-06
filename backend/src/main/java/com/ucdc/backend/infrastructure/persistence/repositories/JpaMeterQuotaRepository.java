package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.infrastructure.persistence.entity.MeterQuotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface JpaMeterQuotaRepository extends JpaRepository<MeterQuotaEntity, UUID> {

    @Query("""
      select q from MeterQuotaEntity q
      where q.meterId = :meterId
        and :at >= q.validFrom
        and (q.validTo is null or :at < q.validTo)
      order by q.validFrom desc
      """)
    Optional<MeterQuotaEntity> findActive(@Param("meterId") UUID meterId,
                                          @Param("at") OffsetDateTime at);
}
