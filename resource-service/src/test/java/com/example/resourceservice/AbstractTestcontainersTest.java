package com.example.resourceservice;

import com.example.resourceservice.config.TestcontainersConfig;
import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.service.ResourceParsingProducer;
import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.SongServiceClient;
import com.example.resourceservice.service.StorageService;
import com.example.resourceservice.service.StorageServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
@ContextConfiguration(classes = TestcontainersConfig.class)
public abstract class AbstractTestcontainersTest {

    @Autowired
    private StorageService storageService;
    @Autowired
    private ResourceService resourceService;

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
}
