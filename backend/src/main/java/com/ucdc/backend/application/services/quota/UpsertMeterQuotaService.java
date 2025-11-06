package com.ucdc.backend.application.services.quota;

import com.ucdc.backend.application.usecase.quota.UpsertMeterQuotaUseCase;
import com.ucdc.backend.domain.model.MeterQuota;
import com.ucdc.backend.domain.repositories.MeterQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpsertMeterQuotaService implements UpsertMeterQuotaUseCase{

    private final MeterQuotaRepository quotaRepo;

    @Override
    public UpsertMeterQuotaUseCase.Result handle(UpsertMeterQuotaUseCase.Command cmd) {
        var now = OffsetDateTime.now();
        // cerrar si hay activa
        quotaRepo.findActiveByMeter(cmd.meterId(), now)
                .ifPresent(q -> quotaRepo.save(q.close(now)));
        // crear nueva
        var q = MeterQuota.create(UUID.randomUUID(), cmd.meterId(), cmd.periodicity(), cmd.kwhLimit());
        quotaRepo.save(q);
        return new Result(q.id());
    }
}
