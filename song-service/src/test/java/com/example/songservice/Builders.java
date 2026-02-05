package com.example.songservice;

import com.example.songservice.dto.CreateSongRequest;
import com.example.songservice.dto.SongDto;
import com.example.songservice.entity.Song;

public interface Builders {

    static CreateSongRequest buildCreateSongRequest() {
        return new CreateSongRequest(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );
    }

    static Song buildSong() {
        var song = new Song();
        song.setId(1L);
        song.setName("The song");
        song.setArtist("John Doe");
        song.setAlbum("Songs");
        song.setDuration("12:34");
        song.setYear("2020");
        return song;
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
