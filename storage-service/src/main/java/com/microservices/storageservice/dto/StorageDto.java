package com.microservices.storageservice.dto;

public record StorageDto(
    Long id,
    String storageType,
    String bucket,
    String path
) {
}
