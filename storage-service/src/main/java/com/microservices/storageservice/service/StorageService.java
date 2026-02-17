package com.microservices.storageservice.service;

import com.microservices.storageservice.dto.CreateStorageResponse;
import com.microservices.storageservice.dto.*;
import com.microservices.storageservice.repository.StorageRepository;
import com.microservices.storageservice.service.validation.CsvIdsParser;
import com.microservices.storageservice.service.validation.CsvIdsValidator;
import com.microservices.storageservice.service.validation.IdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private IdValidator idValidator;
    @Autowired
    private CsvIdsValidator csvIdsValidator;
    @Autowired
    private CsvIdsParser csvIdsParser;
//
//    public CreateStorageResponse createStorage(StorageDto storageDto) {
//        var storageEntity = storageMapper.toEntity(storageDto);
//        var savedStorageEntry = storageRepository.save(storageEntity);
//        return new CreateStorageResponse(savedStorageEntry.getId());
//    }

    @Transactional
    public Long createStorage(CreateStorageRequest createStorageRequest) {
        if (storageRepository.existsById(createStorageRequest.id())) {
            throw new StorageAlreadyExistsException(createStorageRequest.id());
        }

        var storage = new Storage(
            createStorageRequest.id(),
            createStorageRequest.name(),
            createStorageRequest.artist(),
            createStorageRequest.album(),
            createStorageRequest.duration(),
            createStorageRequest.year()
        );

        var createdStorage = storageRepository.save(storage);
        return createdStorage.getId();
    }
    
    public List<StorageDto> getAllStorages() {
        var storages = storageRepository.findAll();
        return storages.stream()
            .map(storage -> new StorageDto(
                    storage.getId(),
                    storage.getStorageType(),
                    storage.getBucket(),
                    storage.getPath()
                )
            )
            .collect(Collectors.toList());
    }

    @Transactional
    public List<Long> deleteStorages(String csvIds) {
        csvIdsValidator.validate(csvIds);

        return csvIdsParser.parse(csvIds)
            .stream()
            .filter(storageRepository::existsById)
            .peek(storageRepository::deleteById)
            .collect(Collectors.toList());
    }
}
