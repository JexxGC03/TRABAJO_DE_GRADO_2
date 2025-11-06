package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.value.PasswordCredential;
import com.ucdc.backend.infrastructure.persistence.entity.PasswordCredentialEntity;
import com.ucdc.backend.infrastructure.persistence.entity.UserEntity;
import com.ucdc.backend.infrastructure.persistence.mapper.common.DateMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface CredentialJpaMapper {

    /* ===== Entity -> Domain ===== */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "toOffset")
    PasswordCredential toDomain(PasswordCredentialEntity e);

    /* ===== Domain -> Entity ===== */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "user", ignore = true)
    PasswordCredentialEntity toEntity(PasswordCredential domain);

    /* ===== Helpers ===== */
    default UserEntity toUserRef(UUID userId) {
        if (userId == null) return null;
        var u = new UserEntity();
        u.setId(userId);
        return u;
    }
}
