package com.ucdc.backend.application.services.quota;

import com.ucdc.backend.application.dto.quota.MeterQuotaCommand;
import com.ucdc.backend.application.dto.quota.MeterQuotaResult;
import com.ucdc.backend.application.usecase.quota.UpsertMeterQuotaUseCase;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.model.MeterQuota;
import com.ucdc.backend.domain.repositories.MeterQuotaHistoryRepository;
import com.ucdc.backend.domain.repositories.MeterQuotaRepository;
import com.ucdc.backend.domain.repositories.MeterRepository;
import com.ucdc.backend.domain.value.MeterQuotaVersion;
import com.ucdc.backend.infrastructure.persistence.entity.MeterQuotaHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpsertMeterQuotaService implements UpsertMeterQuotaUseCase{

    private final MeterRepository meterRepo;
    private final MeterQuotaRepository quotaRepo;
    private final MeterQuotaHistoryRepository historyRepo;


    @Override
    public MeterQuotaResult handle(UUID meterId, MeterQuotaCommand cmd) {
        // 1) validar que exista el meter
        meterRepo.findById(meterId)
                .orElseThrow(() -> new NotFoundException("Meter not found: ", meterId));

        var now = OffsetDateTime.now();

        // 2) si hay vigente -> mandar a histórico
        var activeOpt = quotaRepo.findByMeterId(meterId);
        activeOpt.ifPresent(active -> {
            var version = new MeterQuotaVersion(
                    meterId,
                    active.periodicity(),
                    active.kwhLimit(),
                    active.validFrom(),
                    now
            );
            historyRepo.save(version);
        });

        // 3) crear/actualizar la vigente (reutiliza el adapter para evitar duplicados)
        var newActive = MeterQuota.create(meterId, cmd.periodicity(), cmd.kwhLimit());
        var saved = quotaRepo.save(newActive);

        return new MeterQuotaResult(
                saved.meterId(),
                saved.periodicity(),
                saved.kwhLimit(),
                saved.validFrom(),
                saved.validTo()
        );
    }
}
