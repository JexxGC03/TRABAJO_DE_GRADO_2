package com.ucdc.backend.application.dto.meter;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetMeterReadingsQuery(
        UUID meterId,
        OffsetDateTime from,
        OffsetDateTime to,
        Integer page,
        Integer size
) {
}
