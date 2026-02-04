package com.example.e2etests.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StepsDefinitions {

    private static final Logger logger = LoggerFactory.getLogger(StepsDefinitions.class);

    private static final String RESOURCES_URL = "http://localhost:8083/resources";
    private static final String SONGS_URL = "http://localhost:8084/songs";
    private static final String FILE_PATH = "/audio/audio2.mp3";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private Integer resourceId;

    public StepsDefinitions(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @When("the user uploads the resource {string} to the resource service")
    public void uploadResource(String fileName) throws IOException {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        var requestEntity = new HttpEntity<>(audio, headers);

        var responseEntity = restTemplate.postForEntity(RESOURCES_URL, requestEntity, Map.class);
        assertNotNull(responseEntity);

        assertEquals(200, responseEntity.getStatusCode().value());
        var responseBody = responseEntity.getBody();
        assertNotNull(responseBody);

        resourceId = (int) responseBody.get("id");
        assertNotNull(resourceId);
    }

    @Then("the user waits for the resource processor to parse the resource")
    public void waitResourceParsed() throws InterruptedException {
        for (var i = 0; i < 60; i++) {
            try {
                var responseEntity = restTemplate.getForEntity(SONGS_URL + "/" + resourceId, Map.class);
                if (responseEntity.getStatusCode().value() == 200) {
                    break;
                }
            } catch (HttpClientErrorException.NotFound e) {
                logger.info("wait {} second(s) ", i + 1);
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Then("the user retrieves the song metadata from the song service")
    public void retrieveSongMetadata(String json) throws JsonProcessingException {
        var responseEntity = restTemplate.getForEntity(SONGS_URL + "/" + resourceId, Map.class);
        assertEquals(200, responseEntity.getStatusCode().value());

        var expectedMetadata = objectMapper.readValue(json, new TypeReference<Map<?, ?>>() {
        });
        var actualMetadata = responseEntity.getBody();
        assertNotNull(actualMetadata);

        expectedMetadata.keySet().forEach(key -> {
            assertEquals(expectedMetadata.get(key), actualMetadata.get(key));
        });
    }
}
