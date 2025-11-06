package com.ucdc.backend.infrastructure.persistence.mapper;

import com.ucdc.backend.domain.model.Recommendation;
import com.ucdc.backend.infrastructure.persistence.entity.RecommendationEntity;
import com.ucdc.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RecommendationJpaMapper {

    /* ======= Domain → Entity ======= */
    @Mapping(target = "id",        expression = "java(domain.id())")
    @Mapping(target = "user",      expression = "java(toUserRef(domain.userId()))")
    @Mapping(target = "message",   expression = "java(domain.message())")
    @Mapping(target = "status",    expression = "java(domain.status())")
    @Mapping(target = "createdAt", expression = "java(domain.createdAt())")
    @Mapping(target = "updatedAt", expression = "java(domain.updatedAt())")
    RecommendationEntity toEntity(Recommendation domain);

    /* ======= Entity → Domain (rehidratación) ======= */
    @ObjectFactory
    default Recommendation from(RecommendationEntity e) {
        UUID userId = e.getUser() != null ? e.getUser().getId() : null;
        return Recommendation.rehydrate(
                e.getId(),
                userId,
                e.getMessage(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    @Named("mapOne")
    default Recommendation toDomain(RecommendationEntity e) { return from(e); }

    @IterableMapping(qualifiedByName = "mapOne")
    default java.util.List<Recommendation> toDomainList(java.util.List<RecommendationEntity> entities) {
        return entities.stream().map(this::from).toList();
    }

    /* ======= Helper: stub de UserEntity por id ======= */
    default UserEntity toUserRef(UUID id) {
        if (id == null) return null;
        UserEntity u = new UserEntity();
        u.setId(id);                  // solo id; JPA manejará el proxy
        return u;
    }
}
