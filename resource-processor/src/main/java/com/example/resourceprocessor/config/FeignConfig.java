package com.example.resourceprocessor.config;

import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public Retryer retryer(@Value("${feign.retry.period}") long period,
                           @Value("${feign.retry.max-period}") long maxPeriod,
                           @Value("${feign.retry.max-attempts}") int maxAttempts) {
        return new Retryer.Default(period, maxPeriod, maxAttempts);
    }
}
