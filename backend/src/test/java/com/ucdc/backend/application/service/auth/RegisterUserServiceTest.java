package com.ucdc.backend.application.service.auth;

import com.ucdc.backend.application.dto.auth.RegisterUserCommand;
import com.ucdc.backend.application.services.auth.RegisterUserService;
import com.ucdc.backend.domain.exceptions.logic.ConflictException;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.PasswordEncoder;
import com.ucdc.backend.domain.security.PasswordPolicy;
import com.ucdc.backend.domain.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserServiceTest {

    @Mock
    UserRepository userRepo;
    @Mock
    PasswordCredentialRepository credentialRepo;
    @Mock
    PasswordEncoder encoder;
    @Mock
    PasswordPolicy policy;

    @InjectMocks
    RegisterUserService service;

    @Test
    void whenOk_registersUser_andStoresHash() {
        var cmd = new RegisterUserCommand("Alice Doe","alice@test.com","1001","SVC-1","3001234567","SecretPwd1!", false);
        lenient().when(userRepo.existsByEmail("alice@test.com")).thenReturn(false);
        lenient().when(userRepo.existsByCitizenId("1001")).thenReturn(false);

        when(encoder.encode("SecretPwd1!")).thenReturn("hashed");

        var userId = UUID.randomUUID();
        var persisted = ClientUser.newClient(
                userId,"Alice Doe", Email.of("alice@test.com"), CitizenId.of("1001"),
                ServiceNumber.of("SVC-1"), Phone.of("3001234567")
        );
        when(userRepo.save(any())).thenReturn(persisted);
        when(credentialRepo.save(any(PasswordCredential.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var out = service.register(cmd);

        assertEquals(userId, out.id());
        assertEquals("alice@test.com", out.email());
        verify(policy).validate("SecretPwd1!");
        verify(encoder).encode("SecretPwd1!");

        var captor = ArgumentCaptor.forClass(PasswordCredential.class);
        verify(credentialRepo).save(captor.capture());
        assertEquals(userId, captor.getValue().userId());
        assertEquals("hashed", captor.getValue().passwordHash());
    }

    @Test
    void whenEmailExists_throwConflict() {
        var cmd = new RegisterUserCommand("n","bob@test.com","2002","SVC-2",null,"AAaa11!!", false);
        when(userRepo.existsByEmail("bob@test.com")).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.register(cmd));
        verifyNoInteractions(encoder, credentialRepo);
    }

    @Test
    void whenCitizenExists_throwConflict() {
        var cmd = new RegisterUserCommand("n","bob@test.com","2002","SVC-2",null,"AAaa11!!", false);
        when(userRepo.existsByEmail("bob@test.com")).thenReturn(false);
        when(userRepo.existsByCitizenId("2002")).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.register(cmd));
        verifyNoInteractions(encoder, credentialRepo);
    }
}
