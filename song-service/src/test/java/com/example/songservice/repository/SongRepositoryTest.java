package com.example.songservice.repository;

import com.example.songservice.model.Song;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SongRepositoryTest {
    @Autowired
    private SongRepository songRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Check the context with in-memory database")
    void check_contextStarts() {
        assertAll(
                () -> assertNotNull(songRepository),
                () -> assertNotNull(testEntityManager)
        );
    }

    @Test
    @DisplayName("Check find all song method with in-memory database")
    void findAll_songs() {
        var song = getSong();

        var persistedSong = testEntityManager.persist(song);
        var actual = songRepository.findById(persistedSong.getId()).get();

        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(persistedSong.getId(), actual.getId()),
                () -> assertEquals(persistedSong.getArtist(), actual.getArtist())
        );
    }

    @Test
    @DisplayName("Check save song method with in-memory database")
    void save_song() {
        var song = getSong();

        var persistedSong = testEntityManager.persist(song);
        var savedSong = songRepository.save(song);

        assertAll(
                () -> assertNotNull(savedSong),
                () -> assertEquals(persistedSong.getId(), savedSong.getId()),
                () -> assertEquals(persistedSong.getArtist(), savedSong.getArtist())
        );
    }

    @Test
    @DisplayName("Check delete song method with in-memory database")
    void delete_song() {
        var song = getSong();

        var persistedSong = testEntityManager.persist(song);
        songRepository.delete(song);

        var actual =
                StreamSupport.stream(songRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());

        assertTrue(actual.isEmpty());
    }

    private Song getSong() {
        return Song.builder()
                .name("name")
                .album("album")
                .artist("artist")
                .length("length")
                .year("2023")
                .resourceId("1")
                .build();
    }
}