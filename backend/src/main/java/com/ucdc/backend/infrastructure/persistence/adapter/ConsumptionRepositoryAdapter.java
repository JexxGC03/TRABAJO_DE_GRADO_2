package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.enums.ConsumptionType;
import com.ucdc.backend.domain.model.Consumption;
import com.ucdc.backend.infrastructure.persistence.entity.ConsumptionEntity;
import com.ucdc.backend.domain.repositories.ConsumptionRepository;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaConsumptionRepository;
import com.ucdc.backend.infrastructure.persistence.mapper.ConsumptionJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class ConsumptionRepositoryAdapter implements ConsumptionRepository{

    private final JpaConsumptionRepository jpa;
    private final ConsumptionJpaMapper mapper;

    private static OffsetDateTime atStartUTC(LocalDate d) {
        return d.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    @Override
    public Optional<Consumption> findByKey(UUID meterId, ConsumptionType type, OffsetDateTime periodStart) {
        return jpa.findByMeter_IdAndConsumptionTypeAndPeriodStart(meterId, type, periodStart)
                .map(mapper::toDomain);
    }

    @Override
    public Consumption save(Consumption c) {
        ConsumptionEntity saved = jpa.save(mapper.toEntity(c));
        return mapper.toDomain(saved);
    }

    @Override
    public List<Consumption> findByTypeBetween(UUID meterId, ConsumptionType type,
                                               OffsetDateTime from, OffsetDateTime to) {
        return jpa.findByMeter_IdAndConsumptionTypeAndPeriodStartBetweenOrderByPeriodStartAsc(
                        meterId, type, from, to)
                .stream().map(mapper::toDomain).toList();
    }

    @Transactional
    @Override
    public boolean deleteIfZero(UUID meterId, ConsumptionType type, OffsetDateTime periodStart) {
        int rows = jpa.deleteByKeyIfZero(meterId, type, periodStart);
        return rows > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyPoint> findMonthlyByMeterBetween(UUID meterId, LocalDate from, LocalDate toInclusive) {
        var fromStart   = from.atStartOfDay().atOffset(ZoneOffset.UTC);
        var toExclusive = toInclusive.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        var rows = jpa.findMonthlyRowsBetween(meterId, fromStart, toExclusive); // List<MonthlyPointProjection>
        return rows.stream()
                .map(r -> new MonthlyPoint(r.getMonth(), r.getKwh()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyPoint> findDailyByMeterBetween(UUID meterId, LocalDate from, LocalDate toInclusive) {
        var fromStart   = atStartUTC(from);
        var toExclusive = atStartUTC(toInclusive.plusDays(1));
        var rows = jpa.findDailyRowsBetween(meterId, fromStart, toExclusive); // List<DailyPointProjection>
        return rows.stream()
                .map(r -> new DailyPoint(r.getDate().toLocalDate(), r.getKwh()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumByMeterBetween(UUID meterId, LocalDate from, LocalDate toInclusive) {
        var fromStart   = atStartUTC(from);
        var toExclusive = atStartUTC(toInclusive.plusDays(1));
        var sum = jpa.sumDailyBetween(meterId, fromStart, toExclusive);
        return (sum == null) ? BigDecimal.ZERO : sum;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyPoint> findMonthlyByMeterAndYear(UUID meterId, int year) {
        var rows = jpa.findMonthlyRowsByYear(meterId, year); // List<MonthlyPointWithYearProjection>
        return rows.stream()
                .map(r -> new MonthlyPoint(r.getMonth(), r.getKwh()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumByMeterAndYear(UUID meterId, int year) {
        var sum = jpa.sumMonthlyByYear(meterId, year);
        return (sum == null) ? BigDecimal.ZERO : sum;
    }

    @Override
    @Transactional(readOnly = true)
    public List<YearTotal> sumByMeterGroupByYear(UUID meterId, int lastNYears) {
        int minYear = Year.now(ZoneOffset.UTC).getValue() - lastNYears + 1;
        var rows = jpa.sumMonthlyGroupByYearFrom(meterId, minYear); // List<YearAggregateProjection>
        return rows.stream()
                .map(r -> new YearTotal(r.getYear(), r.getTotalKwh()))
                .toList();
    }

    @Override
    public BigDecimal sumMonthlyToDate(UUID meterId, YearMonth ym) {
        // 1) Intentar mensual directo (si existe)
        Optional<BigDecimal> monthly = jpa.getMonthly(meterId, ym.getYear(), ym.getMonthValue());
        return monthly.orElseGet(() -> jpa.sumDailyMonthToDate(meterId, ym.getYear(), ym.getMonthValue()));

        // 2) Si no hay agregado mensual, sumar DAILY del mes
    }

    @Override
    public List<BigDecimal> findMonthlyKwhSameMonthOfYear(UUID meterId, Month month, int lastYears) {
        var currentStart = LocalDate.of(YearMonth.now().getYear(), month, 1);
        return jpa.findMonthlyKwhSameMonthOfYear(meterId, month.getValue(), currentStart, lastYears);
    }

    @Override
    public Optional<BigDecimal> findPreviousMonthKwh(UUID meterId, YearMonth ym) {
        var currentStart = LocalDate.of(ym.getYear(), ym.getMonth(), 1);
        return jpa.findPreviousMonthKwh(meterId, currentStart);
    }
}
