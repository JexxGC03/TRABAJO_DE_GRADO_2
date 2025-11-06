package com.ucdc.backend.application.mapper;

import com.ucdc.backend.application.dto.meter.MeterCardDto;
import com.ucdc.backend.application.dto.meter.ReadingDto;
import com.ucdc.backend.application.dto.meter.RegisterMeterCommand;
import com.ucdc.backend.application.dto.meter.RegisterMeterResult;
import com.ucdc.backend.domain.model.Meter;
import com.ucdc.backend.domain.value.Reading;
import com.ucdc.backend.infrastructure.persistence.entity.MeterEntity;
import com.ucdc.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MeterAppMapper {

    /* ===== Entity → Domain ===== */
    default Meter toDomain(RegisterMeterCommand cmd) {
        return Meter.register(
                cmd.userId(),
                cmd.serialNumber(),
                cmd.provider(),
                cmd.installationAddress(),
                cmd.serviceNumber(),
                cmd.alias()
        );
    }

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

    /* ===== App DTOs ↔ Domain ===== */
    default ReadingDto toDto(Reading r) {
        if (r == null) return null;
        // Evitamos que MapStruct busque un ctor público en el dominio
        return new ReadingDto(r.timestamp(), r.kwhAccum());
    }

    default RegisterMeterResult toResult(Meter meter) {
        if (meter == null) return null;
        return new RegisterMeterResult(
                meter.id(),
                meter.serialNumber(),
                meter.provider(),
                meter.serviceNumber(),
                meter.installationAddress(),
                meter.alias(),
                meter.createdAt()
        );
    }

    default MeterCardDto toCardDto(Meter meter) {
        if (meter == null) return null;
        return new MeterCardDto(
                meter.id(),
                meter.alias(),
                meter.installationAddress(),
                meter.serialNumber(),
                meter.status(),
                meter.type(),
                meter.provider()
        );
    }

    /** Helper opcional para listas (también null-safe). */
    default List<MeterCardDto> toCardDtos(List<Meter> meters) {
        if (meters == null) return List.of();
        return meters.stream()
                .filter(Objects::nonNull)
                .map(this::toCardDto)
                .toList();
    }

    /* ===== Helpers ===== */
    default UserEntity toUserRef(UUID userId) {
        if (userId == null) return null;
        var u = new UserEntity();
        u.setId(userId);
        return u;
    }

    default OffsetDateTime toOffset(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime toLocal(OffsetDateTime odt) {
        return odt == null ? null : odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}
