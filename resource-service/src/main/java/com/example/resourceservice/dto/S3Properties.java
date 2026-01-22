package com.example.resourceservice.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "aws.s3")
public record S3Properties(
    String region,
    URI endpointUri,
    String bucket,
    String accessKeyId,
    String secretAccessKey
) {
}
