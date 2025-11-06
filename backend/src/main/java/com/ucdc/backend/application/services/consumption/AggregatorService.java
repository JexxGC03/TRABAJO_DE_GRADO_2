package com.ucdc.backend.application.services.consumption;

import com.ucdc.backend.application.events.MonthlyConsumptionRecorded;
import com.ucdc.backend.application.usecase.consumption.AggregationUseCase;
import com.ucdc.backend.domain.enums.ConsumptionType;
import com.ucdc.backend.domain.model.Consumption;
import com.ucdc.backend.domain.repositories.ConsumptionRepository;
import com.ucdc.backend.domain.repositories.MeterReadingRepository;
import com.ucdc.backend.domain.value.Reading;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AggregatorService implements AggregationUseCase {

    private final MeterReadingRepository readingRepo;
    private final ConsumptionRepository  consumptionRepo;
    private final ApplicationEventPublisher publisher;

    /* ===================== Public API (UseCase) ===================== */
    @Override
    public void bucketizeAt(UUID meterId, OffsetDateTime ts) {
        bucketizeHierarchyAt(meterId, ts);
    }

    @Override
    public void bucketizeRange(UUID meterId, OffsetDateTime from, OffsetDateTime to) {
        if (!from.isBefore(to)) return;

        // 1) MINUTELY: agrupa lecturas por minuto y calcula delta por grupo
        var readings = readingRepo.findBetween(meterId, from, to);
        var byMinute = readings.stream()
                .collect(Collectors.groupingBy(r -> r.timestamp().truncatedTo(ChronoUnit.MINUTES)));

        for (var entry : byMinute.entrySet()) {
            var minuteStart = entry.getKey();
            var minuteDelta = computeDeltaKwh(entry.getValue());
            upsert(meterId, ConsumptionType.MINUTELY, minuteStart, minuteDelta);
        }

        // Determinar los contenedores a recalcular (horas, días, meses) que cubren [from, to)
        var hourStart = truncateHour(from);
        for (var h = hourStart; h.isBefore(to); h = h.plusHours(1)) {
            var hourEnd = h.plusHours(1);
            var hourKwh = sumAggregates(meterId, ConsumptionType.MINUTELY, h, hourEnd);
            upsert(meterId, ConsumptionType.HOURLY, h, hourKwh);
        }

        var dayStart = truncateDay(from);
        for (var d = dayStart; d.isBefore(to); d = d.plusDays(1)) {
            var dayEnd = d.plusDays(1);
            var dayKwh = sumAggregates(meterId, ConsumptionType.HOURLY, d, dayEnd);
            upsert(meterId, ConsumptionType.DAILY, d, dayKwh);
        }

        var monthStart = truncateMonth(from);
        for (var m = monthStart; m.isBefore(to); m = m.plusMonths(1)) {
            var monthEnd = m.plusMonths(1);
            var monthKwh = sumAggregates(meterId, ConsumptionType.DAILY, m, monthEnd);
            upsert(meterId, ConsumptionType.MONTHLY, m, monthKwh);
            publisher.publishEvent(new MonthlyConsumptionRecorded(
                    meterId,
                    java.time.YearMonth.from(m),
                    monthKwh
            ));
        }
    }

    @Override
    public void rebuildDay(UUID meterId, LocalDate day) {
        var from = day.atStartOfDay().atOffset(ZoneOffset.UTC);     // ajusta si usas otra zona
        var to   = from.plusDays(1);
        bucketizeRange(meterId, from, to);
    }

    @Override
    public void rebuildMonth(UUID meterId, YearMonth month) {
        var from = month.atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        var to   = from.plusMonths(1);
        bucketizeRange(meterId, from, to);
    }

    /* ===================== Internal helpers ===================== */

    /** Recalcula jerarquía completa (min→hour→day→month) que cubre el timestamp. */
    private void bucketizeHierarchyAt(UUID meterId, OffsetDateTime ts) {
        // MINUTELY
        var minuteStart = ts.truncatedTo(ChronoUnit.MINUTES);
        var minuteEnd   = minuteStart.plusMinutes(1);
        var minuteDelta = computeDeltaKwh(readingRepo.findBetween(meterId, minuteStart, minuteEnd));
        upsert(meterId, ConsumptionType.MINUTELY, minuteStart, minuteDelta);

        // HOURLY
        var hourStart = ts.truncatedTo(ChronoUnit.HOURS);
        var hourEnd   = hourStart.plusHours(1);
        var hourKwh   = sumAggregates(meterId, ConsumptionType.MINUTELY, hourStart, hourEnd);
        upsert(meterId, ConsumptionType.HOURLY, hourStart, hourKwh);

        // DAILY
        var dayStart = ts.truncatedTo(ChronoUnit.DAYS);
        var dayEnd   = dayStart.plusDays(1);
        var dayKwh   = sumAggregates(meterId, ConsumptionType.HOURLY, dayStart, dayEnd);
        upsert(meterId, ConsumptionType.DAILY, dayStart, dayKwh);

        // MONTHLY
        var monthStart = dayStart.withDayOfMonth(1);
        var monthEnd   = monthStart.plusMonths(1);
        var monthKwh   = sumAggregates(meterId, ConsumptionType.DAILY, monthStart, monthEnd);
        upsert(meterId, ConsumptionType.MONTHLY, monthStart, monthKwh);
    }

    /** Suma incremental positiva de kWh a partir de lecturas acumuladas. */
    private BigDecimal computeDeltaKwh(List<Reading> rs) {
        var sorted = rs.stream()
                .sorted(Comparator.comparing(Reading::timestamp))
                .toList();

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal prev = null;

        for (var r : sorted) {
            var acc = r.kwhAccum();
            if (prev != null && acc.compareTo(prev) >= 0) {
                sum = sum.add(acc.subtract(prev));
            }
            prev = acc;
        }
        return sum;
    }

    /** Suma kWh de buckets existentes en [from, to). */
    private BigDecimal sumAggregates(UUID meterId, ConsumptionType type, OffsetDateTime from, OffsetDateTime to) {
        return consumptionRepo.findByTypeBetween(meterId, type, from, to).stream()
                .map(Consumption::getKwh)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Upsert por clave (meterId + type + periodStart). */
    private void upsert(UUID meterId, ConsumptionType type, OffsetDateTime start, BigDecimal kwh) {
        // Política: si no quieres filas "vacías", evita persistir 0
        if (kwh == null || kwh.signum() == 0) {
            consumptionRepo.deleteIfZero(meterId, type, start); // opcional: si tienes este método
            return;
        }

        var existing = consumptionRepo.findByKey(meterId, type, start);
        if (existing.isPresent()) {
            var c = existing.get();
            var updated = createConsumptionWithId(type, c.getId(), meterId, start, kwh);
            consumptionRepo.save(updated);
        } else {
            var created = createConsumptionWithId(type, null, meterId, start, kwh);
            consumptionRepo.save(created);
        }
    }

    private Consumption createConsumptionWithId(ConsumptionType type, UUID id,
                                                UUID meterId, OffsetDateTime start, BigDecimal kwh) {
        return switch (type) {
            case MINUTELY -> Consumption.minutely(id, meterId, start, kwh);
            case HOURLY   -> Consumption.hourly(id,   meterId, start, kwh);
            case DAILY    -> Consumption.daily(id,    meterId, start, kwh);
            case MONTHLY  -> Consumption.monthly(id,  meterId, start, kwh);
        };
    }

    private static OffsetDateTime truncateHour(OffsetDateTime t)  { return t.truncatedTo(ChronoUnit.HOURS); }
    private static OffsetDateTime truncateDay(OffsetDateTime t)   { return t.truncatedTo(ChronoUnit.DAYS); }
    private static OffsetDateTime truncateMonth(OffsetDateTime t) {
        var d = t.truncatedTo(ChronoUnit.DAYS);
        return d.withDayOfMonth(1);
    }
}
