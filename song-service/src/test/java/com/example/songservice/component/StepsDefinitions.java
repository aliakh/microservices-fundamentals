package com.example.songservice.component;

import com.example.songservice.dto.CreateSongResponse;
import com.example.songservice.entity.Song;
import com.example.songservice.repository.SongRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StepsDefinitions {

    private static final String SERVICE_URL = "http://localhost:";

    private final RestTemplate restTemplate;
    private final SongRepository songRepository;
    private final ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private ResponseEntity<CreateSongResponse> createSongResponse;
    private ResponseEntity<Song> getResponse;

    public StepsDefinitions(RestTemplate restTemplate, SongRepository songRepository, ObjectMapper objectMapper)    {
        this.restTemplate = restTemplate;
        this.songRepository = songRepository;
        this.objectMapper = objectMapper;
    }

    @When("song's data saved")
    public void songDataSaved(Song song) {
        String postURL = SERVICE_URL + port + "/songs";
        createSongResponse = restTemplate.postForEntity(postURL, song, CreateSongResponse.class);
    }

//    @Then("POST response code is {int}")
//    public void checkPostResponseCode(Integer code) {
//        int codeValue = postResponse.getStatusCode().value();
//        assertEquals(code, codeValue);
//    }

    @Then("response code is {int}")
    public void responseCodeIs(int responseStatus) {
        assertThat(createSongResponse.getStatusCode().value()).isEqualTo(responseStatus);
    }

//    @And("response content type is {string}")
//    public void responseContentTypeIs(String contentType) {
//        assertThat(postResponse.getContentType()).isEqualTo(contentType);
//    }

    @And("resource uploaded response is")
    public void resourceUploadedResponseIs(String jsonResponse) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(jsonResponse, new TypeReference<CreateSongResponse>() {
        });
        assertThat(createSongResponse.getBody().id()).isEqualTo(expectedResponse.id());
    }

    @Then("the following resources are saved")
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
    @When("GET request sent songs\\/{int}")
    public void sendGetRequest(Integer id) {
        String getURL = SERVICE_URL + port + "/songs/" + id;
        getResponse = restTemplate.getForEntity(getURL, Song.class);
    }

    @Then("GET response code is {int}")
    public void checkGetResponseCode(Integer code) {
        int codeValue = getResponse.getStatusCode().value();
        assertEquals(code, codeValue);
    }

    @And("song's data returned with resourceId {}")
    public void checkForValidResponse(Integer resourceId) {
        Song body = getResponse.getBody();
        assertNotNull(body);
        assertEquals(resourceId, body.getId());
    }
}