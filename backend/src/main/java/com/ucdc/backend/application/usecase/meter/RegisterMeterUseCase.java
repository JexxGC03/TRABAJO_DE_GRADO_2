package com.ucdc.backend.application.usecase.meter;

import com.ucdc.backend.application.dto.meter.RegisterMeterCommand;
import com.ucdc.backend.application.dto.meter.RegisterMeterResult;

public interface RegisterMeterUseCase {
    RegisterMeterResult handle(RegisterMeterCommand command);
}
