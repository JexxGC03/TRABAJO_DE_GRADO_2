package com.ucdc.backend.application.services.user;

import com.ucdc.backend.application.dto.user.ChangePasswordCommand;
import com.ucdc.backend.application.dto.user.ChangePasswordResult;
import com.ucdc.backend.application.usecase.user.ChangePasswordUseCase;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.exceptions.logic.WrongPasswordException;
import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.repositories.RefreshSessionRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.PasswordEncoder;
import com.ucdc.backend.domain.security.PasswordPolicy;
import com.ucdc.backend.domain.value.PasswordCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepo;
    private final PasswordCredentialRepository credentialRepo;
    private final RefreshSessionRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final PasswordPolicy policy;

    @Override
    public ChangePasswordResult handle(ChangePasswordCommand cmd) {
        // 0) Validaciones básicas
        Objects.requireNonNull(cmd, "command");
        UUID userId = Objects.requireNonNull(cmd.userId(), "userId requerido");
        if (cmd.currentPassword() == null || cmd.currentPassword().isBlank())
            throw new IllegalArgumentException("Contraseña actual requerida");
        if (cmd.newPassword() == null || cmd.newPassword().isBlank())
            throw new IllegalArgumentException("Nueva contraseña requerida");

        // 1) Usuario debe existir
        userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));

        // 2) Credencial actual
        var current = credentialRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Password credential not found"));

        // 3) Verificación de contraseña actual
        if (!encoder.matches(cmd.currentPassword(), current.passwordHash())) {
            throw new WrongPasswordException("La contraseña actual es incorrecta");
        }

        // 4) Política y evitar reutilización
        policy.validate(cmd.newPassword());
        if (encoder.matches(cmd.newPassword(), current.passwordHash())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la actual");
        }

        // 5) Generar nuevo hash y persistir (dominio inmutable → withPasswordHash)
        var newHash = encoder.encode(cmd.newPassword());
        var updated = current.withPasswordHash(newHash);
        var saved = credentialRepo.save(updated); // el adapter hace update in-place

        // 6) (Opcional) invalidar sesiones de refresh; no bloquear el flujo si falla
        try {
            refreshRepo.deleteAllByUserId(userId);
        } catch (Exception ignored) { }

        // 7) Responder con la marca de tiempo efectiva
        var when = (saved.updatedAt() != null) ? saved.updatedAt() : OffsetDateTime.now();
        return new ChangePasswordResult(when);
    }
}
