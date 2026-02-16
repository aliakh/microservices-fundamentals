package com.example.resourceservice.dto;

public record StorageDto(
    Long id,
    StorageType storageType,
    String bucket,
    String path
) {
}
