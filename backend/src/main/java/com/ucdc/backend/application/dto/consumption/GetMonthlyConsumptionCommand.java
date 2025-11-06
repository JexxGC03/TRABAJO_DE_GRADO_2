package com.ucdc.backend.application.dto.consumption;

import java.time.YearMonth;
import java.util.UUID;

public record GetMonthlyConsumptionCommand(
        UUID meterId,
        YearMonth period
) {}
