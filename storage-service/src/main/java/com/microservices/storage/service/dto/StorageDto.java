package com.microservices.storage.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StorageDto(

    @Null
    Long id,

    @NotNull
    StorageType type,

    @NotBlank
    String bucket
) {
}
