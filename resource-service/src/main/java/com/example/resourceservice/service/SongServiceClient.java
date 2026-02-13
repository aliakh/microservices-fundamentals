package com.example.resourceservice.service;

import com.example.resourceservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-gateway", configuration = FeignConfig.class)
public interface SongServiceClient {

    @DeleteMapping("/song-service/songs")
    void deleteSong(@RequestParam("id") Long id);
}
