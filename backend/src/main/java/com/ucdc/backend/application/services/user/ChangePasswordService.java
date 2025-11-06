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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

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
        // 1) Validaciones superficiales
        Objects.requireNonNull(cmd, "command");
        if (cmd.userId() == null) throw new IllegalArgumentException("userId requerido");
        if (cmd.currentPassword() == null || cmd.currentPassword().isBlank())
            throw new IllegalArgumentException("Contraseña actual requerida");
        if (cmd.newPassword() == null || cmd.newPassword().isBlank())
            throw new IllegalArgumentException("Nueva contraseña requerida");
        if (!cmd.newPassword().equals(cmd.confirmNewPassword()))
            throw new IllegalArgumentException("La nueva contraseña y su confirmación no coinciden");

        // 2) Asegurar que el usuario exista (coherente con tu UpdateProfileService)
        userRepo.findById(cmd.userId())
                .orElseThrow(() -> new NotFoundException("User", cmd.userId()));

        // 3) Buscar credencial actual
        var currentCred = credentialRepo.findByUserId(cmd.userId())
                .orElseThrow(() -> new NotFoundException("Password credential not found"));

        // 4) Verificar contraseña actual
        if (!encoder.matches(cmd.currentPassword(), currentCred.passwordHash())) {
            throw new WrongPasswordException("");
        }

        // 5) Política de contraseñas (fuerza mínima, etc.)
        policy.validate(cmd.newPassword());

        // 6) Evitar que la nueva sea igual a la actual
        if (encoder.matches(cmd.newPassword(), currentCred.passwordHash())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la actual");
        }

        // 7) Generar nuevo hash y persistir
        var newHash = encoder.encode(cmd.newPassword());
        var saved = credentialRepo.save(PasswordCredential.forUser(cmd.userId(), newHash));

        // 8) Invalidar sesiones de refresh (opcional pero recomendado)
        if (refreshRepo != null) {
            refreshRepo.deleteAllByUserId(cmd.userId());
        }

        // 9) Responder
        var updatedAt = saved.updatedAt() != null ? saved.updatedAt() : OffsetDateTime.now();
        return new ChangePasswordResult(updatedAt);
    }
}
