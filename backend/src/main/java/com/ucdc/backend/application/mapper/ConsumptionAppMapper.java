package com.ucdc.backend.application.mapper;

import com.ucdc.backend.application.dto.consumption.GetAnnualConsumptionResult;
import com.ucdc.backend.application.dto.consumption.GetConsumptionChartResult;
import com.ucdc.backend.application.dto.consumption.GetMonthlyConsumptionResult;
import com.ucdc.backend.domain.repositories.ConsumptionRepository;
import com.ucdc.backend.domain.value.ConsumptionDailyPoint;
import com.ucdc.backend.domain.value.ConsumptionMonthlyPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsumptionAppMapper {
    /* Daily points → Monthly result series */
    @Mapping(target = "date", source = "date")
    @Mapping(target = "kwh",  source = "kwh")
    GetMonthlyConsumptionResult.PointDTO toMonthlyPointDTO(ConsumptionDailyPoint src);

    /* Monthly points → Annual result series */
    @Mapping(target = "month", source = "month")
    @Mapping(target = "kwh",   source = "kwh")
    GetAnnualConsumptionResult.MonthlyDTO toAnnualMonthlyDTO(ConsumptionMonthlyPoint src);
    List<GetAnnualConsumptionResult.MonthlyDTO> toAnnualMonthlyDTOs(List<ConsumptionRepository.MonthlyPoint> src);


    // MONTHLY result (lista de puntos diarios del mes)
    ConsumptionDailyPoint toDto(ConsumptionRepository.DailyPoint p);
    List<ConsumptionDailyPoint> toMonthlyPointDTOs(List<ConsumptionRepository.DailyPoint> points);

    // Chart DAILY
    GetConsumptionChartResult.ChartDailyPoint toChartDto(ConsumptionRepository.DailyPoint p);
    List<GetConsumptionChartResult.ChartDailyPoint> toChartDailies(List<ConsumptionRepository.DailyPoint> points);

    // Chart MONTHLY
    GetConsumptionChartResult.ChartMonthlyPoint toChartDto(ConsumptionRepository.MonthlyPoint p);
    List<GetConsumptionChartResult.ChartMonthlyPoint> toChartMonthlies(List<ConsumptionRepository.MonthlyPoint> points);
}
