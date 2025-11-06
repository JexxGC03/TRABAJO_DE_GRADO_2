package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.model.MeterQuota;
import com.ucdc.backend.infrastructure.persistence.entity.MeterEntity;
import com.ucdc.backend.infrastructure.persistence.entity.MeterQuotaEntity;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MeterQuotaJpaMapper {


    MeterQuotaEntity toEntity(MeterQuota quota);

    @ObjectFactory
    default MeterQuota rehydrate(MeterQuotaEntity e) {
        if (e == null) return null;
        return MeterQuota.rehydrate(
                e.getId(),
                e.getMeterId(),
                MeterQuota.Periodicity.valueOf(e.getPeriodicity()),
                e.getKwhLimit(),
                e.getValidFrom(),
                e.getValidTo()
        );
    }
    MeterQuota toDomain(MeterQuotaEntity e);


    /* ========= Helper: stub MeterEntity con solo id ========= */
    default MeterEntity toMeterRef(UUID id) {
        if (id == null) return null;
        MeterEntity m = new MeterEntity();
        m.setId(id);
        return m;
    }
}
