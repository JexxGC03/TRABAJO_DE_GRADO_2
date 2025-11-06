package com.ucdc.backend.application.service.alert;

import com.ucdc.backend.application.services.alert.ResolveAlertService;
import com.ucdc.backend.application.usecase.alert.ResolveAlertUseCase;
import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.domain.repositories.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveAlertServiceTest {

    @Mock
    AlertRepository alertRepo;

    @Test
    void whenAlertNotFound_thenThrow404() {
        ResolveAlertUseCase service = new ResolveAlertService(alertRepo);
        UUID id = UUID.randomUUID();
        when(alertRepo.findById(id)).thenReturn(Optional.empty());

        var ex = assertThrows(NotFoundException.class, () -> service.handle(id));
        assertTrue(ex.getMessage().contains("Alert not found"));
        verify(alertRepo, never()).save(any());
    }

    @Test
    void whenResolve_ok_thenSave() {
        ResolveAlertUseCase service = new ResolveAlertService(alertRepo);
        var a = Alert.rehydrate(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.TEN, BigDecimal.TEN, AlertStatus.ACTIVE,
                OffsetDateTime.now().minusHours(1), OffsetDateTime.now().minusHours(1));

        when(alertRepo.findById(a.id())).thenReturn(Optional.of(a));
        when(alertRepo.save(a)).thenReturn(a);

        service.handle(a.id());

        assertEquals(AlertStatus.RESOLVED, a.status());
        verify(alertRepo).save(a);
    }
}
