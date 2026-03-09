package com.example.resourceservice.component;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestOAuth2ClientConfig {

    @Bean
    @Primary
    public RequestInterceptor noOpFeignRequestInterceptor() {
        return template -> {
        };
    }
}
