package com.ucdc.backend.application.usecase.meter;

import com.ucdc.backend.application.dto.meter.MeterCardDto;
import com.ucdc.backend.application.dto.meter.UpdateMeterCommand;

public interface UpdateMeterUseCase {
    MeterCardDto handle(UpdateMeterCommand cmd);
}
