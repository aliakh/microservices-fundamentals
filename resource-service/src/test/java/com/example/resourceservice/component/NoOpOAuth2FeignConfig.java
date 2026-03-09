package com.example.resourceservice.component;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class NoOpOAuth2FeignConfig {

    @Bean
    @Primary
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return template -> {
        };
    }
}
