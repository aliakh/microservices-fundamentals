package com.example.resourceservice.dto;

import jakarta.validation.constraints.*;

public record StorageDto(

    @NotNull
    Long id,

    @NotNull
    StorageType type,

    @NotBlank
    String bucket
) {
}
