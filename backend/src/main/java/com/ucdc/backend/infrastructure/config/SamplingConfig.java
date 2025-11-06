package com.ucdc.backend.infrastructure.config;

import com.ucdc.backend.domain.value.SamplingPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Duration;

@Configuration
public class SamplingConfig {

    @Bean
    public SamplingPolicy samplingPolicy() {
        return new SamplingPolicy(
                Duration.ofSeconds(60),    // minInterval
                Duration.ofMinutes(5),     // maxInterval
                new BigDecimal("10"),      // minDeltaWh = 10Wh = 0.01kWh
                0.05                       // jitter 5%
        );
    }
}
