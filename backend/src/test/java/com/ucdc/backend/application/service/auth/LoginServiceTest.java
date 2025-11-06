package com.ucdc.backend.application.service.auth;

import com.ucdc.backend.application.dto.auth.LoginCommand;
import com.ucdc.backend.application.dto.auth.LoginResult;
import com.ucdc.backend.application.mapper.AuthAppMapper;
import com.ucdc.backend.application.services.auth.LoginService;
import com.ucdc.backend.domain.enums.UserStatus;
import com.ucdc.backend.domain.exceptions.logic.ForbiddenException;
import com.ucdc.backend.domain.exceptions.logic.UnauthorizedException;
import com.ucdc.backend.domain.model.AdminUser;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.domain.model.User;
import com.ucdc.backend.domain.repositories.PasswordCredentialRepository;
import com.ucdc.backend.domain.repositories.RefreshSessionRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.JwtProviderPort;
import com.ucdc.backend.domain.security.PasswordEncoder;
import com.ucdc.backend.domain.security.PasswordPolicy;
import com.ucdc.backend.domain.security.UserSecurityRepository;
import com.ucdc.backend.domain.value.CitizenId;
import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.domain.value.Phone;
import com.ucdc.backend.domain.value.ServiceNumber;
import com.ucdc.backend.domain.value.RefreshSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private UserRepository users;
    @Mock private PasswordCredentialRepository credentials;
    @Mock private RefreshSessionRepository sessions;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtProviderPort jwt;
    @Mock private PasswordPolicy passwordPolicy;
    @Mock private UserSecurityRepository securityRepo;
    @Mock private AuthAppMapper mapper;

    @InjectMocks
    private LoginService service;

    private UUID userId;
    private Email email;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = new Email("john.doe@mail.com");
        user = makeClientUser(userId, email, UserStatus.ACTIVE);

        // Este stub lo usan la mayoría de tests (siempre buscamos por email válido)
        // marcar como lenient para que los tests que prueben emails distintos no fallen con UnnecessaryStubbing
        lenient().when(users.findByEmail(new Email("john.doe@mail.com"))).thenReturn(Optional.of(user));
    }

    // ============ CAMINO FELIZ ============

    @Test
    void login_ok_reseteaIntentos_guardaSesion_y_mapeaResultado() {
        var cmd = new LoginCommand("john.doe@mail.com", "S3cure!");

        when(credentials.findByUserId(userId)).thenReturn(Optional.of("$2a$hash"));
        when(encoder.matches("S3cure!", "$2a$hash")).thenReturn(true);

        when(sessions.defaultRefreshTtlSeconds()).thenReturn(3600);
        when(sessions.defaultAccessTtlSeconds()).thenReturn(900);

        // ⚠️ Firma con long (0L) o usa anyLong()
        when(jwt.generateAccessToken(same(user), eq(0L))).thenReturn("acc.jwt");
        when(jwt.generateRefreshToken(same(user), eq(0L))).thenReturn("ref.jwt");

        var summary = new LoginResult.UserSummary(
                user.id(), user.fullName(), user.email().value(),
                user.role().name(), user.status().name()
        );
        var expected = new LoginResult("Bearer", "acc.jwt", 900, "ref.jwt", OffsetDateTime.now(), summary);
        when(mapper.toLoginResult(eq("Bearer"), eq("acc.jwt"), eq(900), eq("ref.jwt"),
                any(OffsetDateTime.class), same(user))).thenReturn(expected);

        var out = service.login(cmd);

        verify(securityRepo).resetFailedAttempts(userId);
        // El servicio guarda un RefreshSession; capturarlo y verificar token + userId
        ArgumentCaptor<RefreshSession> captor = ArgumentCaptor.forClass(RefreshSession.class);
        verify(sessions).save(captor.capture());
        RefreshSession saved = captor.getValue();
        assertEquals(userId, saved.userId());
        assertEquals("ref.jwt", saved.refreshToken());

        assertSame(expected, out);
    }

    // ============ ERRORES ============

    @Test
    void cuandoEmailNoExiste_lanzaUnauthorized() {
        var cmd = new LoginCommand("no@exists.com", "x");
        when(users.findByEmail(new Email("no@exists.com"))).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> service.login(cmd));
        verifyNoInteractions(credentials, encoder, jwt, mapper, sessions, securityRepo);
    }

    @Test
    void cuandoUsuarioBloqueado_lanzaForbidden() {
        var blocked = makeClientUser(userId, email, UserStatus.BLOCKED);
        when(users.findByEmail(new Email("john.doe@mail.com"))).thenReturn(Optional.of(blocked));

        var cmd = new LoginCommand("john.doe@mail.com", "x");

        assertThrows(ForbiddenException.class, () -> service.login(cmd));
        verifyNoInteractions(credentials, encoder, jwt, mapper, sessions, securityRepo);
    }

    @Test
    void cuandoPasswordVacia_lanzaUnauthorized() {
        var cmd = new LoginCommand("john.doe@mail.com", "   ");

        when(credentials.findByUserId(userId)).thenReturn(Optional.of("$2a$hash"));
        assertThrows(UnauthorizedException.class, () -> service.login(cmd));
        verifyNoInteractions(encoder, jwt, mapper, sessions);
        verify(credentials).findByUserId(userId);
    }

    @Test
    void cuandoHashNoExiste_lanzaUnauthorized() {
        var cmd = new LoginCommand("john.doe@mail.com", "x");
        when(credentials.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> service.login(cmd));

        verifyNoInteractions(encoder, jwt, mapper, sessions);
    }

    @Test
    void passwordIncorrecta_incrementaIntentos_sinBloquear() {
        var cmd = new LoginCommand("john.doe@mail.com", "bad");
        when(credentials.findByUserId(userId)).thenReturn(Optional.of("$2a$hash"));
        when(encoder.matches("bad", "$2a$hash")).thenReturn(false);
        when(securityRepo.incrementFailedAttempts(userId)).thenReturn(2); // < MAX (3)

        assertThrows(UnauthorizedException.class, () -> service.login(cmd));

        verify(securityRepo).incrementFailedAttempts(userId);
        verify(users, never()).save(any());
        verify(securityRepo, never()).resetFailedAttempts(userId);
        verifyNoInteractions(jwt, mapper, sessions);
    }

    @Test
    void passwordIncorrecta_alcanzaMaximo_bloqueaUsuario_yReseteaIntentos() {
        var cmd = new LoginCommand("john.doe@mail.com", "bad");
        when(credentials.findByUserId(userId)).thenReturn(Optional.of("$2a$hash"));
        when(encoder.matches("bad", "$2a$hash")).thenReturn(false);
        when(securityRepo.incrementFailedAttempts(userId)).thenReturn(3); // == MAX (3)

        assertThrows(UnauthorizedException.class, () -> service.login(cmd));

        // Con withStatus(...) mutando el mismo objeto, es el mismo 'user'
        verify(users).save(same(user));
        verify(securityRepo).resetFailedAttempts(userId);
        verifyNoInteractions(jwt, mapper, sessions);
        assertEquals(UserStatus.BLOCKED, user.status());
    }

    // ============ Helpers ============

    private User makeClientUser(UUID id, Email mail, UserStatus status) {
        // Ajusta este constructor si tu modelo difiere
        return new ClientUser(
                id,
                "John Doe",
                mail,
                new CitizenId("CC-123"),
                new ServiceNumber("S-0001"),
                new Phone("+57-3000000000"),
                status,
                OffsetDateTime.now().minusDays(2),
                OffsetDateTime.now().minusDays(1)
        );
    }

    @SuppressWarnings("unused")
    private User makeAdminUser(UUID id, Email mail, UserStatus status) {
        return new AdminUser(
                id,
                "Alice Admin",
                mail,
                new CitizenId("CC-999"),
                new ServiceNumber("ADM-001"),
                new Phone("+57-3010000000"),
                status,
                OffsetDateTime.now().minusDays(2),
                OffsetDateTime.now().minusDays(1)
        );
    }
}
