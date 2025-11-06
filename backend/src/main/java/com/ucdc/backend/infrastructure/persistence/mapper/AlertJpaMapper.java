package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.infrastructure.persistence.entity.AlertEntity;
import com.ucdc.backend.infrastructure.persistence.entity.MeterEntity;
import org.mapstruct.*;

import java.time.YearMonth;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AlertJpaMapper {

    /* ========= Domain → Entity ========= */
    @Mapping(target = "id",           expression = "java(domain.id())")
    @Mapping(target = "meter",        expression = "java(toMeterRef(domain.meterId()))")
    @Mapping(target = "thresholdKwh", expression = "java(domain.thresholdKwh())")
    @Mapping(target = "currentKwh",   expression = "java(domain.currentKwh())")
    @Mapping(target = "status",       expression = "java(domain.status())")
    @Mapping(target = "type",         expression = "java(domain.type())")
    @Mapping(target = "granularity",  expression = "java(domain.granularity().name())")
    @Mapping(target = "period",       expression = "java(domain.period().toString())")
    @Mapping(target = "createdAt",    expression = "java(domain.createdAt())")
    @Mapping(target = "updatedAt",    expression = "java(domain.updatedAt())")
    AlertEntity toEntity(Alert domain);

    /* ========= Entity → Domain ========= */
    @ObjectFactory
    default Alert from(AlertEntity e) {
        UUID meterId = e.getMeter() != null ? e.getMeter().getId() : null;
        return Alert.rehydrate(
                e.getId(),
                meterId,
                e.getThresholdKwh(),
                e.getCurrentKwh(),
                e.getStatus(),
                e.getType(),
                e.getReason(),
                Alert.Granularity.valueOf(e.getGranularity()),
                YearMonth.parse(e.getPeriod()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    @Named("mapOne")
    default Alert toDomain(AlertEntity e) { return from(e); }

    @IterableMapping(qualifiedByName = "mapOne")
    default java.util.List<Alert> toDomainList(java.util.List<AlertEntity> entities) {
        return entities.stream().map(this::from).toList();
    }

    /* ========= Helper: stub MeterEntity con solo id ========= */
    default MeterEntity toMeterRef(UUID id) {
        if (id == null) return null;
        MeterEntity m = new MeterEntity();
        m.setId(id);
        return m;
    }
}
