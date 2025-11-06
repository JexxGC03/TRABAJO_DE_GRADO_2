package com.ucdc.backend.application.usecase.reading;

import com.ucdc.backend.domain.value.Reading;

public interface IngestReadingUseCase {
    void handle(Reading reading);
}
