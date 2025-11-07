package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.value.PasswordCredential;
import com.ucdc.backend.infrastructure.persistence.entity.PasswordCredentialEntity;
import com.ucdc.backend.infrastructure.persistence.mapper.CredentialJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaPasswordCredentialRepository;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class PasswordCredentialRepositoryAdapter implements PasswordCredentialRepository {

    private final JpaPasswordCredentialRepository jpa;
    private final JpaUserRepository userJpa;
    private final CredentialJpaMapper mapper;

    @Override
    public Optional<PasswordCredential> findByUserId(UUID userId) {
        return jpa.findById(userId).map(mapper::toDomain);
    }

    @Override
    public PasswordCredential save(PasswordCredential credential) {
        var userId = credential.userId();

        if (jpa.existsById(userId)) {
            // === UPDATE IN-PLACE (ENTIDAD YA MANAGED) ===
            var e = jpa.getReferenceById(userId);
            e.setPasswordHash(credential.passwordHash());
            e.setUpdatedAt(credential.updatedAt().toLocalDateTime());
            // no llames a save: ya está administrada; devuelve mapeo
            return mapper.toDomain(e);
        } else {
            // === CREATE ===
            var e = mapper.toEntity(credential);
            // Si usas @MapsId, debes setear la referencia al User
            e.setUser(userJpa.getReferenceById(userId));
            var saved = jpa.save(e);
            return mapper.toDomain(saved);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpa.deleteById(userId);
    }
}
