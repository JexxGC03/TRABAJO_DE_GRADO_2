package com.ucdc.backend.application.services.auth;

import com.ucdc.backend.application.dto.auth.LoginResult;
import com.ucdc.backend.application.dto.auth.RefreshTokenCommand;
import com.ucdc.backend.application.usecase.auth.RefreshTokenUseCase;
import com.ucdc.backend.domain.exceptions.logic.UnauthorizedException;
import com.ucdc.backend.domain.repositories.RefreshSessionRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.JwtProviderPort;
import com.ucdc.backend.domain.value.RefreshSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService implements RefreshTokenUseCase{

    private final RefreshSessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final JwtProviderPort jwt;

    @Override
    public LoginResult refresh(RefreshTokenCommand cmd) {
        var session = sessionRepo.findByRefreshToken(cmd.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        var user = userRepo.findById(session.userId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        var accessToken = jwt.generateAccessToken(user, 0L);
        var newRefreshToken = jwt.generateRefreshToken(user, 0L);
        long ttl = sessionRepo.defaultRefreshTtlSeconds();

        // reemplaza la sesión anterior
        sessionRepo.deleteByRefreshToken(cmd.refreshToken());
        sessionRepo.save(RefreshSession.forUser(user.id(), newRefreshToken, ttl));

        return new LoginResult(
                "Bearer",
                accessToken,
                3600,
                newRefreshToken,
                OffsetDateTime.now().plusDays(30),
                new LoginResult.UserSummary(
                        user.id(),
                        user.fullName(),
                        user.email().value(),
                        user.role().name(),
                        user.status().name()
                )
        );
    }
}
