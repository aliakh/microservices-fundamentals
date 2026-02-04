package com.example.endtoendtests.endtoend;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StepDefinitions {

    private static final Logger logger = LoggerFactory.getLogger(StepDefinitions.class);
    private static final String RESOURCES_URL = "http://localhost:8083/resources";
    private static final String SONGS_URL = "http://localhost:8084/songs";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private Integer id;

    @When("user uploads the resource {string} to the resource service")
    public void uploadResource(String path) throws IOException {
        var audio = new ClassPathResource(path).getInputStream().readAllBytes();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        var requestEntity = new HttpEntity<>(audio, headers);

        var responseEntity = restTemplate.postForEntity(RESOURCES_URL, requestEntity, Map.class);
        assertNotNull(responseEntity);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        var responseBody = responseEntity.getBody();
        assertNotNull(responseBody);

        id = (int) responseBody.get("id");
        assertNotNull(id);
    }

    @Then("user waits for the resource processor to parse the resource")
    public void waitResourceParsed() throws InterruptedException {
        for (var i = 0; i < 60; i++) {
            try {
                var responseEntity = restTemplate.getForEntity(SONGS_URL + "/" + id, Map.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    break;
                }
            } catch (HttpClientErrorException.NotFound e) {
                logger.info("wait {} second(s) ", i + 1);
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Then("user gets the song metadata from the song service")
    public void getSongMetadata(String json) throws JsonProcessingException {
        var responseEntity = restTemplate.getForEntity(SONGS_URL + "/" + id, Map.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        var expectedMetadata = objectMapper.readValue(json, new TypeReference<Map<?, ?>>() {
        });
        var actualMetadata = responseEntity.getBody();
        assertNotNull(actualMetadata);

        expectedMetadata.keySet().forEach(key ->
            assertEquals(expectedMetadata.get(key), actualMetadata.get(key))
        );
    }
}
