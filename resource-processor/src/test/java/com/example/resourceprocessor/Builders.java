package com.example.resourceprocessor;

import com.example.resourceprocessor.dto.SongDto;

public interface Builders {

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
