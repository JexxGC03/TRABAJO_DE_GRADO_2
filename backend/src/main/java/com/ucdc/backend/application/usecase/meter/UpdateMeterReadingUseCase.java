package com.ucdc.backend.application.usecase.meter;

import com.ucdc.backend.application.dto.meter.UpdateMeterReadingCommand;
import com.ucdc.backend.application.dto.meter.UpdateMeterReadingResult;

public interface UpdateMeterReadingUseCase {
    UpdateMeterReadingResult handle(UpdateMeterReadingCommand command);
}
