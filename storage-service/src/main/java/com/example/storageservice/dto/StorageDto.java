package com.example.storageservice.dto;

public record StorageDto(
    Long id,
    String storageType,
    String bucket,
    String path
) {
}
