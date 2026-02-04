package com.example.resourceservice.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DataTableType;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Map;

@Configurable
public class DataTableConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

//    public DataTableConfig(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }

    @DataTableType
    public ResourceDto convertToResourceDto(Map<String, String> row) {
        return objectMapper.convertValue(row, ResourceDto.class);
//        return new ResourceDto(
//            Long.valueOf(row.get("id")),
//            row.get("key")
//        );
    }
}
