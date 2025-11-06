package com.ucdc.backend.application.service.meter;

import com.ucdc.backend.application.dto.meter.UpdateMeterReadingCommand;
import com.ucdc.backend.application.dto.meter.UpdateMeterReadingResult;
import com.ucdc.backend.application.services.meter.UpdateMeterReadingService;
import com.ucdc.backend.domain.repositories.MeterReadingRepository;
import com.ucdc.backend.domain.repositories.MeterRepository;
import com.ucdc.backend.domain.value.Reading;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UpdateMeterReadingServiceTest {

    private final MeterRepository meterRepo = mock(MeterRepository.class);
    private final MeterReadingRepository readingRepo = mock(MeterReadingRepository.class);
    private final ConsumptionAggregatorPort aggregator = mock(ConsumptionAggregatorPort.class);

    @Test
    void handle_meterNoExiste_lanzaNotFound() {
        var cmd = new UpdateMeterReadingCommand(UUID.randomUUID(), OffsetDateTime.now(), new BigDecimal("100"));

        when(meterRepo.existsById(cmd.meterId())).thenReturn(false);

        var service = new UpdateMeterReadingService(meterRepo, readingRepo, aggregator);

        assertThrows(RuntimeException.class, () -> service.handle(cmd));
        verify(readingRepo, never()).save(any());
        verify(aggregator, never()).bucketize(any(), any());
    }

    @Test
    void handle_timestampDuplicado_lanzaConflict() {
        var cmd = new UpdateMeterReadingCommand(UUID.randomUUID(), OffsetDateTime.now(), new BigDecimal("100"));

        when(meterRepo.existsById(cmd.meterId())).thenReturn(true);
        when(readingRepo.existsByMeterAndTimestamp(cmd.meterId(), cmd.timestamp())).thenReturn(true);

        var service = new UpdateMeterReadingService(meterRepo, readingRepo, aggregator);

        assertThrows(RuntimeException.class, () -> service.handle(cmd));
        verify(readingRepo, never()).save(any());
        verify(aggregator, never()).bucketize(any(), any());
    }

    @Test
    void handle_kwhDecreciente_devuelveINVALID_yNoPersiste() {
        var meterId = UUID.randomUUID();
        var cmd = new UpdateMeterReadingCommand(meterId, OffsetDateTime.now(), new BigDecimal("90"));

        when(meterRepo.existsById(meterId)).thenReturn(true);
        when(readingRepo.existsByMeterAndTimestamp(meterId, cmd.timestamp())).thenReturn(false);
        when(readingRepo.findLastByMeter(meterId))
                .thenReturn(Optional.of(new Reading(meterId, cmd.timestamp().minusMinutes(1), new BigDecimal("100"))));

        var service = new UpdateMeterReadingService(meterRepo, readingRepo, aggregator);

        UpdateMeterReadingResult res = service.handle(cmd);

        assertEquals("INVALID", res.status());
        verify(readingRepo, never()).save(any());
        verify(aggregator, never()).bucketize(any(), any());
    }

    @Test
    void handle_ok_persiste_yBucketiza() {
        var meterId = UUID.randomUUID();
        var cmd = new UpdateMeterReadingCommand(meterId, OffsetDateTime.now(), new BigDecimal("110"));

        when(meterRepo.existsById(meterId)).thenReturn(true);
        when(readingRepo.existsByMeterAndTimestamp(meterId, cmd.timestamp())).thenReturn(false);
        when(readingRepo.findLastByMeter(meterId))
                .thenReturn(Optional.of(new Reading(meterId, cmd.timestamp().minusMinutes(1), new BigDecimal("100"))));

        var service = new UpdateMeterReadingService(meterRepo, readingRepo, aggregator);

        UpdateMeterReadingResult res = service.handle(cmd);

        assertEquals("PERSISTED", res.status());
        verify(readingRepo, times(1)).save(any(Reading.class));
        verify(aggregator, times(1)).bucketize(eq(meterId), eq(cmd.timestamp()));
    }
}
