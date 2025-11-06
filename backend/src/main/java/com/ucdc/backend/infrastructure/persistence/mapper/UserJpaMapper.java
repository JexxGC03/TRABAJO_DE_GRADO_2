package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.enums.Role;
import com.ucdc.backend.domain.model.AdminUser;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.domain.model.User;
import com.ucdc.backend.domain.value.*;
import com.ucdc.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface UserJpaMapper {

    /* ========= Entity → Domain ========= */
    default User toDomain(UserEntity e) {
        if (e == null) return null;

        Phone phone = e.getPhone() != null ? new Phone(e.getPhone()) : null;

        if (e.getRole() == Role.ADMIN) {
            return new AdminUser(
                    e.getId(),
                    e.getFullName(),
                    e.getEmail(),
                    new CitizenId(e.getCitizenId()),
                    new ServiceNumber(e.getServiceNumber()),
                    phone,
                    e.getStatus(),
                    toOffset(e.getCreatedAt()),
                    toOffset(e.getUpdatedAt())
            );
        }
        return new ClientUser(
                e.getId(),
                e.getFullName(),
                e.getEmail(),
                new CitizenId(e.getCitizenId()),
                new ServiceNumber(e.getServiceNumber()),
                phone,
                e.getStatus(),
                toOffset(e.getCreatedAt()),
                toOffset(e.getUpdatedAt())
        );
    }

    /* ========= Domain → Entity ========= */
    default UserEntity toEntity(User d) {
        if (d == null) return null;

        var e = new UserEntity();
        e.setId(d.id());
        e.setFullName(d.fullName());
        e.setEmail(d.email());
        e.setCitizenId(d.citizenId().value());
        e.setServiceNumber(d.serviceNumber().value());
        e.setPhone(d.phone() != null ? d.phone().value() : null);
        e.setRole(d.role());
        e.setStatus(d.status());
        e.setCreatedAt(toLocal(d.createdAt()));
        e.setUpdatedAt(toLocal(d.updatedAt()));
        return e;
    }

    /* ========= Hooks ========= */
    @AfterMapping
    default void ensureTimestamps(@MappingTarget UserEntity e) {
        if (e.getCreatedAt() == null) e.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        if (e.getUpdatedAt() == null) e.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }

    /* ========= Helpers ========= */
    @Named("toOffset")
    default OffsetDateTime toOffset(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atOffset(ZoneOffset.UTC);
    }

    @Named("toLocal")
    default LocalDateTime toLocal(OffsetDateTime odt) {
        return odt == null ? null : odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}
