package com.ucdc.backend.application.services.auth;

import com.ucdc.backend.application.dto.auth.LoginCommand;
import com.ucdc.backend.application.dto.auth.LoginResult;
import com.ucdc.backend.application.mapper.AuthAppMapper;
import com.ucdc.backend.application.usecase.auth.LoginUseCase;
import com.ucdc.backend.domain.enums.UserStatus;
import com.ucdc.backend.domain.exceptions.logic.ForbiddenException;
import com.ucdc.backend.domain.exceptions.logic.UnauthorizedException;
import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.repositories.RefreshSessionRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.JwtProviderPort;
import com.ucdc.backend.domain.security.PasswordEncoder;
//import com.ucdc.backend.domain.security.PasswordPolicy;
import com.ucdc.backend.domain.security.UserSecurityRepository;
import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.domain.value.RefreshSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements LoginUseCase {

    private static final int MAX_ATTEMPTS = 3;

    private final UserRepository users;
    private final PasswordCredentialRepository credentials;
    private final RefreshSessionRepository sessions;
    private final PasswordEncoder encoder;
    private final JwtProviderPort jwt;
    //private final PasswordPolicy passwordPolicy;
    private final UserSecurityRepository securityRepo;
    private final AuthAppMapper mapper;

    @Override
    public LoginResult login(LoginCommand cmd) {
        var email = new Email(cmd.email());
        var user = users.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (user.status() == UserStatus.BLOCKED) {
            throw new ForbiddenException("User is blocked after multiple failed attempts");
        }

        // 1) Traer credencial (no el hash directamente)
        var credential = credentials.findByUserId(user.id())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        // 2) Validación de entrada
        if (cmd.password() == null || cmd.password().isBlank()) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // 3) Comparar usando el string del hash
        String storedHash = credential.passwordHash(); // <--- importante
        if (!encoder.matches(cmd.password(), storedHash)) {
            int attempts = securityRepo.incrementFailedAttempts(user.id());
            if (attempts >= MAX_ATTEMPTS) {
                var blocked = user.withStatus(UserStatus.BLOCKED);
                users.save(blocked);
                securityRepo.resetFailedAttempts(user.id());
            }
            throw new UnauthorizedException("Invalid credentials");
        }

        // 4) Éxito → reset intentos
        securityRepo.resetFailedAttempts(user.id());

        // 5) Emitir tokens y persistir refresh session
        var access  = jwt.generateAccessToken(user, 0);
        var refresh = jwt.generateRefreshToken(user, 0);
        var refreshExp = OffsetDateTime.now().plusSeconds(sessions.defaultRefreshTtlSeconds());
        sessions.save(RefreshSession.forUser(user.id(), refresh, sessions.defaultRefreshTtlSeconds()));

        return mapper.toLoginResult(
                "Bearer",
                access,
                sessions.defaultAccessTtlSeconds(),
                refresh,
                refreshExp,
                user
        );
    }
}
