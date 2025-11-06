package com.ucdc.backend.application.services.meter;

import com.ucdc.backend.application.dto.meter.MeterCardDto;
import com.ucdc.backend.application.dto.meter.UpdateMeterCommand;
import com.ucdc.backend.application.mapper.MeterAppMapper;
import com.ucdc.backend.application.usecase.meter.UpdateMeterUseCase;
import com.ucdc.backend.domain.exceptions.logic.ConflictException;
import com.ucdc.backend.domain.exceptions.logic.ForbiddenException;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.model.Meter;
import com.ucdc.backend.domain.repositories.MeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMeterService implements UpdateMeterUseCase {

    private final MeterRepository meterRepo;
    private final MeterAppMapper mapper;

    @Override
    public MeterCardDto handle(UpdateMeterCommand c) {
        var current = meterRepo.findById(c.meterId())
                .orElseThrow(() -> new NotFoundException("meter not found"));

        if (!current.userId().equals(c.userId())) {
            throw new ForbiddenException("not owner");
        }

        // (recomendado) validar unicidad de serial si cambia
        if (!current.serialNumber().equalsIgnoreCase(c.serialNumber())
                && meterRepo.existsBySerialNumberIgnoreCaseAndIdNot(c.serialNumber(), current.id())) {
            throw new ConflictException("Serial already in use", c.serialNumber());
        }

        var now = OffsetDateTime.now();

        // Si NO quieres permitir cambiar type/status, conserva current.Type()/Status()
        var updated = Meter.rehydrate(
                current.id(),
                current.userId(),
                c.serialNumber(),
                current.type(),
                current.status(),
                c.provider(),
                c.installationAddress(),
                current.serviceNumber(),
                c.alias(),
                current.installedAt(),  // se preserva
                current.createdAt(),     // se preserva
                now                         // se actualiza
        );

        meterRepo.save(updated);
        return mapper.toCardDto(updated);
    }
}
