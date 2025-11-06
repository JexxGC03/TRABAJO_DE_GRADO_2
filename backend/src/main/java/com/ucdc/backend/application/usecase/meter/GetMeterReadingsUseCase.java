package com.ucdc.backend.application.usecase.meter;

import com.ucdc.backend.application.dto.meter.GetMeterReadingsQuery;
import com.ucdc.backend.application.dto.meter.GetMeterReadingsResult;

public interface GetMeterReadingsUseCase {
    GetMeterReadingsResult handle(GetMeterReadingsQuery query);
}
