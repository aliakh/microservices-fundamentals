package com.example.resourceprocessor;

import com.example.resourceprocessor.dto.CreateSongDto;
import com.example.resourceprocessor.dto.ResourceDto;
import com.example.resourceprocessor.dto.SongDto;

public interface Builders {

    static ResourceDto buildResourceDto() {
        return new ResourceDto(
            1L,
            "74bcaf90-df4f-4e55-bb63-5d84961c2f5a"
        );
    }

    static CreateSongDto buildCreateSongDto(long id) {
        return new CreateSongDto(
            id,
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );
    }

    static SongDto buildSongDto() {
        return new SongDto(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );
    }
}
