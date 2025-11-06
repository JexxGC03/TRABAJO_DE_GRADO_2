package com.ucdc.backend.domain.exceptions.logic;

import java.time.YearMonth;
import java.util.UUID;

public class InsufficientDataException extends RuntimeException {
    public InsufficientDataException(UUID meterId, YearMonth period) {
        super(String.format("No consumption data available for meter %s in period %s",  meterId, period));
    }
}
