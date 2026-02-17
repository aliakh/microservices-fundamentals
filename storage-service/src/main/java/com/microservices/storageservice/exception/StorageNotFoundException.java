package com.microservices.storageservice.exception;

public class StorageNotFoundException extends RuntimeException {

    public StorageNotFoundException(Long id) {
        super(String.format("Song metadata for ID=%d not found", id));
    }
}
