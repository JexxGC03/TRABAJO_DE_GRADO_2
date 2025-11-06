package com.ucdc.backend.application.service.meter;

import com.ucdc.backend.application.dto.meter.GetMeterReadingsQuery;
import com.ucdc.backend.application.dto.meter.GetMeterReadingsResult;
import com.ucdc.backend.application.dto.meter.ReadingDto;
import com.ucdc.backend.application.mapper.MeterAppMapper;
import com.ucdc.backend.application.services.meter.GetMeterReadingsService;
import com.ucdc.backend.domain.repositories.MeterReadingRepository;
import com.ucdc.backend.domain.repositories.MeterRepository;
import com.ucdc.backend.domain.value.Reading;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetMeterReadingsServiceTest {

    private final MeterRepository meterRepo = mock(MeterRepository.class);
    private final MeterReadingRepository readingRepo = mock(MeterReadingRepository.class);

    private final MeterAppMapper mapper = new MeterAppMapper() {
        @Override public ReadingDto toDto(Reading r) {
            return new ReadingDto(r.timestamp(), r.kwhAccum());
        }
    };

    @Test
    void handle_meterNoExiste_lanzaNotFound() {
        var q = new GetMeterReadingsQuery(UUID.randomUUID(), null, null, 0, 10);

        when(meterRepo.existsById(q.meterId())).thenReturn(false);

        var service = new GetMeterReadingsService(meterRepo, readingRepo, mapper);

        assertThrows(RuntimeException.class, () -> service.handle(q));
        verify(readingRepo, never()).findByMeterBetweenOrdered(any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void handle_ok_devuelveLecturasOrdenadas() {
        var meterId = UUID.randomUUID();
        var from = OffsetDateTime.now().minusDays(1);
        var to = OffsetDateTime.now();
        var q = new GetMeterReadingsQuery(meterId, from, to, 0, 5);

        when(meterRepo.existsById(meterId)).thenReturn(true);

        var r1 = new Reading(meterId, from.plusHours(1), new BigDecimal("10.0"));
        var r2 = new Reading(meterId, from.plusHours(2), new BigDecimal("12.5"));

        when(readingRepo.findByMeterBetweenOrdered(eq(meterId), eq(from), eq(to), anyInt(), anyInt()))
                .thenReturn(new MeterReadingRepository.Slice<>(List.of(r1, r2), 2));

        var service = new GetMeterReadingsService(meterRepo, readingRepo, mapper);

        GetMeterReadingsResult result = service.handle(q);

        assertEquals(meterId, result.meterId());
        assertEquals(2, result.readings().size());
        assertEquals(2, result.total());
        assertTrue(result.readings().get(0).timestamp().isBefore(result.readings().get(1).timestamp()));
    }
}
