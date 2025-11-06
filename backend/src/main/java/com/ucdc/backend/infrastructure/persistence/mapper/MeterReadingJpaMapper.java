package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.value.Reading;
import com.ucdc.backend.infrastructure.persistence.entity.MeterReadingEntity;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel="spring")
public interface MeterReadingJpaMapper {

    /* ===== Entity → Domain ===== */
    default Reading toDomain(MeterReadingEntity e) {
        if (e == null) return null;
        UUID meterId = e.getMeterId() != null ? e.getMeterId() : null;
        return new Reading(
                meterId,
                e.getTs(),
                e.getKwhAccum()
        );
    }

    /* ===== Domain → Entity ===== */
    default MeterReadingEntity toEntity(Reading d) {
        if (d == null) return null;
        var e = new MeterReadingEntity();
        e.setMeterId(d.meterId());
        e.setTs(d.timestamp());
        e.setKwhAccum(d.kwhAccum());
        return e;
    }
}
