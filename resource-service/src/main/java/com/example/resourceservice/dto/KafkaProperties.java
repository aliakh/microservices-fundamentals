package com.example.resourceservice.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
    String bootstrapUrl,
    String topic
) {
}
