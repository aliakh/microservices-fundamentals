package com.example.resourceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StorageDto(

    @NotNull
    Long id,

    @NotNull
    StorageType storageType,

    @NotBlank
    String bucket,

    @NotBlank
    String path
) {
}
