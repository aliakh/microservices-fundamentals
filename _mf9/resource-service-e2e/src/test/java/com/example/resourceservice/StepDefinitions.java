package com.example.resourceservice;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StepDefinitions {

    private static final String RESOURCE_SERVICE_HOST = "http://localhost:8080";
    private static final String SONG_SERVICE_HOST = "http://localhost:8089";
    public static final String testFile = "/com/example/resourceservice/input/testFile.mp3";
    private HttpResponse latestResponse;
    private byte[] fileContent;
    private String resourceId;

    @Given("new mp3 file")
    public void new_mp3_file() throws IOException {
        InputStream fileStream = this.getClass().getResourceAsStream(testFile);
        fileContent = fileStream.readAllBytes();
    }

    @When("I upload file to the service")
    public void i_upload_file_to_the_service() throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(RESOURCE_SERVICE_HOST + "/api/resources"))
                .POST(HttpRequest.BodyPublishers.ofByteArray(fileContent))
                .build();

        this.latestResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Then("I receive ID of new uploaded file")
    public void i_receive_id_of_new_uploaded_file() throws InterruptedException {
        this.resourceId = (String) this.latestResponse.body();

        Assertions.assertEquals(200, latestResponse.statusCode());
        Assertions.assertTrue(StringUtils.isNotBlank(resourceId));
        delay(1000);
    }

    @Then("I can get file metadata by the given ID")
    public void i_can_download_file_from_s3_using_the_id() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SONG_SERVICE_HOST + "/api/songs/" + this.resourceId))
                .GET()
                .build();

        HttpResponse songMetadataResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, songMetadataResponse.statusCode());

        String songMetadataJSON = (String) songMetadataResponse.body();
        Assertions.assertNotNull(songMetadataJSON);
        Assertions.assertTrue(songMetadataJSON.contains(",\"name\":\"MacCunn: The Lay of the Last Minstrel - Part 2. Final chorus: O Caledonia! stern and wild\""));
    }

    private static void delay(long timeInMs) throws InterruptedException {
        Thread.currentThread().sleep(timeInMs);
    }

}
