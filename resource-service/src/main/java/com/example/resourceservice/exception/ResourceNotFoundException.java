package com.example.resourceservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Long id) {
        super(String.format("Resource for ID=%d not found", id));
    }
}
