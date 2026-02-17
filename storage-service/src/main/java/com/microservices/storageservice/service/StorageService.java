package com.microservices.storageservice.service;

import com.microservices.storageservice.dto.CreateStorageRequest;
import com.microservices.storageservice.dto.StorageDto;
import com.microservices.storageservice.entity.Storage;
import com.microservices.storageservice.exception.StorageTypeAlreadyExistsException;
import com.microservices.storageservice.repository.StorageRepository;
import com.microservices.storageservice.service.validation.CsvIdsParser;
import com.microservices.storageservice.service.validation.CsvIdsValidator;
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
    private CsvIdsValidator csvIdsValidator;
    @Autowired
    private CsvIdsParser csvIdsParser;

    @Transactional
    public Long createStorage(CreateStorageRequest createStorageRequest) {
        if (storageRepository.existsByStorageType(createStorageRequest.storageType())) {
            throw new StorageTypeAlreadyExistsException(createStorageRequest.storageType());
        }

        var storage = new Storage();
        storage.setStorageType(createStorageRequest.storageType());
        storage.setBucket(createStorageRequest.bucket());
        storage.setPath(createStorageRequest.path());

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
