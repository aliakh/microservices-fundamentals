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

    private final RestTemplate restTemplate = new RestTemplate();
    private final SongRepository songRepository;
    private final ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private ResponseEntity<CreateSongResponse> createSongResponse;
    private ResponseEntity<SongDto> getSongResponse;
    private ResponseEntity<DeleteSongsResponse> deleteSongsResponse;

    public StepDefinitions(SongRepository songRepository, ObjectMapper objectMapper) {
        this.songRepository = songRepository;
        this.objectMapper = objectMapper;
    }

    @When("user sends a POST request to create song")
    public void sendCreateSongRequest(CreateSongRequest createSongRequest) {
        createSongResponse = restTemplate.postForEntity(URL_HOST + port + "/songs", createSongRequest, CreateSongResponse.class);
    }

    @Then("the song creation response code is {int}")
    public void checkResponseCodeCreateSongRequest(int responseStatus) {
        assertThat(createSongResponse.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song creation content type is {string}")
    public void checkResponseContentTypeCreateSongRequest(String contentType) {
        assertThat(createSongResponse.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song creation response is")
    public void checkResponseCreateSongRequest(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<CreateSongResponse>() {
        });
        var actualResponse = createSongResponse.getBody();
        assertThat(actualResponse.id()).isEqualTo(expectedResponse.id());
    }

    @Then("the songs are saved to the database")
    public void checkDatabaseSongs(List<Song> songs) {
        songs.forEach(expectedSong -> {
                var songOptional = songRepository.findById(expectedSong.getId());
                assertThat(songOptional).isPresent();

                var actualSong = songOptional.get();
                assertThat(actualSong.getId().equals(expectedSong.getId())).isTrue();
                assertThat(actualSong.getName().equals(expectedSong.getName())).isTrue();
                assertThat(actualSong.getArtist().equals(expectedSong.getArtist())).isTrue();
                assertThat(actualSong.getAlbum().equals(expectedSong.getAlbum())).isTrue();
                assertThat(actualSong.getDuration().equals(expectedSong.getDuration())).isTrue();
                assertThat(actualSong.getYear().equals(expectedSong.getYear())).isTrue();
            }
        );
    }

    @When("user sends a GET request to get song by id={long}")
    public void sendGetSongRequest(long id) {
        getSongResponse = restTemplate.getForEntity(URL_HOST + port + "/songs/" + id, SongDto.class);
    }

    @Then("the song retrieval response code is {int}")
    public void checkResponseCodeGetSongRequest(int responseStatus) {
        assertThat(getSongResponse.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song retrieval response content type is {string}")
    public void checkResponseContentTypeGetSongRequest(String contentType) {
        assertThat(getSongResponse.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song retrieval response is")
    public void checkResponseGetSongRequest(String json) throws JsonProcessingException {
        var expectedSongDto = objectMapper.readValue(json, new TypeReference<SongDto>() {
        });
        var actualSongDto = getSongResponse.getBody();
        assertThat(actualSongDto.id()).isEqualTo(expectedSongDto.id());
        assertThat(actualSongDto.name()).isEqualTo(expectedSongDto.name());
        assertThat(actualSongDto.artist()).isEqualTo(expectedSongDto.artist());
        assertThat(actualSongDto.album()).isEqualTo(expectedSongDto.album());
        assertThat(actualSongDto.duration()).isEqualTo(expectedSongDto.duration());
        assertThat(actualSongDto.year()).isEqualTo(expectedSongDto.year());
    }

    @When("user sends a DELETE request to delete song by id={long}")
    public void sendDeleteSongRequest(long id) {
        deleteSongsResponse = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_HOST + port + "/songs").queryParam("id", id).build().toUri(),
            HttpMethod.DELETE,
            null,
            DeleteSongsResponse.class
        );
    }

    @Then("the song deletion response code is {int}")
    public void checkResponseCodeDeleteSongRequest(int responseStatus) {
        assertThat(deleteSongsResponse.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song deletion response content type is {string}")
    public void checkResponseContentTypeDeleteSongRequest(String contentType) {
        assertThat(deleteSongsResponse.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song deleting response is")
    public void checkResponseDeleteSongRequest(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<DeleteSongsResponse>() {
        });
        var actualResponse = deleteSongsResponse.getBody();
        assertThat(actualResponse.ids()).isEqualTo(expectedResponse.ids());
    }
}