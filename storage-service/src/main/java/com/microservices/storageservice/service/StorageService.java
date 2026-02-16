package com.microservices.storageservice.service;

import com.microservices.storageservice.dto.StorageCreatedResponse;
import com.microservices.storageservice.dto.StorageDto;
import com.microservices.storageservice.dto.StoragesDeletedResponse;
import com.microservices.storageservice.service.mapper.StorageMapper;
import com.microservices.storageservice.repository.StorageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    public StorageService(StorageRepository storageRepository, StorageMapper storageMapper) {
        this.storageRepository = storageRepository;
        this.storageMapper = storageMapper;
    }

    public StorageCreatedResponse createStorage(StorageDto storageDto) {
        var storageEntity = storageMapper.toEntity(storageDto);
        var savedStorageEntry = storageRepository.save(storageEntity);
        return new StorageCreatedResponse(savedStorageEntry.getId());
    }

    public List<StorageDto> getAllStorages() {
        var storageEntities = storageRepository.findAll();
        return storageEntities.stream()
            .map(storageMapper::toDto)
            .collect(Collectors.toList());
    }

    public StoragesDeletedResponse deleteResponses(List<Long> ids) {
        var deletedIds = storageRepository.findAllById(ids)
            .stream()
            .map(storage -> {
                storageRepository.deleteById(storage.getId());
                return storage.getId();
            })
            .collect(Collectors.toList());

        return new StoragesDeletedResponse(deletedIds);
    }
}
