package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.model.Meter;
import com.ucdc.backend.infrastructure.persistence.entity.MeterEntity;
import com.ucdc.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeterJpaMapper {

    /* ===== Entity → Domain (usa factory, respeta campos final) ===== */
    default Meter toDomain(MeterEntity e) {
        if (e == null) return null;
        return Meter.rehydrate(
                e.getId(),
                e.getUser() != null ? e.getUser().getId() : null,
                e.getSerialNumber(),
                e.getType(),
                e.getStatus(),
                e.getProvider(),
                e.getInstallationAddress(),
                e.getServiceNumber(),
                e.getAlias(),
                toOffset(e.getInstalledAt()),
                null, // createdAt (factory lo resuelve si null)
                null  // updatedAt
        );
    }

    /* ===== Domain → Entity (manual por getters no JavaBean) ===== */
    default MeterEntity toEntity(Meter d) {
        if (d == null) return null;
        var e = new MeterEntity();
        e.setId(d.id());
        e.setUser(toUserRef(d.userId()));
        e.setSerialNumber(d.serialNumber());
        e.setType(d.type());
        e.setStatus(d.status());
        e.setProvider(d.provider());
        e.setServiceNumber(d.serviceNumber());
        e.setInstallationAddress(d.installationAddress());
        e.setInstalledAt(toLocal(d.installedAt()));
        e.setAlias(d.alias());
        // timestamps (si los llevas en dominio)
        e.setCreatedAt(toLocal(d.createdAt()));
        e.setUpdatedAt(toLocal(d.updatedAt()));
        return e;
    }

    /* ===== Helpers ===== */
    default UserEntity toUserRef(java.util.UUID userId) {
        if (userId == null) return null;
        var u = new UserEntity();
        u.setId(userId);
        return u;
    }
    static java.time.OffsetDateTime toOffset(java.time.LocalDateTime ldt) {
        return ldt == null ? null : ldt.atOffset(java.time.ZoneOffset.UTC);
    }
    static java.time.LocalDateTime toLocal(java.time.OffsetDateTime odt) {
        return odt == null ? null : odt.withOffsetSameInstant(java.time.ZoneOffset.UTC).toLocalDateTime();
    }
}
