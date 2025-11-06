package com.ucdc.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration

public class HttpLoggingConfig {
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        var f = new CommonsRequestLoggingFilter();
        f.setIncludeClientInfo(true);
        f.setIncludeQueryString(true);
        f.setIncludePayload(true);
        f.setIncludeHeaders(false); // ponlo en true si quieres ver headers
        f.setMaxPayloadLength(10_000);
        return f;
    }
}
