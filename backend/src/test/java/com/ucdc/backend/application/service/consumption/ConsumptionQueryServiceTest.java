package com.ucdc.backend.application.service.consumption;

import com.ucdc.backend.application.dto.consumption.*;
import com.ucdc.backend.application.mapper.ConsumptionAppMapper;
import com.ucdc.backend.application.services.ProjectionServicePort;
import com.ucdc.backend.application.services.consumption.ConsumptionQueryService;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.repositories.MeterRepository;
import com.ucdc.backend.domain.value.ConsumptionDailyPoint;
import com.ucdc.backend.domain.value.ConsumptionMonthlyPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsumptionQueryServiceTest {
    private MeterRepository meterRepository;
    private ConsumptionRepositoryPort consumptionRepository;
    private ProjectionServicePort projectionService;
    private ConsumptionAppMapper mapper;
    private ConsumptionQueryService service;

    private final UUID meterId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        meterRepository = mock(MeterRepository.class);
        consumptionRepository = mock(ConsumptionRepositoryPort.class);
        projectionService = mock(ProjectionServicePort.class);
        mapper = mock(ConsumptionAppMapper.class);

        service = new ConsumptionQueryService(
                meterRepository,
                consumptionRepository,
                projectionService,
                mapper
        );
    }

    /* ========= GetMonthlyConsumption ========= */
    @Test
    void shouldReturnMonthlyConsumptionSuccessfully() {
        // given
        YearMonth period = YearMonth.of(2025, 9);
        var cmd = new GetMonthlyConsumptionCommand(meterId, period);

        when(meterRepository.existsById(meterId)).thenReturn(true);

        var from = period.atDay(1);
        var to = period.atEndOfMonth();

        var points = List.of(
                new ConsumptionDailyPoint(LocalDate.of(2025, 9, 1), new BigDecimal("3.50")),
                new ConsumptionDailyPoint(LocalDate.of(2025, 9, 2), new BigDecimal("3.80"))
        );
        when(consumptionRepository.findDailyByMeterBetween(meterId, from, to)).thenReturn(points);
        when(consumptionRepository.sumByMeterBetween(meterId, from, to)).thenReturn(new BigDecimal("7.30"));

        // mapper mock
        when(mapper.toMonthlyPointDTOs(points)).thenReturn(
                List.of(
                        new GetMonthlyConsumptionResult.PointDTO(LocalDate.of(2025, 9, 1), new BigDecimal("3.50")),
                        new GetMonthlyConsumptionResult.PointDTO(LocalDate.of(2025, 9, 2), new BigDecimal("3.80"))
                )
        );

        // when
        var result = service.handle(cmd);

        // then
        assertNotNull(result);
        assertEquals(meterId, result.meterId());
        assertEquals(new BigDecimal("7.30"), result.totalKwh());
        assertEquals(2, result.dailySeries().size());
        verify(consumptionRepository).findDailyByMeterBetween(meterId, from, to);
    }

    @Test
    void shouldThrowWhenMeterDoesNotExist() {
        var cmd = new GetMonthlyConsumptionCommand(meterId, YearMonth.now());
        when(meterRepository.existsById(meterId)).thenReturn(false);

        var ex = assertThrows(
                NotFoundException.class,
                () -> service.handle(cmd)
        );

        assertEquals("Meter not found with id " + meterId, ex.getMessage());
    }

    /* ========= GetAnnualConsumption ========= */
    @Test
    void shouldReturnAnnualConsumptionSuccessfully() {
        var cmd = new GetAnnualConsumptionCommand(meterId, 2025);
        when(meterRepository.existsById(meterId)).thenReturn(true);

        var monthlyPoints = List.of(
                new ConsumptionMonthlyPoint(1, new BigDecimal("100")),
                new ConsumptionMonthlyPoint(2, new BigDecimal("150"))
        );

        when(consumptionRepository.findMonthlyByMeterAndYear(meterId, 2025)).thenReturn(monthlyPoints);
        when(consumptionRepository.sumByMeterAndYear(meterId, 2025)).thenReturn(new BigDecimal("250"));
        when(consumptionRepository.sumByMeterGroupByYear(eq(meterId), anyInt())).thenReturn(List.of());

        when(mapper.toAnnualMonthlyDTOs(monthlyPoints))
                .thenReturn(List.of(
                        new GetAnnualConsumptionResult.MonthlyDTO(1, new BigDecimal("100")),
                        new GetAnnualConsumptionResult.MonthlyDTO(2, new BigDecimal("150"))
                ));

        var result = service.handle(cmd);

        assertNotNull(result);
        assertEquals(new BigDecimal("250"), result.totalKwh());
        assertEquals(2, result.monthlySeries().size());
    }

    /* ========= GetConsumptionChart ========= */
    @Test
    void shouldReturnDailyChart() {
        var from = LocalDate.of(2025, 9, 1);
        var to = LocalDate.of(2025, 9, 3);
        var cmd = new GetConsumptionChartCommand(meterId, from, to, GetConsumptionChartCommand.Granularity.DAILY);
        when(meterRepository.existsById(meterId)).thenReturn(true);

        var daily = List.of(
                new ConsumptionDailyPoint(LocalDate.of(2025, 9, 1), BigDecimal.ONE)
        );
        when(consumptionRepository.findDailyByMeterBetween(meterId, from, to)).thenReturn(daily);
        when(mapper.toChartDailies(daily)).thenReturn(
                List.of(new GetConsumptionChartResult.PointDTO("2025-09-01", BigDecimal.ONE))
        );

        var result = service.handle(cmd);

        assertEquals("DAILY", result.granularity());
        assertEquals(1, result.series().size());
    }

    /* ========= CompareActualVsProjected ========= */
    @Test
    void shouldCompareRealAndProjected() {
        var cmd = new CompareActualVsProjectedCommand(
                meterId,
                YearMonth.of(2025, 9),
                CompareActualVsProjectedCommand.Granularity.DAILY,
                CompareActualVsProjectedCommand.ProjectionModel.HISTORICAL_MEAN
        );

        when(meterRepository.existsById(meterId)).thenReturn(true);

        var realPoints = List.of(
                new ConsumptionDailyPoint(LocalDate.of(2025, 9, 1), new BigDecimal("10")),
                new ConsumptionDailyPoint(LocalDate.of(2025, 9, 2), new BigDecimal("8"))
        );

        when(consumptionRepository.findDailyByMeterBetween(
                any(UUID.class), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(realPoints);

        var projected = List.of(
                new CompareActualVsProjectedResult.PointDTO("2025-09-01", new BigDecimal("9")),
                new CompareActualVsProjectedResult.PointDTO("2025-09-02", new BigDecimal("9"))
        );
        when(projectionService.project(any(), any(), any(), any())).thenReturn(projected);

        var result = service.handle(cmd);

        assertNotNull(result);
        assertEquals(2, result.real().size());
        assertEquals(2, result.projected().size());
        assertFalse(result.delta().isEmpty());
        verify(projectionService).project(any(), any(), any(), any());
    }
}
