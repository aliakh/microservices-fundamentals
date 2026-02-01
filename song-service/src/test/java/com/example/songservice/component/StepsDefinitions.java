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

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class StepsDefinitions {

    private static final String URL_HOST = "http://localhost:";

    private final RestTemplate restTemplate;
    private final SongRepository songRepository;
    private final ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private ResponseEntity<CreateSongResponse> createSongResponse;
    private ResponseEntity<SongDto> getSongResponse;
    private ResponseEntity<DeleteSongsResponse> deleteSongsResponse;

    public StepsDefinitions(RestTemplate restTemplate, SongRepository songRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.songRepository = songRepository;
        this.objectMapper = objectMapper;
    }

    @When("the user sends a POST request to the \\/songs endpoint")
    public void songDataSaved(CreateSongRequest createSongRequest) {
        createSongResponse = restTemplate.postForEntity(URL_HOST + port + "/songs", createSongRequest, CreateSongResponse.class);
    }

    @Then("the song creation response code is {int}")
    public void responseCodeIs(int responseStatus) {
        assertThat(createSongResponse.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song creation content type is {string}")
    public void responseContentTypeIs(String contentType) {
        assertThat(createSongResponse.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song creation response is")
    public void resourceUploadedResponseIs(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<CreateSongResponse>() {
        });
        CreateSongResponse body = createSongResponse.getBody();
        assertThat(body.id()).isEqualTo(expectedResponse.id());
    }

    @Then("the songs are saved to the database")
    public void theFollowingResourcesAreSaved(List<Song> resources) {
        resources.forEach(resource -> {
                Optional<Song> foundResource = songRepository.findById(resource.getId());
                assertThat(foundResource).isPresent();

                Song actualResource = foundResource.get();
                assertThat(actualResource.getId().equals(resource.getId())).isTrue();
                assertThat(actualResource.getName().equals(resource.getName())).isTrue();
                assertThat(actualResource.getArtist().equals(resource.getArtist())).isTrue();
                assertThat(actualResource.getAlbum().equals(resource.getAlbum())).isTrue();
                assertThat(actualResource.getDuration().equals(resource.getDuration())).isTrue();
                assertThat(actualResource.getYear().equals(resource.getYear())).isTrue();
            }
        );
    }

    @When("the user sends a GET request to the \\/songs\\/{long} endpoint")
    public void userGetsResourceWithId(long id) {
        getSongResponse = restTemplate.getForEntity(URL_HOST + port + "/songs/" + id, SongDto.class);
    }

    @Then("the song retrieval response code is {int}")
    public void response2CodeIs(int responseStatus) {
        assertThat(getSongResponse.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song retrieval response content type is {string}")
    public void response2ContentTypeIs(String contentType) {
        assertThat(getSongResponse.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song retrieval response is")
    public void resourceUploadedResponseIs2(String json) throws JsonProcessingException {
        var expectedSongDto = objectMapper.readValue(json, new TypeReference<SongDto>() {
        });
        SongDto actualSongDto = getSongResponse.getBody();
        assertThat(actualSongDto.id()).isEqualTo(expectedSongDto.id());
        assertThat(actualSongDto.name()).isEqualTo(expectedSongDto.name());
        assertThat(actualSongDto.artist()).isEqualTo(expectedSongDto.artist());
        assertThat(actualSongDto.album()).isEqualTo(expectedSongDto.album());
        assertThat(actualSongDto.duration()).isEqualTo(expectedSongDto.duration());
        assertThat(actualSongDto.year()).isEqualTo(expectedSongDto.year());
    }

    @When("the user sends a DELETE request to the \\/songs?id={long} endpoint")
    public void userGetsResource2WithId(long id) {
        URI uri = UriComponentsBuilder.fromHttpUrl(URL_HOST + port + "/songs")
            .queryParam("id", id)
            .build()
            .toUri();

        deleteSongsResponse = restTemplate.exchange(
            uri,
            HttpMethod.DELETE,
            null,
            DeleteSongsResponse.class);
//        deleteSonfResponse = restTemplate.delete(SERVICE_URL + port + "/songs/ids=" + id, DeleteSongsResponse.class);
    }

    @Then("the song deletion response code is {int}")
    public void response3CodeIs(int responseStatus) {
        assertThat(deleteSongsResponse.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song deletion response content type is {string}")
    public void response3ContentTypeIs(String contentType) {
        assertThat(deleteSongsResponse.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song deleting response is")
    public void resourceUploadedResponseIs3(String json) throws JsonProcessingException {
        var expectedSongDto = objectMapper.readValue(json, new TypeReference<DeleteSongsResponse>() {
        });
        DeleteSongsResponse actualSongDto = deleteSongsResponse.getBody();
        assertThat(actualSongDto.ids()).isEqualTo(expectedSongDto.ids());
//        assertThat(actualSongDto.name()).isEqualTo(expectedSongDto.name());
//        assertThat(actualSongDto.artist()).isEqualTo(expectedSongDto.artist());
//        assertThat(actualSongDto.album()).isEqualTo(expectedSongDto.album());
//        assertThat(actualSongDto.duration()).isEqualTo(expectedSongDto.duration());
//        assertThat(actualSongDto.year()).isEqualTo(expectedSongDto.year());
    }
}