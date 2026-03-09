package com.example.resourceservice.config;

import com.example.resourceservice.dto.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class KafkaMockConfig {

    @Bean
    public KafkaTemplate<Long, String> kafkaTemplateMock() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    public KafkaProperties kafkaProperties() {
        return mock(KafkaProperties.class);
    }
}

