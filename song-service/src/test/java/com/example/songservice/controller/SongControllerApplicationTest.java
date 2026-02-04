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
        var songDto = getSongDto();

        var responseEntity = restTemplate.postForEntity(URL_PATH, songDto, CreateSongResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var createSongResponse = responseEntity.getBody();
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

        var responseEntity = restTemplate.getForEntity(URL_PATH + "/" + savedSong.getId(), SongDto.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var songDto = responseEntity.getBody();
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

        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        var responseEntity = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_PATH).queryParam("id", savedSong.getId()).build().toUri(),
            HttpMethod.DELETE,
            null,
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
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );
    }

    private Song buildSongEntity() {
        Song songEntity = new Song();
        songEntity.setId(1L);
        songEntity.setName("The song");
        songEntity.setArtist("John Doe");
        songEntity.setAlbum("Songs");
        songEntity.setDuration("12:34");
        songEntity.setYear("2020");
        return songEntity;
    }
}
