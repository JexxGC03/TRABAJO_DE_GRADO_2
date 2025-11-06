package com.ucdc.backend.application.services.meter;

import com.ucdc.backend.application.dto.meter.ListMyMetersQuery;
import com.ucdc.backend.application.dto.meter.ListMyMetersResult;
import com.ucdc.backend.application.mapper.MeterAppMapper;
import com.ucdc.backend.application.usecase.meter.ListMyMetersUseCase;
import com.ucdc.backend.domain.repositories.MeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListMyMetersService implements ListMyMetersUseCase {

    private final MeterRepository meterRepo;
    private final MeterAppMapper mapper;


    @Override
    public ListMyMetersResult handle(ListMyMetersQuery query) {
        var meters = meterRepo.findByUserId(query.userId());
        var items = meters.stream().map(mapper::toCardDto).toList();
        return new ListMyMetersResult(items);
    }
}
