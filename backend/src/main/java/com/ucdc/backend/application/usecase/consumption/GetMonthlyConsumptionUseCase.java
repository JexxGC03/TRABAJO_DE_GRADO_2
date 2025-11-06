package com.ucdc.backend.application.usecase.consumption;

import com.ucdc.backend.application.dto.consumption.GetMonthlyConsumptionCommand;
import com.ucdc.backend.application.dto.consumption.GetMonthlyConsumptionResult;

public interface GetMonthlyConsumptionUseCase {
    GetMonthlyConsumptionResult handle(GetMonthlyConsumptionCommand command);
}
