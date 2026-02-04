package com.example.songservice.component;

import com.example.songservice.dto.CreateSongRequest;
import com.example.songservice.dto.CreateSongResponse;
import com.example.songservice.dto.DeleteSongsResponse;
import com.example.songservice.dto.SongDto;
import com.example.songservice.entity.Song;
import com.example.songservice.repository.SongRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinitions {

    private static final String URL_HOST = "http://localhost:";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final SongRepository songRepository;

    @LocalServerPort
    private int port;

    private ResponseEntity<CreateSongResponse> createSongEntity;
    private ResponseEntity<SongDto> getSongEntity;
    private ResponseEntity<DeleteSongsResponse> deleteSongsEntity;

    public StepDefinitions(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @When("user creates a song")
    public void sendCreateSongRequest(CreateSongRequest createSongRequest) {
        createSongEntity = restTemplate.postForEntity(URL_HOST + port + "/songs", createSongRequest, CreateSongResponse.class);
    }

    @Then("the song creation response code is {int}")
    public void checkCreateSongResponseCode(int responseStatus) {
        assertThat(createSongEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song creation content type is {string}")
    public void checkCreateSongResponseContentType(String contentType) {
        assertThat(createSongEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song creation response body is")
    public void checkCreateSongResponseBody(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<CreateSongResponse>() {
        });
        var actualResponse = createSongEntity.getBody();
        assertThat(actualResponse.id()).isEqualTo(expectedResponse.id());
    }

    @Then("the songs are saved to the database")
    public void checkDatabaseSongs(List<Song> songs) {
        songs.forEach(expectedSong -> {
                var actualSong = songRepository.findById(expectedSong.getId()).orElseThrow();
                assertThat(actualSong.getId()).isEqualTo(expectedSong.getId());
                assertThat(actualSong.getName()).isEqualTo(expectedSong.getName());
                assertThat(actualSong.getArtist()).isEqualTo(expectedSong.getArtist());
                assertThat(actualSong.getAlbum()).isEqualTo(expectedSong.getAlbum());
                assertThat(actualSong.getDuration()).isEqualTo(expectedSong.getDuration());
                assertThat(actualSong.getYear()).isEqualTo(expectedSong.getYear());
            }
        );
    }

    @When("user gets the song by id={long}")
    public void sendGetSongRequest(long id) {
        getSongEntity = restTemplate.getForEntity(URL_HOST + port + "/songs/" + id, SongDto.class);
    }

    @Then("the song retrieval response code is {int}")
    public void checkGetSongRequestResponseCode(int responseStatus) {
        assertThat(getSongEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song retrieval response content type is {string}")
    public void checkGetSongRequestResponseContentType(String contentType) {
        assertThat(getSongEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song retrieval response body is")
    public void checkGetSongRequestResponseBody(String json) throws JsonProcessingException {
        var expectedSongDto = objectMapper.readValue(json, new TypeReference<SongDto>() {
        });
        var actualSongDto = getSongEntity.getBody();
        assertThat(actualSongDto.id()).isEqualTo(expectedSongDto.id());
        assertThat(actualSongDto.name()).isEqualTo(expectedSongDto.name());
        assertThat(actualSongDto.artist()).isEqualTo(expectedSongDto.artist());
        assertThat(actualSongDto.album()).isEqualTo(expectedSongDto.album());
        assertThat(actualSongDto.duration()).isEqualTo(expectedSongDto.duration());
        assertThat(actualSongDto.year()).isEqualTo(expectedSongDto.year());
    }

    @When("user deletes the song by id={long}")
    public void sendDeleteSongRequest(long id) {
        deleteSongsEntity = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_HOST + port + "/songs").queryParam("id", id).build().toUri(),
            HttpMethod.DELETE,
            null,
            DeleteSongsResponse.class
        );
    }

    @Then("the song deletion response code is {int}")
    public void checkDeleteSongRequestResponseCode(int responseStatus) {
        assertThat(deleteSongsEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song deletion response content type is {string}")
    public void checkDeleteSongRequestResponseContentType(String contentType) {
        assertThat(deleteSongsEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song deleting response body is")
    public void checkDeleteSongRequestResponseBody(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<DeleteSongsResponse>() {
        });
        var actualResponse = deleteSongsEntity.getBody();
        assertThat(actualResponse.ids()).isEqualTo(expectedResponse.ids());
    }
}