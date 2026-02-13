package com.example.resourceservice;

import com.example.resourceservice.dto.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Testcontainers
@ContextConfiguration(classes = AbstractTestcontainersTest.TestConfig.class)
public abstract class AbstractTestcontainersTest {

    protected static final String TOPIC_NAME = "resources";

    @Container
    protected static final LocalStackContainer LOCAL_STACK =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(LocalStackContainer.Service.S3);

    @Container
    protected static final KafkaContainer KAFKA =
        new KafkaContainer(DockerImageName.parse("apache/kafka"));

    static {
        LOCAL_STACK.start();
        KAFKA.start();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public S3Client s3Client() {
            return S3Client.builder()
                .endpointOverride(LOCAL_STACK.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(LOCAL_STACK.getAccessKey(), LOCAL_STACK.getSecretKey())
                    )
                )
                .region(Region.of(LOCAL_STACK.getRegion()))
                .build();
        }

        @Bean
        @Primary
        public KafkaProperties kafkaProperties() {
            return new KafkaProperties(
                KAFKA.getBootstrapServers(),
                TOPIC_NAME
            );
        }
    }
}
