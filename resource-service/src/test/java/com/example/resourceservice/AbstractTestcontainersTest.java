package com.example.resourceservice;

import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.ResourceParsingProducer;
import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.S3Service;
import com.example.resourceservice.service.SongServiceClient;
import com.example.resourceservice.service.StorageService;
import com.example.resourceservice.service.StorageServiceClient;
import org.junit.jupiter.api.BeforeEach;
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

@Testcontainers
@ContextConfiguration(classes = ContainersConfig.class)
public abstract class AbstractTestcontainersTest {

//    protected static final LocalStackContainer LOCAL_STACK = LocalStackSingleton.INSTANCE;
//
//    @DynamicPropertySource
//    static void localstackProperties(DynamicPropertyRegistry registry) {
//        // Safe: resolved after container is started (thanks to the singleton's static block)
//        registry.add("aws.s3.endpoint", () ->
//            LOCAL_STACK.getEndpointOverride(LocalStackContainer.Service.S3).toString());
//        registry.add("aws.region", LOCAL_STACK::getRegion);
//        registry.add("aws.accessKeyId", LOCAL_STACK::getAccessKey);
//        registry.add("aws.secretKey", LOCAL_STACK::getSecretKey);
//    }


    @Autowired
    private StorageService storageService;
    @Autowired
    private ResourceService resourceService;
//    @Autowired
//    private ResourceRepository resourceRepository;
//    @Autowired
//    private S3Service s3Service;

//    @PostConstruct
//    void init() {
//        resourceRepository.deleteAll();
//    }
    @PostConstruct
    void mockSongServiceClient() {
        var songServiceClient = mock(SongServiceClient.class);
        doNothing().when(songServiceClient).deleteSong(anyLong());
        ReflectionTestUtils.setField(resourceService, "songServiceClient", songServiceClient);
    }

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

//
//    static class TestConfig {
//        @Bean
//        @Primary
//        public S3Client s3Client(
//            @Value("${aws.s3.endpoint}") String endpoint,
//            @Value("${aws.region}") String region,
//            @Value("${aws.accessKeyId}") String accessKey,
//            @Value("${aws.secretKey}") String secretKey) {
//
//            // Build from properties — avoids touching container during bean creation time
//            return S3Client.builder()
//                .endpointOverride(URI.create(endpoint))
//                .credentialsProvider(
//                    StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(accessKey, secretKey)
//                    )
//                )
//                .region(Region.of(region))
//                .build();
//        }
//    }

}
