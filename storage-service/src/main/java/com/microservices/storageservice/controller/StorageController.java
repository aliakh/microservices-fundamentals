package com.microservices.storageservice.controller;

import com.microservices.storageservice.dto.CreateStorageRequest;
import com.microservices.storageservice.dto.CreateStorageResponse;
import com.microservices.storageservice.dto.DeleteStoragesResponse;
import com.microservices.storageservice.dto.StorageDto;
import com.microservices.storageservice.service.StorageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/storages")
@Validated
public class StorageController {

    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    private StorageService storageService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateStorageResponse> createStorage(@RequestBody @Valid CreateStorageRequest createStorageRequest) {
        var createdId = storageService.createStorage(createStorageRequest);
        return ResponseEntity.ok(new CreateStorageResponse(createdId));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<StorageDto>> getAllStorages() {
        return ResponseEntity.ok(storageService.getAllStorages());
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<DeleteStoragesResponse> deleteStorages(@RequestParam("id") String csvIds) {
        var deletedIds = storageService.deleteStorages(csvIds);
        return ResponseEntity.ok(new DeleteStoragesResponse(deletedIds));
    }
}
