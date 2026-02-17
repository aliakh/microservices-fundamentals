package com.microservices.storageservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

//TODO
public class StorageNotFoundException2 extends ResponseStatusException {

    public StorageNotFoundException2(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }

    public StorageNotFoundException2(String reason, Throwable cause) {
        super(HttpStatus.NOT_FOUND, reason, cause);
    }
}
