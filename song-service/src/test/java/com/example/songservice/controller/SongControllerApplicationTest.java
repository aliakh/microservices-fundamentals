package com.example.songservice.controller;

import com.example.songservice.dto.CreateSongResponse;
import com.example.songservice.dto.SongDto;
import com.example.songservice.dto.DeleteSongsResponse;
import com.example.songservice.entity.Song;
import com.example.songservice.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
class SongControllerApplicationTest {

    private static final String URL_PATH = "/songs";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private SongRepository songRepository;

    @BeforeEach
    void init() {
        songRepository.deleteAll();
    }

    @Test
    void shouldCreateSong() {
        var songDto = getSongDto();

        var responseEntity = testRestTemplate.postForEntity(URL_PATH, songDto, CreateSongResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var CreateSongResponse = responseEntity.getBody();
        assertNotNull(CreateSongResponse);
        assertNotNull(CreateSongResponse.id());

        var foundSongEntity = songRepository.findById(CreateSongResponse.id());
        assertTrue(foundSongEntity.isPresent());

        var actualSongEntity = foundSongEntity.get();
        assertEquals(CreateSongResponse.id(), actualSongEntity.getId());
        assertEquals(songDto.id(), actualSongEntity.getId());
        assertEquals(songDto.name(), actualSongEntity.getName());
        assertEquals(songDto.artist(), actualSongEntity.getArtist());
        assertEquals(songDto.album(), actualSongEntity.getAlbum());
        assertEquals(songDto.duration(), actualSongEntity.getDuration());
        assertEquals(songDto.year(), actualSongEntity.getYear());
    }

    @Test
    void shouldGetSong() {
        var savedSongEntity = songRepository.save(getSongEntity());

        var responseEntity = testRestTemplate.getForEntity(URL_PATH + "/" + savedSongEntity.getId(), SongDto.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var songDto = responseEntity.getBody();
        assertNotNull(songDto);
        assertEquals(savedSongEntity.getId(), songDto.id());
        assertEquals(savedSongEntity.getName(), songDto.name());
        assertEquals(savedSongEntity.getArtist(), songDto.artist());
        assertEquals(savedSongEntity.getAlbum(), songDto.album());
        assertEquals(savedSongEntity.getDuration(), songDto.duration());
        assertEquals(savedSongEntity.getYear(), songDto.year());
    }

    @Test
    void shouldDeleteSong() {
        var savedSongEntity = songRepository.save(getSongEntity());

        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        var responseEntity = testRestTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_PATH).queryParam("id", savedSongEntity.getId()).build().toUri(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            DeleteSongsResponse.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var songDeletedResponse = responseEntity.getBody();
        assertNotNull(songDeletedResponse);
        assertNotNull(songDeletedResponse.ids());
        assertEquals(1, songDeletedResponse.ids().size());

        var foundSongEntities = songRepository.findAllById(songDeletedResponse.ids());
        assertNotNull(foundSongEntities);
        assertTrue(foundSongEntities.isEmpty());
    }

    private SongDto getSongDto() {
        return new SongDto(
            1L,
            "Song",
            "John Doe",
            "Songs",
            "60",
            "2020"
        );
    }

    private Song getSongEntity() {
        Song songEntity = new Song();
        songEntity.setId(1L);
        songEntity.setName("A song");
        songEntity.setArtist("John Doe");
        songEntity.setAlbum("Songs");
        songEntity.setDuration("12:34");
        songEntity.setYear("2020");
        return songEntity;
    }
}
