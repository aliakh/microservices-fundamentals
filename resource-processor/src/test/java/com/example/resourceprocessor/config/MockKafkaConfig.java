package com.example.resourceprocessor.config;

import com.example.resourceprocessor.dto.KafkaProperties;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockKafkaConfig {

    @Bean
    @Primary
    public KafkaTemplate<Long, String> kafkaTemplateMock() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    public  KafkaProperties kafkaProperties() {
        return mock(KafkaProperties.class);
    };
}

