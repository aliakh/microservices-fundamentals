package com.example.resourceservice.service;

import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import com.example.resourceservice.exception.StorageNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    @Autowired
    private StorageServiceClient storageServiceClient;

//    @CircuitBreaker(name = "storageServiceClient", fallbackMethod = "getAllStoragesFallback")
    public List<StorageDto> getAllStorages() {
        return storageServiceClient.getAllStorages();
    }

//    private List<StorageDto> getAllStoragesFallback(Exception e) {
//        logger.warn("Failed to read storages, using a local fallback instead", e);
//
//        return List.of(
//            new StorageDto(1L, StorageType.STAGING, "resources-staging", "/staging/"),
//            new StorageDto(2L, StorageType.PERMANENT, "resources-permanent", "/permanent/")
//        );
//    }

    public StorageDto getStorageById(long storageId) {
        var storages = getAllStorages();
        return storages
            .stream()
            .filter(storageDto -> storageId == storageDto.id())
            .findAny()
            .orElseThrow(() -> new StorageNotFoundException(String.format("Storage for ID=%d not found", storageId)));
    }

    public StorageDto getStagingStorage() {
        var storages = getAllStorages();
        return storages
            .stream()
            .filter(storage -> StorageType.STAGING.equals(storage.storageType()))
            .findFirst()
            .orElseThrow(() -> new StorageNotFoundException(String.format("Staging storage not found among storages: %s", storages)));
    }

    public StorageDto getPermanentStorage() {
        var storages = getAllStorages();
        return storages
            .stream()
            .filter(storage -> StorageType.PERMANENT.equals(storage.storageType()))
            .findFirst()
            .orElseThrow(() -> new StorageNotFoundException(String.format("Permanent storage not found among storages: %s", storages)));
    }
}
