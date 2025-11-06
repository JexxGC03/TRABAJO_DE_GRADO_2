package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.security.UserSecurityRepository;
import com.ucdc.backend.infrastructure.persistence.entity.UserSecurityEntity;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaUserSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserSecurityRepositoryAdapter implements UserSecurityRepository {

    private final JpaUserSecurityRepository jpa;

    @Override
    public int incrementFailedAttempts(UUID userId) {
        var e = jpa.findById(userId).orElseGet(() -> {
            var n = new UserSecurityEntity();
            n.setUserId(userId);
            n.setFailedAttempts(0);
            return n;
        });
        e.setFailedAttempts(e.getFailedAttempts() + 1);
        jpa.save(e);
        return e.getFailedAttempts();
    }

    @Override
    public void resetFailedAttempts(UUID userId) {
        jpa.findById(userId).ifPresent(e -> {
            e.setFailedAttempts(0);
            jpa.save(e);
        });
    }

    @Override
    public int getFailedAttempts(UUID userId) {
        return jpa.findById(userId).map(UserSecurityEntity::getFailedAttempts).orElse(0);
    }
}
