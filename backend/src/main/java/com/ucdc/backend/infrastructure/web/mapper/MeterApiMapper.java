package com.ucdc.backend.infrastructure.web.mapper;

import com.ucdc.backend.application.dto.meter.MeterCardDto;
import com.ucdc.backend.application.dto.meter.RegisterMeterCommand;
import com.ucdc.backend.application.dto.meter.RegisterMeterResult;
import com.ucdc.backend.application.dto.meter.UpdateMeterCommand;
import com.ucdc.backend.domain.enums.Provider;
import com.ucdc.backend.infrastructure.web.dto.meter.MeterItemResponse;
import com.ucdc.backend.infrastructure.web.dto.meter.MeterResponse;
import com.ucdc.backend.infrastructure.web.dto.meter.MeterUpdateRequest;
import com.ucdc.backend.infrastructure.web.dto.meter.RegisterMeterRequest;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring",  uses = {ProviderMapper.class})
public interface MeterApiMapper {

    // API -> App
    @Mapping(target = "userId", expression = "java(userId)")
    @Mapping(target = "serialNumber", source = "serialNumber")
    @Mapping(target = "provider", source = "provider")
    @Mapping(target = "serviceNumber", source = "serviceNumber")
    @Mapping(target = "installationAddress", source = "installationAddress")
    @Mapping(target = "alias", source = "alias")
    RegisterMeterCommand toCommand(RegisterMeterRequest request, @Context UUID userId);

    // App -> API
    @Mapping(target = "id", source = "id")
    @Mapping(target = "serialNumber", source = "serialNumber")
    @Mapping(target="provider", source="provider")
    @Mapping(target = "serviceNumber", source = "serviceNumber")
    @Mapping(target = "installationAddress", source = "installationAddress")
    @Mapping(target = "alias", source = "alias")
    @Mapping(target = "createdAt", source = "createdAt")
    MeterResponse toResponse(RegisterMeterResult result);

    @Mapping(target = "alias", source = "alias")
    @Mapping(target = "installationAddress", source = "installationAddress")
    @Mapping(target = "serialNumber", source = "serialNumber")
    @Mapping(target = "status", expression = "java(dto.status().name())")
    @Mapping(target = "provider", expression = "java(dto.provider().name())")
    MeterItemResponse toInmuebleItem(MeterCardDto dto);

    // App -> Web
    @Mapping(target = "id",                  source = "id")
    @Mapping(target = "serialNumber",        source = "serialNumber")
    @Mapping(target = "provider",            expression = "java(dto.provider().name())")
    @Mapping(target = "installationAddress", source = "installationAddress")
    @Mapping(target = "alias",               source = "alias")
    @Mapping(target = "status",              expression = "java(dto.status().name())")
    @Mapping(target = "type",                expression = "java(dto.type().name())")
    MeterItemResponse toMeterItem(MeterCardDto dto);


    // Web -> App (update)
    default UpdateMeterCommand toUpdate(UUID userId, UUID meterId, MeterUpdateRequest r) {
        return new UpdateMeterCommand(
                userId,
                meterId,
                r.alias(),
                r.installationAddress(),
                r.serialNumber(),
                Provider.valueOf(r.provider())
        );
    }
}
