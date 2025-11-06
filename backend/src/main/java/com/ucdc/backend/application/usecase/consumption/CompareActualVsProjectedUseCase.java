package com.ucdc.backend.application.usecase.consumption;

import com.ucdc.backend.application.dto.consumption.CompareActualVsProjectedCommand;
import com.ucdc.backend.application.dto.consumption.CompareActualVsProjectedResult;

public interface CompareActualVsProjectedUseCase {
    CompareActualVsProjectedResult handle(CompareActualVsProjectedCommand command);
}
