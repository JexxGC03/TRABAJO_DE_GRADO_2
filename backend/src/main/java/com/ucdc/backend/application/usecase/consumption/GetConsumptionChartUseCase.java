package com.ucdc.backend.application.usecase.consumption;

import com.ucdc.backend.application.dto.consumption.GetConsumptionChartCommand;
import com.ucdc.backend.application.dto.consumption.GetConsumptionChartResult;

public interface GetConsumptionChartUseCase {
    GetConsumptionChartResult handle(GetConsumptionChartCommand command);
}
