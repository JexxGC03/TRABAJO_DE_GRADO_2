package com.ucdc.backend.application.usecase.consumption;

import com.ucdc.backend.application.dto.consumption.GetAnnualConsumptionCommand;
import com.ucdc.backend.application.dto.consumption.GetAnnualConsumptionResult;

public interface GetAnnualConsumptionUseCase {
    GetAnnualConsumptionResult handle(GetAnnualConsumptionCommand command);
}
