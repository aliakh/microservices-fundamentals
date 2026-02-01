package com.example.songservice.component;

import com.example.songservice.entity.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DataTableType;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Map;

@Configurable
public class DataTableConfig {

    private final ObjectMapper objectMapper;

    public DataTableConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @DataTableType
    public Song convertSong(Map<String, String> tableRow) {
        return objectMapper.convertValue(tableRow, Song.class);
    }
}
