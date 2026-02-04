package com.example.songservice.controller;

import com.example.songservice.dto.CreateSongResponse;
import com.example.songservice.dto.DeleteSongsResponse;
import com.example.songservice.dto.SongDto;
import com.example.songservice.entity.Song;
import com.example.songservice.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class SongControllerApplicationTest {

    private static final String URL_PATH = "/songs";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private SongRepository songRepository;

    @BeforeEach
    void init() {
        songRepository.deleteAll();
    }

    @Test
    void shouldCreateSong() {
        var songDto = buildSongDto();

        var createSongEntity = restTemplate.postForEntity(URL_PATH, songDto, CreateSongResponse.class);
        assertEquals(HttpStatus.OK, createSongEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, createSongEntity.getHeaders().getContentType());

        var createSongResponse = createSongEntity.getBody();
        assertNotNull(createSongResponse);
        assertNotNull(createSongResponse.id());

        var foundSong = songRepository.findById(createSongResponse.id());
        assertTrue(foundSong.isPresent());
        var actualSong = foundSong.get();
        assertEquals(createSongResponse.id(), actualSong.getId());
        assertEquals(songDto.id(), actualSong.getId());
        assertEquals(songDto.name(), actualSong.getName());
        assertEquals(songDto.artist(), actualSong.getArtist());
        assertEquals(songDto.album(), actualSong.getAlbum());
        assertEquals(songDto.duration(), actualSong.getDuration());
        assertEquals(songDto.year(), actualSong.getYear());
    }

    @Test
    void shouldGetSong() {
        var savedSong = songRepository.save(buildSongEntity());

        var createSongEntity = restTemplate.getForEntity(URL_PATH + "/" + savedSong.getId(), SongDto.class);
        assertEquals(HttpStatus.OK, createSongEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, createSongEntity.getHeaders().getContentType());

        var songDto = createSongEntity.getBody();
        assertNotNull(songDto);
        assertEquals(savedSong.getId(), songDto.id());
        assertEquals(savedSong.getName(), songDto.name());
        assertEquals(savedSong.getArtist(), songDto.artist());
        assertEquals(savedSong.getAlbum(), songDto.album());
        assertEquals(savedSong.getDuration(), songDto.duration());
        assertEquals(savedSong.getYear(), songDto.year());
    }

    @Test
    void shouldDeleteSong() {
        var savedSong = songRepository.save(buildSongEntity());

        var deleteSongEntity = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_PATH).queryParam("id", savedSong.getId()).build().toUri(),
            HttpMethod.DELETE,
            null,
            DeleteSongsResponse.class
        );
        assertEquals(HttpStatus.OK, deleteSongEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, deleteSongEntity.getHeaders().getContentType());

        var deleteSongResponse = deleteSongEntity.getBody();
        assertNotNull(deleteSongResponse);
        assertNotNull(deleteSongResponse.ids());
        assertEquals(1, deleteSongResponse.ids().size());

        var foundSongs = songRepository.findAllById(deleteSongResponse.ids());
        assertNotNull(foundSongs);
        assertTrue(foundSongs.isEmpty());
    }

    private SongDto buildSongDto() {
        return new SongDto(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );
    }

    private Song buildSongEntity() {
        var song = new Song();
        song.setId(1L);
        song.setName("The song");
        song.setArtist("John Doe");
        song.setAlbum("Songs");
        song.setDuration("12:34");
        song.setYear("2020");
        return song;
    }
}
