package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.usecase.quota.GetActiveMeterQuotaUseCase;
import com.ucdc.backend.application.usecase.quota.UpsertMeterQuotaUseCase;
import com.ucdc.backend.domain.model.MeterQuota;
import com.ucdc.backend.infrastructure.web.dto.quota.QuotaRequest;
import com.ucdc.backend.infrastructure.web.dto.quota.QuotaResponse;
import com.ucdc.backend.infrastructure.web.mapper.MeterQuotaApiMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/meters/{meterId}/quota")
@RequiredArgsConstructor
public class QuotaController {

    private final UpsertMeterQuotaUseCase upsert;
    private final GetActiveMeterQuotaUseCase getActive;
    private final MeterQuotaApiMapper mapper;

    // PUT /api/meters/{meterId}/quota
    @PutMapping
    public ResponseEntity<QuotaResponse> upsert(
            @PathVariable @NotNull UUID meterId,
            @Valid @RequestBody QuotaRequest body) {

        var cmd = new UpsertMeterQuotaUseCase.Command(
                meterId,
                MeterQuota.Periodicity.valueOf(body.periodicity()),
                body.kwhLimit()
        );
        var res = upsert.handle(cmd);
        var active = getActive.handle(new GetActiveMeterQuotaUseCase.Query(meterId));
        return ResponseEntity.ok(mapper.toResponse(active));
    }

    // GET /api/meters/{meterId}/quota/active
    @GetMapping("active")
    public ResponseEntity<QuotaResponse> getActive(@PathVariable @NotNull UUID meterId) {
        var res = getActive.handle(new GetActiveMeterQuotaUseCase.Query(meterId));
        return ResponseEntity.ok(mapper.toResponse(res));
    }
}
