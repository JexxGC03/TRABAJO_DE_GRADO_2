package com.ucdc.backend.application.dto.consumption;

import java.time.YearMonth;
import java.util.UUID;

public record CompareActualVsProjectedCommand(
        UUID meterId,
        YearMonth period,
        Granularity granularity,
        ProjectionModel model
) {
    public enum Granularity { DAILY, MONTHLY }
    public enum ProjectionModel { HISTORICAL_MEAN }
}
