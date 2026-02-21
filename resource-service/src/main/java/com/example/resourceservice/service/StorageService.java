package com.example.resourceservice.service;

import com.example.resourceservice.dto.StorageDto;
import com.example.resourceservice.dto.StorageType;
import com.example.resourceservice.exception.StorageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageService {

    @Autowired
    private StorageServiceClient storageServiceClient;

    public List<StorageDto> getAllStorages() {
        return storageServiceClient.getAllStorages();
    }

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
