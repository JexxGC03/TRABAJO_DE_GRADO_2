package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.value.MeterQuotaVersion;
import com.ucdc.backend.infrastructure.persistence.entity.MeterQuotaHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeterQuotaHistoryJpaMapper {

    @Mapping(target = "meterId", expression = "java(e.getMeter().getId())")
    MeterQuotaVersion toDomain(MeterQuotaHistoryEntity e);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meter", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MeterQuotaHistoryEntity toEntity(MeterQuotaVersion d);
}
