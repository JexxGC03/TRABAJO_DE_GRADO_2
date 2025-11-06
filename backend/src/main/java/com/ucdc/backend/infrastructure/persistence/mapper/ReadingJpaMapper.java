package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.value.Reading;
import com.ucdc.backend.infrastructure.persistence.entity.MeterReadingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ReadingJpaMapper {

    /* ========= Domain → Entity ========= */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meterId", source = "meterId") // record: Reading.meterId()
    @Mapping(target = "ts", source = "timestamp", qualifiedByName = "toUtc")
    MeterReadingEntity toEntity(Reading domain);

    /* ========= Entity → Domain ========= */
    @Mapping(target = "meterId", source = "meterId")
    @Mapping(target = "timestamp", source = "ts") // ya es OffsetDateTime
    Reading toDomain(MeterReadingEntity entity);

    /* ========= Helpers ========= */
    @Named("toUtc")
    default OffsetDateTime toUtc(OffsetDateTime odt) {
        return odt == null ? null : odt.withOffsetSameInstant(ZoneOffset.UTC);
    }
}
