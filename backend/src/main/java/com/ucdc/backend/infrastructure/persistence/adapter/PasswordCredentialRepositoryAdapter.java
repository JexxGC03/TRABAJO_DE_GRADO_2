package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.value.PasswordCredential;
import com.ucdc.backend.infrastructure.persistence.entity.PasswordCredentialEntity;
import com.ucdc.backend.infrastructure.persistence.mapper.CredentialJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaPasswordCredentialRepository;
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
    private final CredentialJpaMapper mapper;

    @Override
    public Optional<PasswordCredential> findByUserId(UUID userId) {
        return jpa.findById(userId).map(mapper::toDomain);
    }

    @Override
    public PasswordCredential save(PasswordCredential credential) {
        var entity = mapper.toEntity(credential);
        var saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpa.deleteById(userId);
    }
}
