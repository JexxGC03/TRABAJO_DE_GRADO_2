package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.usecase.alert.ListAlertsUseCase;
import com.ucdc.backend.application.usecase.alert.ResolveAlertUseCase;
import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.enums.AlertType;
import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.infrastructure.web.dto.alert.AlertResponse;
import com.ucdc.backend.infrastructure.web.mapper.AlertApiMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final ListAlertsUseCase listAlerts;
    private final ResolveAlertUseCase resolveAlert;
    private final AlertApiMapper mapper;

    // GET /api/alerts?meterId=&status=&type=&granularity=&from=YYYY-MM&to=YYYY-MM
    @GetMapping
    public ResponseEntity<List<AlertResponse>> list(
            @RequestParam(required = false) UUID meterId,
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) AlertType type,
            @RequestParam(required = false) Alert.Granularity granularity,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth to) {

        var q = new ListAlertsUseCase.Query(meterId, status, type, granularity, from, to);
        var alerts = listAlerts.handle(q);
        return ResponseEntity.ok(alerts.stream().map(mapper::toResponse).toList());
    }

    // PATCH /api/alerts/{id}/resolve
    @PatchMapping("{id}/resolve")
    public ResponseEntity<Void> resolve(@PathVariable @NotNull UUID id) {
        resolveAlert.handle(id);          // idempotente: si ya estaba resuelta, que no falle
        return ResponseEntity.noContent().build(); // 204
    }
}
