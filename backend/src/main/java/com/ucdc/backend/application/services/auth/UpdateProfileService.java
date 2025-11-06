package com.ucdc.backend.application.services.auth;

import com.ucdc.backend.application.dto.user.UpdateProfileCommand;
import com.ucdc.backend.application.dto.user.UpdateProfileResult;
import com.ucdc.backend.application.usecase.user.UpdateProfileUseCase;
import com.ucdc.backend.domain.exceptions.logic.ConflictException;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.PasswordEncoder;
import com.ucdc.backend.domain.security.PasswordPolicy;
import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.domain.value.PasswordCredential;
import com.ucdc.backend.domain.value.Phone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserRepository userRepo;
    private final PasswordCredentialRepository credentialRepo;
    private final PasswordEncoder encoder;
    private final PasswordPolicy policy;

    @Override
    public UpdateProfileResult update(UpdateProfileCommand cmd) {
        var user = userRepo.findById(cmd.userId())
                .orElseThrow(() -> new NotFoundException("User", cmd.userId()));

        // Verificar unicidad del correo (si cambió)
        if (!user.email().value().equals(cmd.email()) &&
                userRepo.existsByEmail(cmd.email())) {
            throw new ConflictException("Email already registered: ", cmd.email());
        }

        // Actualizar campos básicos
        user.update(cmd.name(), Email.of(cmd.email()), Phone.fromNullable(cmd.phone()));
        userRepo.save(user);

        // Si cambia la contraseña, validar y actualizar hash
        if (cmd.newPassword() != null && !cmd.newPassword().isBlank()) {
            policy.validate(cmd.newPassword());
            var hash = encoder.encode(cmd.newPassword());

            var currentCred = credentialRepo.findByUserId(cmd.userId())
                    .orElseThrow(() -> new NotFoundException("Password credential not found"));

            credentialRepo.save(PasswordCredential.forUser(cmd.userId(), hash));
        }

        return new UpdateProfileResult(user.fullName(), user.email().toString(), user.phone().toString());
    }
}
