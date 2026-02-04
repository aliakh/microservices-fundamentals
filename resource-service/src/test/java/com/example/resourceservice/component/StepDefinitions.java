package com.example.resourceservice.component;

import com.example.resourceservice.Uuid;
import com.example.resourceservice.dto.DeleteResourcesResponse;
import com.example.resourceservice.dto.UploadResourceResponse;
import com.example.resourceservice.repository.ResourceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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
    private final ResourceRepository resourceRepository;

    @LocalServerPort
    private int port;

    private ResponseEntity<UploadResourceResponse> uploadResourceEntity;
    private ResponseEntity<byte[]> getResourceEntity;
    private ResponseEntity<DeleteResourcesResponse> deleteResourceEntity;

    public StepDefinitions(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @When("user makes POST request to upload file {string}")
    public void userUploadsFile(String file) throws IOException {
        var audio = new ClassPathResource(FILE_PATH + file).getInputStream().readAllBytes();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        var requestEntity = new HttpEntity<>(audio, headers);

        uploadResourceEntity = restTemplate.postForEntity(URL_HOST + port + "/resources", requestEntity, UploadResourceResponse.class);
    }

    @Then("the resource creation response code is {int}")
    public void checkCreateResourceResponseCode(int responseStatus) {
        assertThat(uploadResourceEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the resource creation content type is {string}")
    public void checkCreateResourceResponseContentType(String contentType) {
        assertThat(uploadResourceEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the resource creation response body is")
    public void checkCreateResourceResponseBody(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<UploadResourceResponse>() {
        });
        var actualResponse = uploadResourceEntity.getBody();
        assertThat(actualResponse.id()).isEqualTo(expectedResponse.id());
    }

    @Then("the resources are saved to the database")
    public void theFollowingResourcesAreSaved(List<ResourceDto> resources) {
        resources.forEach(expectedResource -> {
                var actualResource = resourceRepository.findById(expectedResource.id()).orElseThrow();
                assertThat(actualResource.getId()).isEqualTo(expectedResource.id());
                assertThat(Uuid.isValid(actualResource.getKey())).isTrue();
            }
        );
    }

    @When("user gets resource by id={long}")
    public void userGetsResourceWithId(long id) {
        getResourceEntity = restTemplate.getForEntity(URL_HOST + port + "/resources/" + id, byte[].class);
    }

    @When("user deletes the resource by id={long}")
    public void userDeletesResourceWithId(long id) {
        deleteResourceEntity = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_HOST + port + "/resources").queryParam("id", id).build().toUri(),
            HttpMethod.DELETE,
            null,
            DeleteResourcesResponse.class
        );
    }

    @Then("the resource retrieval response code is {int}")
    public void checkGetResourceRequestResponseCode(int responseStatus) {
        assertThat(getResourceEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the resource retrieval response content type is {string}")
    public void checkGetResourceRequestResponseContentType(String contentType) {
        assertThat(getResourceEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("response body has size {long}")
    public void responseBodyHasSize(long fileSize) {
        assertThat(getResourceEntity.getBody().length).isEqualTo(fileSize);
    }

    @Then("the resource deletion response code is {int}")
    public void checkDeleteResourceRequestResponseCode(int responseStatus) {
        assertThat(deleteResourceEntity.getStatusCode().value()).isEqualTo(responseStatus);
    }

    @And("the resource deletion response content type is {string}")
    public void checkDeleteResourceRequestResponseContentType(String contentType) {
        assertThat(deleteResourceEntity.getHeaders().getContentType().toString()).isEqualTo(contentType);
    }

    @And("the resource deleting response body is")
    public void checkDeleteResourceRequestResponseBody(String json) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(json, new TypeReference<DeleteResourcesResponse>() {
        });
        var actualResponse = deleteResourceEntity.getBody();
        assertThat(actualResponse.ids()).isEqualTo(expectedResponse.ids());
    }
}
