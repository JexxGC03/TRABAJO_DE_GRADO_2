package com.ucdc.backend.application.dto.meter;

import java.util.List;
import java.util.UUID;

public record GetMeterReadingsResult(
        UUID meterId,
        List<ReadingDto> readings,
        long total
) {
}
