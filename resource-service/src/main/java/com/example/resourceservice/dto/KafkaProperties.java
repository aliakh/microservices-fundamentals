package com.example.resourceservice.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
    String bootstrapUrl,
    String parsingResourcesTopic,
    String finalizingResourcesTopic,
    String finalizingResourcesConsumerGroup,
    Map<String, String> properties
) {
}
