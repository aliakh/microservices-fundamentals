package com.example.resourceprocessor.config;

import com.example.resourceprocessor.dto.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class KafkaTestConfig {

    @Bean
    public KafkaTemplate<Long, String> kafkaTemplateMock() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    public KafkaProperties kafkaProperties() {
        return mock(KafkaProperties.class);
    }
}

