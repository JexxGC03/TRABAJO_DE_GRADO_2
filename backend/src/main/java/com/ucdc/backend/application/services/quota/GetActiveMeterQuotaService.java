package com.ucdc.backend.application.services.quota;

import com.ucdc.backend.application.dto.quota.MeterQuotaResult;
import com.ucdc.backend.application.usecase.quota.GetActiveMeterQuotaUseCase;
import com.ucdc.backend.domain.repositories.MeterQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetActiveMeterQuotaService implements GetActiveMeterQuotaUseCase {

    private final MeterQuotaRepository quotaRepo;

    @Override
    public MeterQuotaResult handle(Query q) {
        var at = OffsetDateTime.now();
        var quota = quotaRepo.findActiveByMeter(q.meterId(), at)
                .orElseThrow(() -> new IllegalStateException("No active quota"));
        return new MeterQuotaResult(quota.meterId(), quota.periodicity(), quota.kwhLimit(), quota.validFrom(), quota.validTo());
    }
}
