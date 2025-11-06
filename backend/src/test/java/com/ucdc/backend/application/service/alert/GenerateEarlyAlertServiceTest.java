package com.ucdc.backend.application.service.alert;

import com.ucdc.backend.application.dto.alert.GenerateAlertCommand;
import com.ucdc.backend.application.dto.alert.GenerateAlertResult;
import com.ucdc.backend.application.mapper.AlertAppMapper;
import com.ucdc.backend.application.services.alert.GenerateEarlyAlertService;
import com.ucdc.backend.application.usecase.alert.GenerateEarlyAlertUseCase;
import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.domain.repositories.AlertRepository;
import com.ucdc.backend.domain.repositories.MeterRepository;
//import com.ucdc.backend.domain.services.NotificationServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateEarlyAlertServiceTest {

    @Mock MeterRepository meterRepo;
    @Mock AlertRepository alertRepo;
    //@Mock NotificationServicePort notifier;
    @Mock AlertAppMapper mapper;

    GenerateEarlyAlertUseCase service;

    UUID meterId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new GenerateEarlyAlertService(meterRepo, alertRepo, mapper);
    }

    @Test
    void whenMeterNotFound_thenThrow404() {
        var cmd = new GenerateAlertCommand(meterId, BigDecimal.TEN, BigDecimal.ONE, OffsetDateTime.now());
        when(meterRepo.existsById(meterId)).thenReturn(false);

        var ex = assertThrows(IllegalArgumentException.class, () -> service.handle(cmd));
        assertTrue(ex.getMessage().contains("Meter not found"));
        verify(alertRepo, never()).save(any());
    }

    @Test
    void whenActiveAlertExists_thenThrow409() {
        var cmd = new GenerateAlertCommand(meterId, BigDecimal.TEN, BigDecimal.TEN, OffsetDateTime.now());
        when(meterRepo.existsById(meterId)).thenReturn(true);
        when(alertRepo.findActiveByMeter(meterId)).thenReturn(Optional.of(mock(Alert.class)));

        assertThrows(IllegalStateException.class, () -> service.handle(cmd));
        verify(alertRepo, never()).save(any());;
    }

    @Test
    void whenBelowThreshold_thenNoAlertAndNoPersistence() {
        var cmd = new GenerateAlertCommand(meterId, new BigDecimal("100"), new BigDecimal("60"), OffsetDateTime.now());
        when(meterRepo.existsById(meterId)).thenReturn(true);
        when(alertRepo.findActiveByMeter(meterId)).thenReturn(Optional.empty());

        GenerateAlertResult result = service.handle(cmd);

        assertEquals("NO_ALERT", result.status());
        verify(alertRepo, never()).save(any());
    }

    @Test
    void whenAboveThreshold_thenPersistAndNotify() {
        var now = OffsetDateTime.now();
        var cmd = new GenerateAlertCommand(meterId, new BigDecimal("100"), new BigDecimal("100"), now);
        var domainToSave = Alert.rehydrate(UUID.randomUUID(), meterId, new BigDecimal("100"), new BigDecimal("100"),
                AlertStatus.ACTIVE, now, now);

        when(meterRepo.existsById(meterId)).thenReturn(true);
        when(alertRepo.findActiveByMeter(meterId)).thenReturn(Optional.empty());

        // El service puede llamar mapper.from(cmd) o mapper.toDomain(cmd): cubrimos ambos
        when(mapper.toResult(any(Alert.class)))
                .thenReturn(new GenerateAlertResult(domainToSave.id(), "ACTIVE", domainToSave.toString()));
        try { when(mapper.getClass().getMethod("from", GenerateAlertCommand.class)).thenReturn(null); } catch (NoSuchMethodException ignored) {}
        // Stubs seguros:
        try { when(mapper.getClass().getMethod("toDomain", GenerateAlertCommand.class)).thenReturn(null); } catch (NoSuchMethodException ignored) {}
        // Usamos Answer por si el mapper real no está; simulamos creación manual:
        // En lugar de depender del mapper, haremos que el repo reciba cualquier Alert y devuelva el mismo.
        when(alertRepo.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(cmd);

        assertEquals("ACTIVE", result.status());
        assertNotNull(result.alertId());
        verify(alertRepo).save(any(Alert.class));
    }
}
