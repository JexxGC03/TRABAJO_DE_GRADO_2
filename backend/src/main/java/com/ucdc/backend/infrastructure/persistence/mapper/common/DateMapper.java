package com.ucdc.backend.infrastructure.persistence.mapper.common;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DateMapper {

    @Named("toOffset") // LocalDateTime -> OffsetDateTime
    public OffsetDateTime toOffset(LocalDateTime value) {
        return (value == null) ? null : value.atOffset(ZoneOffset.UTC);
    }

    @Named("toLocal") // OffsetDateTime -> LocalDateTime
    public LocalDateTime toLocal(OffsetDateTime value) {
        return (value == null) ? null : value.toLocalDateTime();
    }
}
