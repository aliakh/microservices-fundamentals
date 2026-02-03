package com.epam.learning.microservices.e2e.steps;

import com.epam.learning.microservices.e2e.config.ServiceUrlResolver;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@Slf4j
public class SongUploadE2ESteps {

//    @Autowired
//    private ServiceUrlResolver serviceUrlResolver;
    private final String baseUrl = "http://localhost:8080";

    private Response uploadResponse;
    private Integer resourceId;
    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Before
//    public void beforeScenario() {
//        serviceUrlResolver.logServiceConfiguration();
//    }

    @Given("all microservices are running")
    public void allMicroservicesAreRunning() {
        log.info("Checking health of all microservices...");

        // Basic connectivity check for resource service (actuator not ready yet)
        String resourceServiceUrl = baseUrl + "/resource-service/actuator/health";
        log.info("Checking resource service connectivity at: {}", resourceServiceUrl);
        given()
            .when()
            .get(resourceServiceUrl)
            .then()
            .statusCode(200);
        log.info("Resource service is reachable");

        // Basic connectivity check for song service (actuator not ready yet)
        String songServiceUrl = baseUrl + "/song-service/actuator/health";
        log.info("Checking song service connectivity at: {}", songServiceUrl);
        given()
            .when()
            .get(songServiceUrl)
            .then()
            .statusCode(200);
        log.info("Song service is reachable");

        log.info("All microservices are running and reachable");
    }

    @When("I upload a valid MP3 file to the resource service")
    public void iUploadAValidMp3FileToTheResourceService() {
        try {
            log.info("Loading MP3 test file from resources...");
            InputStream mp3Stream = getClass().getResourceAsStream("/mp3/valid-sample-with-required-tags.mp3");
            assertNotNull(mp3Stream, "MP3 test file should be found in test resources");

            byte[] mp3Data = mp3Stream.readAllBytes();
            mp3Stream.close();

            log.info("Uploading MP3 file ({} bytes) to: {}/resources", mp3Data.length, baseUrl);

            uploadResponse = given()
                .contentType("audio/mpeg")
                .body(mp3Data)
                .when()
                .post(baseUrl + "/resources")
                .then()
                .extract()
                .response();

            log.info("Upload request completed with status: {}", uploadResponse.getStatusCode());

        } catch (IOException e) {
            log.error("Failed to load MP3 test file", e);
            fail("Failed to load MP3 test file: " + e.getMessage());
        }
    }

    @When("I upload an invalid file format")
    public void iUploadAnInvalidFileFormat() {
        byte[] invalidData = "This is not an MP3 file".getBytes();
        String resourceServiceUrl = baseUrl;

        log.info("Uploading invalid file to: {}/resources", resourceServiceUrl);

        uploadResponse = given()
            .contentType("text/plain")
            .body(invalidData)
            .when()
            .post(resourceServiceUrl + "/resources")
            .then()
            .extract()
            .response();

        log.info("Invalid file upload completed with status: {}", uploadResponse.getStatusCode());
    }

    @Then("the upload should be successful")
    public void theUploadShouldBeSuccessful() {
        assertEquals(200, uploadResponse.getStatusCode());
    }

    @Then("the upload should be rejected")
    public void theUploadShouldBeRejected() {
        assertTrue(uploadResponse.getStatusCode() >= 400);
    }

    @And("I should receive a resource ID")
    public void iShouldReceiveAResourceId() {
        String responseBody = uploadResponse.getBody().asString();
        assertTrue(responseBody.contains("id"), "Response should contain an ID");

        try {
            var jsonResponse = objectMapper.readTree(responseBody);
            resourceId = jsonResponse.get("id").asInt();
            assertTrue(resourceId > 0, "Resource ID should be positive");
            log.info("Received resource ID: {}", resourceId);
        } catch (Exception e) {
            fail("Failed to parse resource ID from response: " + e.getMessage());
        }
    }

    @And("I should receive an appropriate error message")
    public void iShouldReceiveAnAppropriateErrorMessage() {
        String responseBody = uploadResponse.getBody().asString();
        assertFalse(responseBody.isEmpty(), "Error response should not be empty");
    }

    @And("the resource should trigger processing")
    public void theResourceShouldTriggerProcessing() {
        log.info("Waiting for async processing to complete...");
        try {
            Thread.sleep(2000); // Give time for processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @And("the song metadata should be extracted and stored in song service")
    public void theSongMetadataShouldBeExtractedAndStoredInSongService() {
        String songServiceUrl = baseUrl;

        log.info("Waiting for song metadata to be processed and stored in song service...");

        await()
            .atMost(30, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                log.debug("Checking for song metadata with ID: {}", resourceId);
                Response songResponse = given()
                    .when()
                    .get(songServiceUrl + "/songs/" + resourceId)
                    .then()
                    .extract()
                    .response();

                assertEquals(200, songResponse.getStatusCode(),
                    "Song metadata should be available in song service");
                log.info("Song metadata found for resource ID: {}", resourceId);
            });
    }

    @And("I should be able to retrieve both the resource and song data")
    public void iShouldBeAbleToRetrieveBothTheResourceAndSongData() {

        log.info("Verifying resource can be downloaded from: {}", baseUrl);
        given()
            .when()
            .get(baseUrl + "/resources/" + resourceId)
            .then()
            .statusCode(200)
            .contentType("audio/mpeg");
        log.info("Resource download verification successful");

        log.info("Verifying song metadata can be retrieved from: {}", baseUrl);
        given()
            .when()
            .get(baseUrl + "/songs/" + resourceId)
            .then()
            .statusCode(200);
        log.info("Song metadata retrieval verification successful");
    }
}
