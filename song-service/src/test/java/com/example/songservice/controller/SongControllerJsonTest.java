package com.example.songservice.controller;

import com.example.songservice.model.Song;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
class SongControllerJsonTest {
    @Autowired
    private JacksonTester<Song> json;

    @Value("classpath:song_metadata.json")
    private Resource songMetadataContent;

    @Test
    @DisplayName("Check Song Metadata Serialization")
    void serialize_SongMetadata() throws IOException {
        var songMetadata = new Song();
        songMetadata.setName("We are the champions");
        songMetadata.setArtist("Queen");

        assertThat(json.write(songMetadata)).hasJsonPath("name");
        assertThat(json.write(songMetadata)).hasJsonPathStringValue("name", "We are the champions");
        assertThat(json.write(songMetadata)).hasJsonPathStringValue("artist", "Queen");
    }

    @Test
    @DisplayName("Check Song Metadata Deserialization")
    void deserialize_SongMetadata() throws IOException {
        var songMetadata = json.readObject(songMetadataContent);

        assertAll(
                () -> assertNotNull(songMetadata),
                () -> assertEquals("We are the champions", songMetadata.getName()),
                () -> assertEquals("Queen", songMetadata.getArtist())
        );
    }
}