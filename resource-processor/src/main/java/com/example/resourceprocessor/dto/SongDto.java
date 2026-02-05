package com.example.resourceprocessor.dto;

public record SongDto(
    Long id,
    String name,
    String artist,
    String album,
    String duration,
    String year
) {
}
