package com.example.resourceservice.service;

import com.example.resourceservice.config.FeignConfig;
import com.example.resourceservice.dto.StorageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "api-gateway", configuration = FeignConfig.class)
public interface StorageServiceClient {

    @GetMapping("/storages")
    List<StorageDto> getAllStorages();
}
