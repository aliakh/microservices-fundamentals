package com.microservices.storageservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

//TODO
public record CreateStorageRequest(

    @Null
    Long id,

    @NotNull
    StorageType storageType,

    @NotBlank
    String bucket,

    @NotBlank
    String path
) {
}
