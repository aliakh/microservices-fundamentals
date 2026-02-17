package com.example.resourceprocessor.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
    String bootstrapUrl,
    String parsingResourcesTopic,
    String parsingResourcesConsumerGroup,
    String finalizingResourcesTopic,
    Map<String, String> properties
) {
}
