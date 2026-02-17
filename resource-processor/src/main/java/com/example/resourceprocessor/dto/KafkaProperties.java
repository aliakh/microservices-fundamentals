package com.example.resourceprocessor.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
    String bootstrapUrl,
    String parsingResourcesTopic,
    String parsingResourcesConsumerGroupId,
    Map<String, String> properties
) {
}
