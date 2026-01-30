package com.example.songservice;

import com.example.songservice.model.Song;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtil {

    public static Song getDefaultSongMetadata(String resourceId) {
        return Song.builder()
                .name("name")
                .album("album")
                .artist("artist")
                .length("length")
                .year("2023")
                .resourceId(resourceId)
                .build();
    }

    public static Song getDefaultSong(String resourceId) {
        return getDefaultSongWithId(null, resourceId);
    }

    public static Song getDefaultSongWithId(Long id, String resourceId) {
        return Song.builder()
                .id(id)
                .name("name")
                .album("album")
                .artist("artist")
                .length("length")
                .year("2023")
                .resourceId(resourceId)
                .build();
    }
}
