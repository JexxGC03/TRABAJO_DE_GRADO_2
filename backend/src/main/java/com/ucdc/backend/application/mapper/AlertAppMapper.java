package com.ucdc.backend.application.mapper;

import com.ucdc.backend.application.dto.alert.GenerateAlertCommand;
import com.ucdc.backend.application.dto.alert.GenerateAlertResult;
import com.ucdc.backend.domain.enums.AlertType;
import com.ucdc.backend.domain.model.Alert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import java.time.YearMonth;
import java.util.UUID;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AlertAppMapper {

    /* ========= Command → Domain ========= */
    @ObjectFactory
    default Alert toDomain(GenerateAlertCommand cmd) {
        // Genera un nuevo Alert.ACTIVE con ID aleatorio
        return Alert.create(
                UUID.randomUUID(),
                cmd.meterId(),
                cmd.thresholdKwh(),
                cmd.currentKwh(),
                AlertType.EARLY_THRESHOLD,
                cmd.reason() != null ? cmd.reason() : "Alerta temprana: ≥80% del umbral",
                cmd.granularity(),
                YearMonth.from(cmd.timestamp())
        );
    }

    /* ========= Domain → Result ========= */
    @Mapping(target = "alertId", expression = "java(alert.id())")
    @Mapping(target = "status",  expression = "java(alert.status().name())")
    @Mapping(target = "message", expression = "java(alert.toString())")
    GenerateAlertResult toResult(Alert alert);
}
