package com.microservices.storageservice.exception;

public class StorageAlreadyExistsException extends RuntimeException {

    public StorageAlreadyExistsException(Long id) {
        super(String.format("Metadata for resource ID=%d already exists", id));
    }
}
