package com.ucdc.backend.application.service.meter;

import com.ucdc.backend.application.dto.meter.RegisterMeterCommand;
import com.ucdc.backend.application.dto.meter.RegisterMeterResult;
import com.ucdc.backend.application.mapper.MeterAppMapper;
import com.ucdc.backend.application.services.meter.RegisterMeterService;
import com.ucdc.backend.domain.enums.Provider;
import com.ucdc.backend.domain.model.ClientUser;
import com.ucdc.backend.domain.model.Meter;
import com.ucdc.backend.domain.repositories.MeterRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.value.CitizenId;
import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.domain.value.Phone;
import com.ucdc.backend.domain.value.ServiceNumber;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RegisterMeterServiceTest {
    private final MeterRepository meterRepo = mock(MeterRepository.class);
    private final UserRepository userRepo = mock(UserRepository.class);

    // Implementación simple del mapper (usa tus factories del dominio)
    private final MeterAppMapper mapper = new MeterAppMapper() {
        @Override
        public Meter toDomain(RegisterMeterCommand cmd) {
            return Meter.register(cmd.userId(), cmd.serialNumber(), cmd.provider(), cmd.installationAddress(), cmd.serviceNumber(), cmd.alias());
        }
        @Override
        public RegisterMeterResult toResult(Meter meter) {
            return new RegisterMeterResult(meter.id(), meter.serialNumber(), meter.provider(), meter.serviceNumber(), meter.installationAddress(), meter.alias(), meter.installedAt());
        }
    };

    @Test
    void handle_ok_creaMeter_yDevuelveActivo() {

        var existingUser = ClientUser.create(
                UUID.randomUUID(),
                "John Doe",
                new Email("john@test.com"),
                new CitizenId("123456789"),
                new ServiceNumber("SRV-001"),
                new Phone("+57 3001234567")
        );

        var cmd = new RegisterMeterCommand(
                existingUser.id(),
                "SN-001",
                Provider.CODENSA,
                "123345",
                "Calle siempreviva",
                "Casa Campo"

        );

        when(userRepo.findById(existingUser.id())).thenReturn(Optional.of(existingUser));
        when(meterRepo.findBySerialNumber("SN-001")).thenReturn(Optional.empty());
        when(meterRepo.save(any(Meter.class))).thenAnswer(inv -> inv.getArgument(0));

        var service = new RegisterMeterService(userRepo, meterRepo, mapper);

        var result = service.handle(cmd);

        assertNotNull(result);
        verify(meterRepo, times(1)).save(any(Meter.class));
    }

    @Test
    void handle_userNoExiste_lanzaNotFound() {
        var cmd = new RegisterMeterCommand(UUID.randomUUID(), "SN-001", Provider.ENEL, "100164", "Calle 123", "Casa Bogota");

        when(userRepo.findById(cmd.userId())).thenReturn(Optional.empty());

        var service = new RegisterMeterService(userRepo, meterRepo, mapper);

        assertThrows(RuntimeException.class, () -> service.handle(cmd));
        verify(meterRepo, never()).save(any());
    }

    @Test
    void handle_serialDuplicado_lanzaConflict() {

        // Usuario del dominio (sealed class -> Client)
        var existingUser = ClientUser.create(
                UUID.randomUUID(),
                "John Doe",
                new Email("john@test.com"),
                new CitizenId("123456789"),
                new ServiceNumber("SRV-001"),
                new Phone("+57 3001234567")
        );

        var cmd = new RegisterMeterCommand(
                existingUser.id(),
                "SN-001",
                Provider.CODENSA,
                "123345",
                "Calle siempreviva",
                "Casa Campo"
        );

        when(userRepo.findById(existingUser.id())).thenReturn(Optional.of(existingUser));
        when(meterRepo.existsBySerialNumber("SN-001")).thenReturn(true);

        var service = new RegisterMeterService(userRepo, meterRepo, mapper);

        assertThrows(RuntimeException.class, () -> service.handle(cmd));
        verify(meterRepo, never()).save(any());
    }
}
