package com.example.songservice.component;


import com.example.songservice.model.Song;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StepsDefinitions {

    private static final String SERVICE_URL = "http://localhost:";
    private final RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private ResponseEntity<Song> getResponse;

    private ResponseEntity<Map> postResponse;

    public StepsDefinitions(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @When("song's data saved")
    public void songDataSaved(Song song) {
        String postURL = SERVICE_URL + port + "/songs";
        postResponse = restTemplate.postForEntity(postURL, song, Map.class);
    }

    @Then("POST response code is {int}")
    public void checkPostResponseCode(Integer code) {
        int codeValue = postResponse.getStatusCode().value();
        assertEquals(code, codeValue);
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
        assertEquals(resourceId, body.getResourceId());
    }
}