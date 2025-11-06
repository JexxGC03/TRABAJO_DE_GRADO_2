package com.ucdc.backend.application.services.meter;

import com.ucdc.backend.application.dto.meter.UpdateMeterReadingCommand;
import com.ucdc.backend.application.dto.meter.UpdateMeterReadingResult;
import com.ucdc.backend.application.usecase.consumption.AggregationUseCase;
import com.ucdc.backend.application.usecase.meter.UpdateMeterReadingUseCase;
import com.ucdc.backend.domain.enums.ReadingIngestStatus;
import com.ucdc.backend.domain.exceptions.logic.ConflictException;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.repositories.MeterReadingRepository;
import com.ucdc.backend.domain.repositories.MeterRepository;
import com.ucdc.backend.domain.value.Reading;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMeterReadingService implements UpdateMeterReadingUseCase {

    private final MeterRepository meterRepo;
    private final MeterReadingRepository readingRepo;
    private final AggregationUseCase aggregation;

    @Override
    public UpdateMeterReadingResult handle(UpdateMeterReadingCommand cmd) {
        final UUID meterId = cmd.meterId();
        final OffsetDateTime ts = cmd.timestamp();
        final BigDecimal kwhAccum = cmd.kwhAccum();

        // 0) Liberación rápida
        if (meterId == null || ts == null || kwhAccum == null) {
            throw new IllegalArgumentException("meterId, timestamp and kwhAccum are required");
        }
        if (kwhAccum.signum() < 0) {
            throw new IllegalArgumentException("kwhAccum must be >= 0");
        }

        // 1) Validar que el medidor exista
        if (!meterRepo.existsById(meterId)) {
            throw new NotFoundException("Meter not found: " + meterId);
        }

        // 2) Evitar duplicados exactos (mismo timestamp para el mismo medidor)
        if (readingRepo.findAt(meterId, ts).isPresent()) { // cambia por existsAt(...) si lo tienes
            throw new ConflictException("Reading already exists for meter at timestamp: ", ts);
        }

        // 3) Monotonía no-decreciente: comparar con anterior y posterior
        //    - Si es menor que la previa -> INVALID (no persiste)
        //    - Si es mayor que la siguiente -> INVALID (rompe orden histórico)
        Optional<Reading> prevOpt = readingRepo.findPreviousBefore(meterId, ts);
        if (prevOpt.isPresent() && kwhAccum.compareTo(prevOpt.get().kwhAccum()) < 0) {
            return invalid(cmd, "Reading breaks monotonicity vs previous reading");
        }

        Optional<Reading> nextOpt = readingRepo.findNextAfter(meterId, ts);
        if (nextOpt.isPresent() && kwhAccum.compareTo(nextOpt.get().kwhAccum()) > 0) {
            return invalid(cmd, "Reading breaks monotonicity vs next reading");
        }

        // 4) Persistir lectura cruda
        Reading reading = new Reading(meterId, ts, kwhAccum);
        readingRepo.save(reading);

        // 5) Disparar agregación por ventanas que cubren el timestamp
        aggregation.bucketizeAt(meterId, ts);

        // 6) Respuesta OK
        return persisted(cmd);
    }

    /* ===================== Helpers de resultado ===================== */

    private UpdateMeterReadingResult persisted(UpdateMeterReadingCommand cmd) {
        return new UpdateMeterReadingResult(cmd.meterId(), cmd.timestamp(), ReadingIngestStatus.PERSISTED, null);
    }

    private UpdateMeterReadingResult invalid(UpdateMeterReadingCommand cmd, String reason) {
        // Política: no persistimos lecturas que rompen monotonía.
        return new UpdateMeterReadingResult(cmd.meterId(), cmd.timestamp(), ReadingIngestStatus.INVALID, reason);
    }
}
