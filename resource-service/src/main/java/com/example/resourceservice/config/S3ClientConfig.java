package com.example.resourceservice.config;

import com.example.resourceservice.dto.S3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@EnableConfigurationProperties(value = S3Properties.class)
public class S3ClientConfig {

    @Bean
    public S3Client getS3Client(S3Properties s3Properties) {
        return S3Client.builder()
            .region(Region.of(s3Properties.region()))
            .endpointOverride(s3Properties.endpointUri())
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(s3Properties.accessKeyId(), s3Properties.secretAccessKey())
                )
            )
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build();
    }
}
