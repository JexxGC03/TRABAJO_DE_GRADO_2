package com.ucdc.backend.application.services;

import com.ucdc.backend.application.dto.consumption.CompareActualVsProjectedResult;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface ProjectionServicePort {
    List<CompareActualVsProjectedResult.PointDTO> project(UUID meterId,
                                                          YearMonth period,
                                                          String granularity,
                                                          String model);
}
