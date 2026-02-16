package com.microservices.storageservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StorageNotFoundException extends ResponseStatusException {

    public StorageNotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }

    public StorageNotFoundException(String reason, Throwable cause) {
        super(HttpStatus.NOT_FOUND, reason, cause);
    }
}
