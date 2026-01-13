package com.example.songservice.exception;

public class SongAlreadyExistsException extends RuntimeException {

    public SongAlreadyExistsException(Long id) {
        super(String.format("Metadata for resource ID=%d already exists", id));
    }
}
