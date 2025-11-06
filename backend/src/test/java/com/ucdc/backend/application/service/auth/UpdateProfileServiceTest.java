package com.ucdc.backend.application.service.auth;

import com.ucdc.backend.application.dto.user.UpdateProfileCommand;
import com.ucdc.backend.application.services.auth.UpdateProfileService;
import com.ucdc.backend.domain.exceptions.logic.ConflictException;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.PasswordEncoder;
import com.ucdc.backend.domain.security.PasswordPolicy;
import com.ucdc.backend.domain.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock UserRepository userRepo;
    @Mock PasswordCredentialRepository credentialRepo;
    @Mock PasswordEncoder encoder;
    @Mock PasswordPolicy policy;

    @InjectMocks
    UpdateProfileService service;

    @Test
    void whenOk_updateNameEmailPhone_andOptionalPassword() {
        var uid = UUID.randomUUID();
        var user = ClientUser.newClient(
                uid, "Old Name", Email.of("old@test.com"), CitizenId.of("1001"),
                ServiceNumber.of("SVC-1"), Phone.of("3001112233")
        );
        when(userRepo.findById(uid)).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail("new@test.com")).thenReturn(false);

        var cmd = new UpdateProfileCommand(uid, "New Name", "new@test.com", "3009998888", "NewP@ssw0rd");
        when(credentialRepo.findByUserId(uid)).thenReturn(Optional.of("oldHash"));
        when(encoder.encode("NewP@ssw0rd")).thenReturn("newHash");
        when(credentialRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var out = service.update(cmd);

        assertEquals("New Name", out.name());
        assertEquals("new@test.com", out.email());
        assertEquals("3009998888", out.phone());

        verify(userRepo).save(user);
        verify(policy).validate("NewP@ssw0rd");
        verify(encoder).encode("NewP@ssw0rd");
        verify(credentialRepo).save(any(PasswordCredential.class));

        assertEquals("New Name", user.fullName());
        assertEquals("new@test.com", user.email().value());
        assertEquals("3009998888", user.phone().value());
    }

    @Test
    void whenEmailTaken_throwConflict() {
        var uid = UUID.randomUUID();
        var user = ClientUser.newClient(
                uid, "Old Name", Email.of("old@test.com"), CitizenId.of("1001"),
                ServiceNumber.of("SVC-1"), Phone.of("3001112233")
        );
        when(userRepo.findById(uid)).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail("new@test.com")).thenReturn(true);

        var cmd = new UpdateProfileCommand(uid, "New Name", "new@test.com", null, null);
        assertThrows(ConflictException.class, () -> service.update(cmd));

        verify(userRepo, never()).save(any());
        verifyNoInteractions(credentialRepo, encoder, policy);
    }

    @Test
    void whenUserMissing_throw404() {
        var uid = UUID.randomUUID();
        when(userRepo.findById(uid)).thenReturn(Optional.empty());
        var cmd = new UpdateProfileCommand(uid, "n", "e@test.com", null, null);
        assertThrows(NotFoundException.class, () -> service.update(cmd));
    }

    @Test
    void whenChangingPassword_butCredentialMissing_throw404() {
        var uid = UUID.randomUUID();
        var user = ClientUser.newClient(
                uid, "Old Name", Email.of("old@test.com"), CitizenId.of("1001"),
                ServiceNumber.of("SVC-1"), Phone.of("3001112233")
        );
        when(userRepo.findById(uid)).thenReturn(Optional.of(user));
        when(credentialRepo.findByUserId(uid)).thenReturn(Optional.empty());
        var cmd = new UpdateProfileCommand(uid, null, "old@test.com", null, "AAaa11!!");

        assertThrows(NotFoundException.class, () -> service.update(cmd));
        // El servicio valida y codifica la contraseña antes de buscar la credencial
        verify(policy).validate("AAaa11!!");
        verify(encoder).encode("AAaa11!!");
        // No se guardó ninguna credencial porque la búsqueda falló y lanzó NotFound
        verify(credentialRepo, never()).save(any());
    }
}
