package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.model.Consumption;
import com.ucdc.backend.infrastructure.persistence.entity.ConsumptionEntity;
import com.ucdc.backend.infrastructure.persistence.entity.MeterEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ConsumptionJpaMapper {

    /* ========= Entity → Domain (usa fábricas del dominio) ========= */
    default Consumption toDomain(ConsumptionEntity e) {
        if (e == null) return null;

        UUID meterId = e.getMeter() != null ? e.getMeter().getId() : null;

        return switch (e.getConsumptionType()) {
            case MINUTELY -> Consumption.minutely(e.getId(), meterId, e.getPeriodStart(), e.getKwh());
            case HOURLY   -> Consumption.hourly(e.getId(),  meterId, e.getPeriodStart(), e.getKwh());
            case DAILY    -> Consumption.daily(e.getId(),   meterId, e.getPeriodStart(), e.getKwh());
            case MONTHLY  -> Consumption.monthly(e.getId(), meterId, e.getPeriodStart(), e.getKwh());
        };
    }

    /* (opcional) batch */
    default List<Consumption> toDomain(List<ConsumptionEntity> entities) {
        return entities == null ? null : entities.stream().map(this::toDomain).toList();
    }

    /* ========= Domain → Entity (MapStruct) ========= */
    @Mapping(target = "id",            source = "id")
    @Mapping(target = "meter",         expression = "java(toMeterRef(domain.getMeterId()))")
    @Mapping(target = "consumptionType", source = "type")
    @Mapping(target = "periodStart",   source = "periodStart")
    @Mapping(target = "kwh",           source = "kwh")
    ConsumptionEntity toEntity(Consumption domain);

    /* ========= Helper ========= */
    default MeterEntity toMeterRef(UUID meterId) {
        if (meterId == null) return null;
        MeterEntity m = new MeterEntity();
        m.setId(meterId);
        return m;
    }
}
