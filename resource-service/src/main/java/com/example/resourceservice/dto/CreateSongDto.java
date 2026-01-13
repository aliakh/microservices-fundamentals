package com.example.resourceservice.dto;

public record CreateSongDto(
    Long id,
    String name,
    String artist,
    String album,
    String duration,
    String year
) {
}
