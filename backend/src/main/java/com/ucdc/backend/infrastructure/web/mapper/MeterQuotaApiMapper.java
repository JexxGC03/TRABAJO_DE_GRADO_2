package com.ucdc.backend.infrastructure.web.mapper;

import com.ucdc.backend.application.dto.quota.MeterQuotaCommand;
import com.ucdc.backend.application.dto.quota.MeterQuotaResult;
import com.ucdc.backend.infrastructure.web.dto.quota.QuotaRequest;
import com.ucdc.backend.infrastructure.web.dto.quota.QuotaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeterQuotaApiMapper {

    @Mapping(target = "kwhLimit", source = "kwhLimit")
    @Mapping(target = "periodicity", source = "periodicity")
    MeterQuotaCommand toCommand(QuotaRequest req);

    @Mapping(target = "meterId",    expression = "java(res.meterId().toString())")
    @Mapping(target = "periodicity", expression = "java(res.periodicity().name())")
    QuotaResponse toResponse(MeterQuotaResult res);
}
