package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.dto.consumption.*;
import com.ucdc.backend.application.usecase.consumption.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@RestController
@RequestMapping("/api/meters/{meterId}/consumption")
@RequiredArgsConstructor
public class ConsumptionController {

    private final GetMonthlyConsumptionUseCase monthly;
    private final GetAnnualConsumptionUseCase annual;
    private final GetConsumptionChartUseCase chart;
    private final CompareActualVsProjectedUseCase compare;

    @GetMapping("/monthly")
    public GetMonthlyConsumptionResult getMonthly(@PathVariable UUID meterId,
                                                  @RequestParam YearMonth period) {
        return monthly.handle(new GetMonthlyConsumptionCommand(meterId, period));
    }

    @GetMapping("/annual")
    public GetAnnualConsumptionResult getAnnual(@PathVariable UUID meterId,
                                                @RequestParam int year) {
        return annual.handle(new GetAnnualConsumptionCommand(meterId, year));
    }

    @GetMapping("/chart")
    public GetConsumptionChartResult getChart(@PathVariable UUID meterId,
                                              @RequestParam LocalDate from,
                                              @RequestParam LocalDate to,
                                              @RequestParam GetConsumptionChartCommand.Granularity granularity) {
        return chart.handle(new GetConsumptionChartCommand(meterId, from, to, granularity));
    }

    @GetMapping("/compare")
    public CompareActualVsProjectedResult compare(@PathVariable UUID meterId,
                                                  @RequestParam YearMonth period,
                                                  @RequestParam CompareActualVsProjectedCommand.Granularity granularity,
                                                  @RequestParam CompareActualVsProjectedCommand.ProjectionModel model) {
        return compare.handle(new CompareActualVsProjectedCommand(meterId, period, granularity, model));
    }
}
