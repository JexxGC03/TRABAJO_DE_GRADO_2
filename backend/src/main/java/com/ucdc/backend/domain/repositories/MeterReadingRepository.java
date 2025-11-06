package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.value.Reading;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeterReadingRepository {

    boolean existsByMeterAndTimestamp(UUID meterId, OffsetDateTime ts);

    // slice/paginación simple
    record Slice<T>(java.util.List<T> items, long total) { }
    Slice<Reading> findByMeterBetweenOrdered(UUID meterId, OffsetDateTime from, OffsetDateTime to, int page, int size);

    Optional<Reading> findPreviousBefore(UUID meterId, OffsetDateTime timestamp);
    Optional<Reading> findAt(UUID meterId, OffsetDateTime timestamp);
    List<Reading> findBetween(UUID meterId, OffsetDateTime from, OffsetDateTime to);

    Optional<Reading> findLastByMeter(UUID meterId);
    Reading save(Reading reading);
    Optional<Reading> findNextAfter(UUID meterId, OffsetDateTime ts);
}
