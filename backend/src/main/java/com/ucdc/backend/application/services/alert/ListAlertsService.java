package com.ucdc.backend.application.services.alert;

import com.ucdc.backend.application.usecase.alert.ListAlertsUseCase;
import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.domain.repositories.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAlertsService implements ListAlertsUseCase {

    private final AlertRepository alerts;

    @Override
    public List<Alert> handle(Query q) {
        return alerts.search(
                q.meterId(),
                q.status(),
                q.type(),
                q.granularity(),
                q.from(),
                q.to()
        );
    }
}
