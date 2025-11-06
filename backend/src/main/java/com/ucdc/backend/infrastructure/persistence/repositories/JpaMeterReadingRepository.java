package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.infrastructure.persistence.entity.MeterReadingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaMeterReadingRepository extends JpaRepository<MeterReadingEntity, UUID> {

    boolean existsByMeterIdAndTs(UUID meterId, OffsetDateTime timestamp);

    Optional<MeterReadingEntity> findTopByMeterIdOrderByTsDesc(UUID meterId);

    Page<MeterReadingEntity> findByMeterIdAndTsBetween(
            UUID meterId, OffsetDateTime from, OffsetDateTime to, Pageable pageable
    );

    Optional<MeterReadingEntity> findTopByMeterIdAndTsLessThanOrderByTsDesc(
            UUID meterId, OffsetDateTime timestamp);

    Optional<MeterReadingEntity> findByMeterIdAndTs(UUID meterId, OffsetDateTime timestamp);
    List<MeterReadingEntity> findByMeterIdAndTsBetween(
            UUID meterId,
            OffsetDateTime from,
            OffsetDateTime to
    );

    Optional<MeterReadingEntity> findFirstByMeterIdAndTsAfterOrderByTsAsc(
            UUID meterId, OffsetDateTime timestamp);

}
