package com.microservices.storageservice.dto;

public record StorageDto(
    Long id,
    StorageType storageType,
    String bucket,
    String path
) {
}
