package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.application.dto.consumption.CompareActualVsProjectedResult;
import com.ucdc.backend.application.services.ProjectionServicePort;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class ProjectionServiceAdapter implements ProjectionServicePort {
    @Override
    public List<CompareActualVsProjectedResult.PointDTO> project(UUID meterId, YearMonth period, String granularity, String model) {
        return Collections.emptyList();
    }
}
