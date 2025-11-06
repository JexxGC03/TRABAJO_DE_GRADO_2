package com.ucdc.backend.application.service.auth;

import com.ucdc.backend.application.dto.auth.LoginResult;
import com.ucdc.backend.application.dto.auth.RefreshTokenCommand;
import com.ucdc.backend.application.services.auth.RefreshTokenService;
import com.ucdc.backend.domain.exceptions.logic.UnauthorizedException;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.domain.repositories.RefreshSessionRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.security.JwtProviderPort;
import com.ucdc.backend.domain.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    RefreshSessionRepository sessions;
    @Mock
    UserRepository userRepo;
    @Mock
    JwtProviderPort jwt;

    @InjectMocks
    RefreshTokenService service;

    @Test
    void whenValidRefresh_replacesSession_andReturnsTokensAndUser() {
        var userId = UUID.randomUUID();
        var oldToken = "old_rt";
        when(sessions.findByRefreshToken(oldToken))
                .thenReturn(Optional.of(new RefreshSession(userId, oldToken, null)));

        var user = ClientUser.newClient(
                userId,"User Name", Email.of("u@test.com"), CitizenId.of("123"),
                ServiceNumber.of("SVC-9"), Phone.of("3001112233")
        );
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(jwt.generateAccessToken(user, 0L)).thenReturn("access");
        when(jwt.generateRefreshToken(user, 0L)).thenReturn("new_rt");

        LoginResult out = service.refresh(new RefreshTokenCommand(oldToken));

        assertEquals("Bearer", out.tokenType());
        assertEquals("access", out.accessToken());
        assertEquals("new_rt", out.refreshToken());
        assertNotNull(out.refreshExp());
        assertEquals(userId, out.user().id());
        verify(sessions).deleteByRefreshToken(oldToken);

        // Capturar la sesión guardada y verificar que se pasó el Refresh token correcto
        ArgumentCaptor<RefreshSession> captor = ArgumentCaptor.forClass(RefreshSession.class);
        verify(sessions).save(captor.capture());
        RefreshSession saved = captor.getValue();
        assertEquals(out.refreshToken(), saved.refreshToken());
        assertEquals(userId, saved.userId());
    }

    @Test
    void whenInvalidRefresh_throw401() {
        when(sessions.findByRefreshToken("bad")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> service.refresh(new RefreshTokenCommand("bad")));
        verifyNoInteractions(userRepo, jwt);
    }

    @Test
    void whenRefreshUserMissing_throw401() {
        var uid = UUID.randomUUID();
        when(sessions.findByRefreshToken("rt"))
                .thenReturn(Optional.of(new RefreshSession(uid, "rt", null)));
        when(userRepo.findById(uid)).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> service.refresh(new RefreshTokenCommand("rt")));
        verifyNoInteractions(jwt);
    }
}
