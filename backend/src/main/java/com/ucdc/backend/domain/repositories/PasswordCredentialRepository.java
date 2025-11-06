package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.value.PasswordCredential;

import java.util.Optional;
import java.util.UUID;

public interface PasswordCredentialRepository {
    Optional<String> findByUserId(UUID userId);
    PasswordCredential save(PasswordCredential credential);
    void deleteByUserId(UUID userId);
}
