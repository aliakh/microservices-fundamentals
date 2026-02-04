package com.example.songservice.component;

import com.example.songservice.dto.CreateSongRequest;
import com.example.songservice.entity.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DataTableType;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Map;

@Configurable
public class DataTableConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DataTableType
    public CreateSongRequest convertToCreateSongRequest(Map<String, String> row) {
        return objectMapper.convertValue(row, CreateSongRequest.class);
    }

    @DataTableType
    public Song convertToSong(Map<String, String> row) {
        return objectMapper.convertValue(row, Song.class);
    }
}
