package com.example.storageservice.controller;

import com.example.storageservice.dto.CreateStorageRequest;
import com.example.storageservice.dto.CreateStorageResponse;
import com.example.storageservice.dto.DeleteStoragesResponse;
import com.example.storageservice.dto.StorageDto;
import com.example.storageservice.service.StorageService;
import jakarta.validation.Valid;
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
