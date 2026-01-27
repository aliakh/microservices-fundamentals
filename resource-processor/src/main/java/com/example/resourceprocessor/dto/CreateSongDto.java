package com.example.resourceprocessor.dto;

public record CreateSongDto(
    Long id,
    String name,
    String artist,
    String album,
    String duration,
    String year
) {
}
