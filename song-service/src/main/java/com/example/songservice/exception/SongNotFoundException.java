package com.example.songservice.exception;

public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException(Long id) {
        super(String.format("Song metadata for ID=%d not found", id));
    }
}
