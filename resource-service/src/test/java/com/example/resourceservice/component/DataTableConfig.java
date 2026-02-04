package com.example.resourceservice.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class DataTableConfig {

    private final ObjectMapper objectMapper;

    public DataTableConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @DataTableType
//    public CreateSongRequest convertCreateSongRequest(Map<String, String> row) {
//        return objectMapper.convertValue(row, CreateSongRequest.class);
//    }
//
//    @DataTableType
//    public Song convertSong(Map<String, String> row) {
//        return objectMapper.convertValue(row, Song.class);
//    }
}
