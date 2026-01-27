package com.example.resourceprocessor.service;

import com.example.resourceprocessor.config.FeignConfig;
import com.example.resourceprocessor.dto.CreateSongDto;
import com.example.resourceprocessor.dto.CreateSongResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "song-service", configuration = FeignConfig.class)
public interface SongServiceClient {

    @PostMapping("/songs")
    CreateSongResponse createSong(@RequestBody CreateSongDto createSongDto);
}
