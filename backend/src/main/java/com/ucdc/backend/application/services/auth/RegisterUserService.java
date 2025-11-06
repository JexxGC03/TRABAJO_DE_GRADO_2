package com.ucdc.backend.application.services.auth;

import com.ucdc.backend.application.dto.auth.RegisterUserCommand;
import com.ucdc.backend.application.dto.auth.RegisterUserResult;
import com.ucdc.backend.application.usecase.auth.RegisterUserUseCase;
import com.ucdc.backend.domain.exceptions.logic.ConflictException;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.PasswordEncoder;
import com.ucdc.backend.domain.security.PasswordPolicy;
import com.ucdc.backend.domain.value.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepo;
    private final PasswordCredentialRepository credentialRepo;
    private final PasswordEncoder encoder;
    private final PasswordPolicy policy;

    @Override
    public RegisterUserResult register(RegisterUserCommand cmd) {
        // 1) Unicidades
        if (userRepo.existsByEmail(cmd.email()))
            throw new ConflictException("Email already registered: ", cmd.email());
        if (userRepo.existsByCitizenId(cmd.citizenId()))
            throw new ConflictException("Citizen ID already registered: ", cmd.citizenId());

        // 2) Política + hash
        policy.validate(cmd.password());
        String hash = encoder.encode(cmd.password());

        // 3) VOs
        var email = Email.of(cmd.email());
        var citizen = CitizenId.of(cmd.citizenId());
        var service = ServiceNumber.of(cmd.serviceNumber());
        var phone = Phone.fromNullable(cmd.phone());

        // 4) Crear y persistir el usuario (asignar ID aquí)
        ClientUser user = ClientUser.newClient(
                null,
                cmd.name(),
                email,
                citizen,
                service,
                phone
        );

        var saved = userRepo.save(user);
        if (saved == null) {
            throw new IllegalStateException("UserRepository.save returned null");
        }

        // 5) Guardar credencial
        credentialRepo.save(PasswordCredential.forUser(saved.id(), hash));

        // 6) Respuesta
        return new RegisterUserResult(saved.id(),saved.fullName(), saved.email().toString(), saved.role().toString(), saved.status().toString());
    }
}
