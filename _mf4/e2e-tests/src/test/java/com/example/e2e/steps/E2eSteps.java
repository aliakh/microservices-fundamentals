package com.example.e2e.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class E2eSteps {
    private static String API_GATEWAY_URI = "http://localhost:8080";
    private static String RESOURCE_SERVICE = "resources";
    private static String SONG_SERVICE = "songs";
    private Response resourceRequestResponse;
    private AtomicReference<Response> finalSongRequestResponse = new AtomicReference<>();

    public E2eSteps() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Given("The API Gateway is up")
    public void healthcheck() {
        Response response = given()
                .baseUri(API_GATEWAY_URI)
                .basePath("/actuator/health")
                .when()
                .get();
        String status = response.jsonPath().get("status");
        assertThat(status).isEqualTo("UP");
    }

    @When("I send a {string} request to {string} with the binary body from file: {string}")
    public void sendRequest(String mediaType, String endpoint, String path) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
        resourceRequestResponse = given()
                .baseUri(API_GATEWAY_URI)
                .contentType(mediaType)
                .body(stream)
                .when()
                .post(endpoint);
    }

    @Then("the {string} response status should be {int}")
    public void responseShouldBe(String serviceName, int code) {
        Response response = resourceRequestResponse;
        if (SONG_SERVICE == serviceName) {
            response = finalSongRequestResponse.get();
        }
        assertThat(response.statusCode()).isEqualTo(code);
    }

    @Then("the response body should be a json with the resource id")
    public void resourceShouldOnlyContainId() {
        String body = resourceRequestResponse.body().asString();
        assertThat(body).contains("id");
    }

    @When("I send a GET song request with the returned id")
    public void getSong() {
        JsonPath jsonPathEvaluator = resourceRequestResponse.jsonPath();
        final Integer id = jsonPathEvaluator.get("id");
        await().pollDelay(1, SECONDS)
                .atMost(10, SECONDS)
                .pollInterval(2, SECONDS)
                .ignoreExceptions()
                .until(() -> {
                            Response response = getSongResponse(id);
                            if (response.statusCode() != HttpStatus.SC_NOT_FOUND) {
                                finalSongRequestResponse.set(response);
                                return true;
                            }
                            return false;
                        }
                );
    }

    private static Response getSongResponse(Integer id) {
        return given()
                .baseUri(API_GATEWAY_URI)
                .basePath("/songs/{id}")
                .pathParam("id", id)
                .when()
                .get();
    }

    @Then("returned values should be: {string}, {string}, {string}, {string}, {string}")
    public void checkMetadata(String expectedName, String expectedArtist, String expectedAlbum,
                              String expectedDuration, String expectedYear) {
        JsonPath jsonPathEvaluator = finalSongRequestResponse.get().jsonPath();
        String name = jsonPathEvaluator.get("name");
        String artist = jsonPathEvaluator.get("artist");
        String album = jsonPathEvaluator.get("album");
        String duration = jsonPathEvaluator.get("duration");
        String year = jsonPathEvaluator.get("year");

        assertThat(name).isEqualTo(expectedName);
        assertThat(artist).isEqualTo(expectedArtist);
        assertThat(album).isEqualTo(expectedAlbum);
        assertThat(duration).isEqualTo(expectedDuration);
        assertThat(year).isEqualTo(expectedYear);
    }
}
