package com.microservices.storageservice.exception;

public class StorageTypeAlreadyExistsException extends RuntimeException {

    public StorageTypeAlreadyExistsException(String storageType) {
        super(String.format("Storage type %s already exists", storageType));
    }
}
