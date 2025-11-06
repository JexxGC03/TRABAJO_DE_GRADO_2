package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.value.RefreshSession;
import com.ucdc.backend.infrastructure.persistence.entity.RefreshSessionEntity;
import com.ucdc.backend.infrastructure.persistence.entity.UserEntity;
import com.ucdc.backend.infrastructure.persistence.mapper.common.DateMapper;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = "spring",  uses = {DateMapper.class})
public interface SessionJpaMapper {

    /* ===== Entity -> Domain ===== */
    @Mappings({
            @Mapping(target = "userId",    source = "user.id"),
            @Mapping(target = "refreshToken",     source = "refreshToken"),
            @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toOffset"),
            @Mapping(target = "expiresAt", source = "expiresAt", qualifiedByName = "toOffset")
    })
    RefreshSession toDomain(RefreshSessionEntity e);

    /* ===== Domain -> Entity ===== */
    @Mappings({
            @Mapping(target = "user",         expression = "java(toUserRef(domain.userId()))"),
            @Mapping(target = "refreshToken", source = "refreshToken"),
            @Mapping(target = "createdAt",    source = "createdAt", qualifiedByName = "toLocal"),
            @Mapping(target = "expiresAt", source = "expiresAt", qualifiedByName = "toLocal")
    })
    RefreshSessionEntity toEntity(RefreshSession domain);

    /* ===== Helpers ===== */
    default UserEntity toUserRef(UUID userId) {
        if (userId == null) return null;
        var u = new UserEntity();
        u.setId(userId);
        return u;
    }
}
