package com.microservices.storageservice.controller;

import com.microservices.storageservice.dto.CreateStorageResponse;
import com.microservices.storageservice.dto.StorageDto;
import com.microservices.storageservice.dto.DeleteStoragesResponse;
import com.microservices.storageservice.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping("/storages")
@RestController
public class StorageController {

    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);

    private final StorageService storageService;

    private final AtomicInteger simulatedDelaySeconds = new AtomicInteger(0);

    @Value("${com.microservices.simulate.error}")
    private boolean simulateError;

    @Value("${com.microservices.simulate.delay}")
    private boolean simulateDelay;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateStorageResponse> createStorage(@RequestBody @Valid StorageDto storageDto) {
        return ResponseEntity.ok(storageService.createStorage(storageDto));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<StorageDto>> getAllStorages() {
        if (simulateError) {
            logger.warn("Exception simulated");
            throw new RuntimeException("Simulated exception");
        }
        if (simulateDelay) {
            logger.warn("Delay {} second(s) simulated", simulatedDelaySeconds.get());
            delay(simulatedDelaySeconds.getAndIncrement());
        }
        return ResponseEntity.ok(storageService.getAllStorages());
    }

    private void delay(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<DeleteStoragesResponse> deleteStorages(@RequestParam List<Long> ids) {
        var deletedIds = storageService.deleteResponses(ids);
        return ResponseEntity.ok(deletedIds);
    }
}
