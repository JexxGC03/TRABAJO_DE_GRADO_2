package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.dto.meter.UpdateMeterReadingCommand;
import com.ucdc.backend.application.dto.meter.UpdateMeterReadingResult;
import com.ucdc.backend.application.usecase.meter.UpdateMeterReadingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/meters/{meterId}/readings")
@RequiredArgsConstructor
public class ReadingController {

    private final UpdateMeterReadingUseCase update;

    @PostMapping
    public UpdateMeterReadingResult ingest(@PathVariable UUID meterId,
                                           @RequestBody ReadingReq req) {
        var cmd = new UpdateMeterReadingCommand(
                meterId, req.timestamp(), req.kwhAccum()
        );
        return update.handle(cmd);
    }

    public record ReadingReq(OffsetDateTime timestamp, BigDecimal kwhAccum) {}
}
