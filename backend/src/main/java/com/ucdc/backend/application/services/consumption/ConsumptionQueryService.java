package com.ucdc.backend.application.services.consumption;

import com.ucdc.backend.application.dto.consumption.*;
import com.ucdc.backend.application.mapper.ConsumptionAppMapper;
import com.ucdc.backend.application.services.ProjectionServicePort;
import com.ucdc.backend.application.usecase.consumption.*;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.repositories.ConsumptionRepository;
import com.ucdc.backend.domain.repositories.MeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumptionQueryService implements
        GetMonthlyConsumptionUseCase,
        GetAnnualConsumptionUseCase,
        GetConsumptionChartUseCase,
        CompareActualVsProjectedUseCase {

    private final MeterRepository meterRepository;
    private final ConsumptionRepository consumptionRepository;
    private final ProjectionServicePort projectionService; // opcional (solo para comparar)
    private final ConsumptionAppMapper mapper;

    /* ====================== Helpers ====================== */
    private void assertMeterExists(java.util.UUID meterId) {
        if (meterId == null) throw new IllegalArgumentException("meterId is required");
        if (!meterRepository.existsById(meterId)) {
            throw new NotFoundException("Meter", meterId);
        }
    }

    private static BigDecimal zeroIfNull(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    /* =================== Monthly =================== */
    @Override
    public GetMonthlyConsumptionResult handle(GetMonthlyConsumptionCommand command) {
        if (command == null || command.period() == null)
            throw new IllegalArgumentException("period is required");
        assertMeterExists(command.meterId());

        YearMonth ym = command.period();
        LocalDate from = ym.atDay(1);
        LocalDate to   = ym.atEndOfMonth();

        var daily = consumptionRepository.findDailyByMeterBetween(command.meterId(), from, to);
        var total = consumptionRepository.sumByMeterBetween(command.meterId(), from, to);

        return new GetMonthlyConsumptionResult(
                command.meterId(),
                command.period(),
                zeroIfNull(total),
                mapper.toMonthlyPointDTOs(daily)
        );
    }

    /* =================== Annual =================== */
    @Override
    public GetAnnualConsumptionResult handle(GetAnnualConsumptionCommand command) {
        if (command == null) throw new IllegalArgumentException("command is required");
        assertMeterExists(command.meterId());

        var monthly = consumptionRepository.findMonthlyByMeterAndYear(command.meterId(), command.year());
        var total   = consumptionRepository.sumByMeterAndYear(command.meterId(), command.year());
        var hist    = consumptionRepository.sumByMeterGroupByYear(command.meterId(), 2); // ajusta ventana histórica

        var histDTOs = hist.stream()
                .map(h -> new GetAnnualConsumptionResult.HistoricalYearDTO(h.year(), h.totalKwh()))
                .toList();

        return new GetAnnualConsumptionResult(
                command.meterId(),
                command.year(),
                zeroIfNull(total),
                mapper.toAnnualMonthlyDTOs(monthly),
                histDTOs
        );
    }

    /* =================== Compare Actual vs Projected =================== */
    @Override
    public CompareActualVsProjectedResult handle(CompareActualVsProjectedCommand command) {
        if (command == null || command.period() == null || command.granularity() == null)
            throw new IllegalArgumentException("period and granularity are required");
        assertMeterExists(command.meterId());
        if (projectionService == null) {
            throw new UnsupportedOperationException("Projection service not configured");
        }

        var label = command.period().toString();
        var from  = command.period().atDay(1);
        var to    = command.period().atEndOfMonth();

        var real = switch (command.granularity()) {
            case DAILY -> consumptionRepository.findDailyByMeterBetween(command.meterId(), from, to)
                    .stream().map(p -> new CompareActualVsProjectedResult.PointDTO(p.date().toString(), p.kwh()))
                    .toList();
            case MONTHLY -> consumptionRepository.findMonthlyByMeterAndYear(command.meterId(), command.period().getYear())
                    .stream().map(p -> new CompareActualVsProjectedResult.PointDTO(String.format("%02d", p.month()), p.kwh()))
                    .toList();
        };

        var projected = projectionService.project(
                command.meterId(),
                command.period(),
                command.granularity().name(),
                command.model().name()
        );

        var deltas = new ArrayList<CompareActualVsProjectedResult.DeltaDTO>();
        for (int i = 0; i < Math.min(real.size(), projected.size()); i++) {
            var r = real.get(i).kwh();
            var p = projected.get(i).kwh();
            var diff = r.subtract(p);
            var percent = p.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : diff.multiply(BigDecimal.valueOf(100))
                    .divide(p, 2, java.math.RoundingMode.HALF_UP);
            deltas.add(new CompareActualVsProjectedResult.DeltaDTO(real.get(i).label(), diff, percent));
        }

        var overBudget = deltas.stream().anyMatch(d -> d.kwhDiff().signum() > 0);
        var avgDev = deltas.stream().map(CompareActualVsProjectedResult.DeltaDTO::percent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var deviation = deltas.isEmpty() ? BigDecimal.ZERO
                : avgDev.divide(BigDecimal.valueOf(deltas.size()), 2, java.math.RoundingMode.HALF_UP);

        var summary = new CompareActualVsProjectedResult.SummaryDTO(overBudget, deviation);

        return new CompareActualVsProjectedResult(
                command.meterId(), label, command.granularity().name(), real, projected, deltas, summary
        );
    }

    /* =================== Chart (Daily/Monthly) =================== */
    @Override
    public GetConsumptionChartResult handle(GetConsumptionChartCommand command) {
        if (command == null || command.from() == null || command.to() == null)
            throw new IllegalArgumentException("from and to are required");
        if (command.from().isAfter(command.to()))
            throw new IllegalArgumentException("from must be <= to");
        assertMeterExists(command.meterId());

        return switch (command.granularity()) {
            case DAILY -> {
                var daily = consumptionRepository.findDailyByMeterBetween(command.meterId(), command.from(), command.to());
                yield new GetConsumptionChartResult(
                        command.meterId(), command.from(), command.to(), "DAILY",
                        mapper.toChartDailies(daily)
                );
            }
            case MONTHLY -> {
                var monthly = consumptionRepository.findMonthlyByMeterBetween(command.meterId(), command.from(), command.to());
                yield new GetConsumptionChartResult(
                        command.meterId(), command.from(), command.to(), "MONTHLY",
                        mapper.toChartMonthlies(monthly)
                );
            }
        };
    }
}




