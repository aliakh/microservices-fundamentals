package com.example.resourceservice;

import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.service.ResourceParsingProducer;
import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.StorageService;
import com.example.resourceservice.service.StorageServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
@ContextConfiguration(classes = AbstractTestcontainersTest.TestConfig.class)
public abstract class AbstractTestcontainersTest {

    @Container
    protected static final LocalStackContainer LOCAL_STACK =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(LocalStackContainer.Service.S3);

    static {
        LOCAL_STACK.start();
    }

    @Autowired
    private StorageService storageService;
    @Autowired
    private ResourceService resourceService;

    @PostConstruct
    void mockStorageServiceClient() {
        var storageServiceClient = mock(StorageServiceClient.class);

        var storages = List.of(
            new StorageDto(1L, StorageType.STAGING, "resources-staging", "/staging/"),
            new StorageDto(2L, StorageType.PERMANENT, "resources-permanent", "/permanent/")
        );
        when(storageServiceClient.getAllStorages()).thenReturn(storages);

        ReflectionTestUtils.setField(storageService, "storageServiceClient", storageServiceClient);
    }

    @PostConstruct
    void mockResourceParsingProducer() {
        var resourceParsingProducer = mock(ResourceParsingProducer.class);

        doAnswer(invocation -> {
            Resource resource = invocation.getArgument(0);
            resourceService.moveResourceToPermanentStorage(resource.getId());
            return null;
        }).when(resourceParsingProducer).parseResource(any());

        ReflectionTestUtils.setField(resourceService, "resourceParsingProducer", resourceParsingProducer);
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
    }
}
