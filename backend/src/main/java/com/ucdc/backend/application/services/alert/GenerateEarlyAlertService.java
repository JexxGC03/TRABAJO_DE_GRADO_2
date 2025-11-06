package com.ucdc.backend.application.services.alert;

import com.ucdc.backend.application.dto.alert.GenerateAlertCommand;
import com.ucdc.backend.application.dto.alert.GenerateAlertResult;
import com.ucdc.backend.application.mapper.AlertAppMapper;
import com.ucdc.backend.application.usecase.alert.GenerateEarlyAlertUseCase;
import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.domain.repositories.AlertRepository;
import com.ucdc.backend.domain.repositories.MeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerateEarlyAlertService implements GenerateEarlyAlertUseCase {

    private final MeterRepository meterRepo;
    private final AlertRepository alertRepo;
    //private final NotificationService notifier;
    private final AlertAppMapper mapper;

    @Override
    public GenerateAlertResult handle(GenerateAlertCommand cmd) {
        if (!meterRepo.existsById(cmd.meterId())) {
            throw new IllegalArgumentException("Meter not found: " + cmd.meterId()); // 404
        }
        alertRepo.findActiveByMeter(cmd.meterId()).ifPresent(a -> {
            throw new IllegalStateException("Active alert already exists for meter"); // 409
        });

        // Regla principal: disparar alerta cuando current >= threshold (según HU-05).
        // El umbral “temprano” 80% se puede parametrizar desde dominio si lo necesitas. :contentReference[oaicite:2]{index=2} :contentReference[oaicite:3]{index=3}
        if (cmd.currentKwh().compareTo(cmd.thresholdKwh()) < 0) {
            // No hay alerta; decisión: idempotente → devolver estado “NO_ALERT”
            return new GenerateAlertResult(null, "NO_ALERT", "Consumption under threshold");
        }

        Alert alert = mapper.toDomain(cmd);
        Alert persisted = alertRepo.save(alert);

        // Notificación asíncrona según puerto (se puede “fire-and-forget”)
        //notifier.notifyUser(persisted);

        return mapper.toResult(persisted);
    }
}
