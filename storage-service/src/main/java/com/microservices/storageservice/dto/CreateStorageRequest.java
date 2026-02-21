package com.microservices.storageservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStorageRequest(

    @NotNull(message = "Storage type is required")
    @Size(min = 1, max = 100, message = "Storage type must be between 1 and 100 characters")
    String storageType,

    @NotBlank(message = "Bucket is required")
    @Size(min = 1, max = 100, message = "Bucket must be between 1 and 100 characters")
    String bucket,

    @NotBlank(message = "Path is required")
    @Size(min = 1, max = 100, message = "Path must be between 1 and 100 characters")
    String path
) {
}
