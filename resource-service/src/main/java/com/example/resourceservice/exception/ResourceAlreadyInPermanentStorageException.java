package com.example.resourceservice.exception;

public class ResourceAlreadyInPermanentStorageException extends RuntimeException {

    public ResourceAlreadyInPermanentStorageException(Long id) {
        super(String.format("Resource for ID=%d is already in permanent storage", id));
    }
}
