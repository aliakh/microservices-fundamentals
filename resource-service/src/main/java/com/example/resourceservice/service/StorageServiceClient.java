package com.example.resourceservice.service;

import com.example.resourceservice.config.FeignConfig;
import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "api-gateway", contextId = "storageServiceClient", configuration = FeignConfig.class)
public interface StorageServiceClient {

    Logger logger = LoggerFactory.getLogger(StorageServiceClient.class);

    @GetMapping("/storages")
    @CircuitBreaker(name = "storageServiceClient", fallbackMethod = "getAllStoragesFallback")
    List<StorageDto> getAllStorages();

    default List<StorageDto> getAllStoragesFallback(Exception e) {
        logger.warn("Failed to read storages, using a local fallback instead", e);

        return List.of(
            new StorageDto(1L, StorageType.STAGING, "resources-staging", "/staging/"),
            new StorageDto(2L, StorageType.PERMANENT, "resources-permanent", "/permanent/")
        );
    }
}
