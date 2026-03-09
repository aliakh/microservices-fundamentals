package com.example.resourceservice.config;

import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import com.example.resourceservice.service.StorageServiceClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class StorageServiceClientTestConfig {

    @Bean
    public StorageServiceClient storageServiceClient() {
        var storageServiceClient = mock(StorageServiceClient.class);
        var storages = List.of(
            new StorageDto(1L, StorageType.STAGING, "resources-staging", "/staging/"),
            new StorageDto(2L, StorageType.PERMANENT, "resources-permanent", "/permanent/")
        );
        when(storageServiceClient.getAllStorages()).thenReturn(storages);
        return storageServiceClient;
    }
}
