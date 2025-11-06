package com.ucdc.backend.application.services.meter;

import com.ucdc.backend.application.dto.meter.GetMeterReadingsQuery;
import com.ucdc.backend.application.dto.meter.GetMeterReadingsResult;
import com.ucdc.backend.application.dto.meter.ReadingDto;
import com.ucdc.backend.application.mapper.MeterAppMapper;
import com.ucdc.backend.application.usecase.meter.GetMeterReadingsUseCase;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.repositories.MeterReadingRepository;
import com.ucdc.backend.domain.repositories.MeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMeterReadingsService implements GetMeterReadingsUseCase {

    private final MeterRepository meterRepo;
    private final MeterReadingRepository readingRepo;
    private final MeterAppMapper mapper;

    @Override
    public GetMeterReadingsResult handle(GetMeterReadingsQuery query) {
        if (!meterRepo.existsById(query.meterId())) {
            throw new NotFoundException("Meter", query.meterId());
        }
        // paginación
        int page = query.page() != null ? Math.max(query.page(), 0) : 0;
        int size = query.size() != null ? Math.max(query.size(), 1) : 100;

        var slice = readingRepo.findByMeterBetweenOrdered(
                query.meterId(), query.from(), query.to(), page, size
        ); // devuelve datos ordenados ascendente

        List<ReadingDto> dtos = slice.items().stream().map(mapper::toDto).toList();
        return new GetMeterReadingsResult(query.meterId(), dtos, slice.total());
    }
}
