package com.ucdc.backend.application.service.auth;

import com.ucdc.backend.application.dto.auth.LogoutCommand;
import com.ucdc.backend.application.services.auth.LogoutService;
import com.ucdc.backend.domain.repositories.RefreshSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock RefreshSessionRepository sessions;
    @InjectMocks
    LogoutService service;

    @Test
    void whenBlankToken_doNothing() {
        service.logout(new LogoutCommand(" "));
        verifyNoInteractions(sessions);
    }

    @Test
    void whenTokenExists_deleteIt() {
        when(sessions.existsByRefreshToken("rt")).thenReturn(true);
        service.logout(new LogoutCommand("rt"));
        verify(sessions).deleteByRefreshToken("rt");
    }

    @Test
    void whenTokenNotExists_noDelete() {
        when(sessions.existsByRefreshToken("rt")).thenReturn(false);
        service.logout(new LogoutCommand("rt"));
        verify(sessions, never()).deleteByRefreshToken("rt");
    }
}
