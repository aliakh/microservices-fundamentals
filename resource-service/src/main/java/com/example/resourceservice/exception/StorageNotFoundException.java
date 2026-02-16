package com.example.resourceservice.exception;

//TODO handler
public class StorageNotFoundException extends RuntimeException {

    public StorageNotFoundException(Long id) {
        super(String.format("Resource for ID=%d not found", id));
    }

    public StorageNotFoundException(String message) {
        super(message);
    }
}
