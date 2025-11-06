package com.ucdc.backend.application.services.alert;

import com.ucdc.backend.application.usecase.alert.ResolveAlertUseCase;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.repositories.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ResolveAlertService implements ResolveAlertUseCase {

    private final AlertRepository alertRepo;

    @Override
    public void handle(UUID alertId) {
        var alert = alertRepo.findById(alertId)
                .orElseThrow(() -> new NotFoundException("Alert", alertId));
        alert.resolve();
        alertRepo.save(alert);
    }
}
