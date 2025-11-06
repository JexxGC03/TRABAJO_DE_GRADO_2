package com.ucdc.backend.application.services.reading;

import com.ucdc.backend.application.services.consumption.AggregatorService;
import com.ucdc.backend.application.usecase.consumption.AggregationUseCase;
import com.ucdc.backend.application.usecase.reading.IngestReadingUseCase;
import com.ucdc.backend.domain.repositories.MeterReadingRepository;
import com.ucdc.backend.domain.value.Reading;
import com.ucdc.backend.domain.value.SamplingPolicy;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class IngestReadingService implements IngestReadingUseCase {

    private final MeterReadingRepository readingRepo;
    private final AggregationUseCase aggregation;
    private final SamplingPolicy samplingPolicy;

    @Override
    public void handle(Reading now) {
        var prev = readingRepo.findLastByMeter(now.meterId()).orElse(null);
        if (!samplingPolicy.shouldPersist(prev, now)) return;
        readingRepo.save(now);
        aggregation.bucketizeAt(now.meterId(), now.timestamp()); // MINUTELY→MONTHLY
    }
}
