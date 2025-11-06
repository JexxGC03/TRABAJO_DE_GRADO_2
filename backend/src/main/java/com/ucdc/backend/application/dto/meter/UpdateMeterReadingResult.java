package com.ucdc.backend.application.dto.meter;

import com.ucdc.backend.domain.enums.ReadingIngestStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdateMeterReadingResult(
        UUID meterId,
        OffsetDateTime timestamp,
        ReadingIngestStatus status,
        String reason // null en éxito
) {}
