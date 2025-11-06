package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.repositories.MeterReadingRepository;
import com.ucdc.backend.domain.value.Reading;
import com.ucdc.backend.infrastructure.persistence.entity.MeterReadingEntity;
import com.ucdc.backend.infrastructure.persistence.mapper.MeterReadingJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaMeterReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MeterReadingRepositoryAdapter implements MeterReadingRepository {

    private final JpaMeterReadingRepository jpa;
    private final MeterReadingJpaMapper mapper;


    @Override
    public boolean existsByMeterAndTimestamp(UUID meterId, OffsetDateTime ts) {
        return jpa.existsByMeterIdAndTs(meterId, ts);
    }

    @Override
    public Slice<Reading> findByMeterBetweenOrdered(UUID meterId, OffsetDateTime from, OffsetDateTime to, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "ts"));
        var p = jpa.findByMeterIdAndTsBetween(meterId, from, to, pageable);
        var items = p.getContent().stream().map(mapper::toDomain).toList();
        return new Slice<>(items, p.getTotalElements());
    }


    @Override
    public Optional<Reading> findLastByMeter(UUID meterId) {
        return jpa.findTopByMeterIdOrderByTsDesc(meterId).map(mapper::toDomain);
    }



    @Override
    public Reading save(Reading reading) {
        var saved = jpa.save(mapper.toEntity(reading));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Reading> findNextAfter(UUID meterId, OffsetDateTime ts) {
        return jpa.findFirstByMeterIdAndTsAfterOrderByTsAsc(meterId, ts)
                .map(this::toDomainInline);
    }

    private Reading toDomainInline(MeterReadingEntity e) {
        return new Reading(e.getMeterId(), e.getTs(), e.getKwhAccum());
    }
        // }

    @Override
    public Optional<Reading> findPreviousBefore(UUID meterId, OffsetDateTime ts) {
        return jpa.findTopByMeterIdAndTsLessThanOrderByTsDesc(meterId, ts)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Reading> findAt(UUID meterId, OffsetDateTime ts) {
        return jpa.findByMeterIdAndTs(meterId, ts)
                .map(mapper::toDomain);
    }

    @Override
    public List<Reading> findBetween(UUID meterId, OffsetDateTime from, OffsetDateTime to) {
        return jpa.findByMeterIdAndTsBetween(meterId, from, to)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
