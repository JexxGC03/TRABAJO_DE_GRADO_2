package com.ucdc.backend.infrastructure.web.mapper;

import com.ucdc.backend.application.usecase.quota.GetActiveMeterQuotaUseCase;
import com.ucdc.backend.infrastructure.web.dto.quota.QuotaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeterQuotaApiMapper {

    @Mapping(target = "quotaId",    expression = "java(res.quotaId().toString())")
    @Mapping(target = "meterId",    ignore = true) // no viene en el Result; opcional llenar desde controlador
    @Mapping(target = "periodicity", expression = "java(res.periodicity().name())")
    @Mapping(target = "kwhLimit",   source = "kwhLimit")
    @Mapping(target = "validFrom",  source = "validFrom")
    @Mapping(target = "validTo",    expression = "java(null)")
    QuotaResponse toResponse(GetActiveMeterQuotaUseCase.Result res);
}
