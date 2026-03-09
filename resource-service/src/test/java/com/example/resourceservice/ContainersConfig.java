package com.example.resourceservice;

import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.service.ResourceParsingProducer;
import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.SongServiceClient;
import com.example.resourceservice.service.StorageService;
import com.example.resourceservice.service.StorageServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@TestConfiguration
public class ContainersConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public LocalStackContainer localStackContainer() {
        return new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(LocalStackContainer.Service.S3)
            .withReuse(true);
    }

    @Bean
    @Primary
    public S3Client s3Client(LocalStackContainer localStack) {
        return S3Client.builder()
            .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                )
            )
            .region(Region.of(localStack.getRegion()))
            .build();
    }

    @DynamicPropertySource
    static void localstackProperties(DynamicPropertyRegistry registry) {
        // You can also inject LocalStackContainer here by making this non-static with @Configuration
        // but static supplier + singleton instance works fine too.
    }
}
