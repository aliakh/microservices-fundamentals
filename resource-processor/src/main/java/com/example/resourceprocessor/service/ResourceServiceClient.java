package com.example.resourceprocessor.service;

import com.example.resourceprocessor.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "resource-service", configuration = FeignConfig.class)
public interface ResourceServiceClient {

    @GetMapping("/resources/{id}")
    byte[] getResource(@PathVariable Long id);
}
