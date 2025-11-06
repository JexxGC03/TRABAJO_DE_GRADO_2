package com.ucdc.backend.application.dto.consumption;

import java.util.UUID;

public record GetAnnualConsumptionCommand(
        UUID meterId,
        int year
) {}
