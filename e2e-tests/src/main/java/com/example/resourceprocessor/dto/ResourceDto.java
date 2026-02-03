package com.example.resourceprocessor.dto;

import java.io.Serializable;

public record ResourceDto(
    Long id,
    String key
) implements Serializable {
}
