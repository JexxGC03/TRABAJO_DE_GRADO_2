package com.ucdc.backend.application.services.auth;

import com.ucdc.backend.application.dto.auth.LogoutCommand;
import com.ucdc.backend.application.usecase.auth.LogoutUseCase;
import com.ucdc.backend.domain.repositories.RefreshSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService implements LogoutUseCase {

    private final RefreshSessionRepository sessionRepo;

    @Override
    public void logout(LogoutCommand cmd) {
        if (cmd.refreshToken() == null || cmd.refreshToken().isBlank()) return; // idempotente

        if (sessionRepo.existsByRefreshToken(cmd.refreshToken())) {
            sessionRepo.deleteByRefreshToken(cmd.refreshToken());
        }
    }
}
