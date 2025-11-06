package com.ucdc.backend.application.usecase.meter;

import com.ucdc.backend.application.dto.meter.ListMyMetersQuery;
import com.ucdc.backend.application.dto.meter.ListMyMetersResult;

public interface ListMyMetersUseCase {
    ListMyMetersResult handle(ListMyMetersQuery query);
}
