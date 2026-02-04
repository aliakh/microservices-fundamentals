package com.example.resourceservice.component;

import com.example.resourceservice.dto.DeleteResourcesResponse;
import com.example.resourceservice.dto.UploadResourceResponse;
import com.example.resourceservice.repository.ResourceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinitions {

    private static final String URL_HOST = "http://localhost:";
    private static final String FILE_PATH = "/audio/";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int port;

    private final ResourceClient resourceClient;
    private final ResourceRepository resourceRepository;

    private ResponseEntity<UploadResourceResponse> uploadResourceEntity;
    private ResponseEntity<byte[]> getResourceEntity;
    private ResponseEntity<DeleteResourcesResponse> deleteResourceEntity;
    
//    private MockMvcResponse response;
//    private UploadResourceResponse uploadResourceResponse;
//    private DeleteResourcesResponse deleteResourceResponse;

    public StepDefinitions(ResourceClient resourceClient, ResourceRepository resourceRepository) {
        this.resourceClient = resourceClient;
        this.resourceRepository = resourceRepository;
    }

    @When("user makes POST request to upload file {string}")
    public void userUploadsFile(String file) throws IOException {
        var audio = new ClassPathResource(FILE_PATH+file).getInputStream().readAllBytes();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        var requestEntity = new HttpEntity<>(audio, headers);

         uploadResourceEntity = restTemplate.postForEntity(URL_HOST + port + "/resources", requestEntity, UploadResourceResponse.class);

//        uploadResourceResponse = uploadFile(file);
    }

    @Then("the song creation response code is {int}")
    public void checkCreateSongResponseCode(int responseStatus) {
        assertThat(uploadResourceEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song creation content type is {string}")
    public void checkCreateSongResponseContentType(String contentType) {
        assertThat(uploadResourceEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song creation response body is")
    public void checkCreateSongResponseBody(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<UploadResourceResponse>() {
        });
        var actualResponse = uploadResourceEntity.getBody();
        assertThat(actualResponse.id()).isEqualTo(expectedResponse.id());
    }

    @Then("the songs are saved to the database")
    public void theFollowingResourcesAreSaved(List<ResourceDto> resources) {
        resources.forEach(expectedResource -> {
                var actualResource = resourceRepository.findById(expectedResource.id()).orElseThrow();
                assertThat(actualResource.getId()).isEqualTo(expectedResource.id());
                assertThat(actualResource.getKey()).isNotNull();
            }
        );
    }

//    @And("resource uploaded response is")
//    public void resourceUploadedResponseIs(String jsonResponse) throws JsonProcessingException {
//        var expectedResponse = objectMapper.readValue(jsonResponse, new TypeReference<UploadResourceResponse>() {
//        });
//        assertThat(uploadResourceResponse.id()).isEqualTo(expectedResponse.id());
//    }

    @When("user gets resource by id={long}")
    public void userGetsResourceWithId(long id) {
        getResourceEntity = restTemplate.getForEntity(URL_HOST + port + "/resources/" + id, byte[].class);
//        response = resourceClient.getResource(id);
    }

    @When("user deletes the resource by id={long}")
    public void userDeletesResourceWithId(long id) {
        deleteResourceEntity = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_HOST + port + "/resources").queryParam("id", id).build().toUri(),
            HttpMethod.DELETE,
            null,
            DeleteResourcesResponse.class
        );        
//        response = resourceClient.deleteResource(id);
//
//        deleteResourceResponse = response.as(new TypeRef<>() {
//        });
//        assertThat(deleteResourceResponse.ids().size()).isEqualTo(1);
//        assertThat(deleteResourceResponse.ids().iterator().next()).isEqualTo(id);
    }

    @Then("the song deletion response code is {int}")
    public void checkDeleteSongRequestResponseCode(int responseStatus) {
        assertThat(deleteResourceEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song deletion response content type is {string}")
    public void checkDeleteSongRequestResponseContentType(String contentType) {
        assertThat(deleteResourceEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the song deleting response body is")
    public void checkDeleteSongRequestResponseBody(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<DeleteResourcesResponse>() {
        });
        var actualResponse = deleteResourceEntity.getBody();
        assertThat(actualResponse.ids()).isEqualTo(expectedResponse.ids());
    }

//    @Then("response code is {int}")
//    public void responseCodeIs(int responseStatus) {
//        assertThat(response.getStatusCode()).isEqualTo(responseStatus);
//    }
//
//    @And("response content type is {string}")
//    public void responseContentTypeIs(String contentType) {
//        assertThat(response.getContentType()).isEqualTo(contentType);
//    }
//
//    @And("resources deleted response is")
//    public void resourcesDeletedResponseIs(String json) throws JsonProcessingException {
//        var expectedResponse = objectMapper.readValue(json, new TypeReference<DeleteResourcesResponse>() {
//        });
//        assertThat(deleteResourceResponse.ids()).isEqualTo(expectedResponse.ids());
//    }

    @Then("the song retrieval response code is {int}")
    public void checkGetSongRequestResponseCode(int responseStatus) {
        assertThat(getResourceEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the song retrieval response content type is {string}")
    public void checkGetSongRequestResponseContentType(String contentType) {
        assertThat(getResourceEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("response body has size {long}")
    public void responseBodyHasSize(long fileSize) {
        assertThat(getResourceEntity.getBody().length).isEqualTo(fileSize);
    }

//    public UploadResourceResponse uploadFile(String file) {
//        try (var is = new ClassPathResource(FILE_PATH + file).getInputStream()) {
//            response = resourceClient.uploadResource(is);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return response.as(new TypeRef<>() {
//        });
//    }
}
