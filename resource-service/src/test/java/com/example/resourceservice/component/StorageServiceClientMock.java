package com.example.resourceservice.component;

import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import com.example.resourceservice.service.StorageService;
import com.example.resourceservice.service.StorageServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Component
public class StorageServiceClientMock {

    @Autowired
    private StorageService storageService;

    @PostConstruct
    void init() {
        var storageServiceClient = mock(StorageServiceClient.class);
        var storages = List.of(
            new StorageDto(1L, StorageType.STAGING, "resources-staging", "/staging/"),
            new StorageDto(2L, StorageType.PERMANENT, "resources-permanent", "/permanent/")
        );
        when(storageServiceClient.getAllStorages()).thenReturn(storages);
        ReflectionTestUtils.setField(storageService, "storageServiceClient", storageServiceClient);
    }
}
