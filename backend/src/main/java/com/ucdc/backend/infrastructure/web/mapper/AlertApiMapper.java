package com.ucdc.backend.infrastructure.web.mapper;

import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.infrastructure.web.dto.alert.AlertResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlertApiMapper {

    @Mapping(target = "id",          expression = "java(alert.id().toString())")
    @Mapping(target = "meterId",     expression = "java(alert.meterId().toString())")
    @Mapping(target = "status",      expression = "java(alert.status().name())")
    @Mapping(target = "type",        expression = "java(alert.type().name())")
    @Mapping(target = "granularity", expression = "java(alert.granularity().name())")
    @Mapping(target = "period",      expression = "java(alert.period().toString())")
    AlertResponse toResponse(Alert alert);
}
