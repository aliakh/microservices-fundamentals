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
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinitions {

    private static final String FILES_PATH = "/audio/";

    private final ResourceClient resourceClient;
    private final ResourceRepository resourceRepository;
    private final ObjectMapper objectMapper;

    private MockMvcResponse response;
    private UploadResourceResponse uploadResourceResponse;
    private DeleteResourcesResponse deleteResourceResponse;

    public StepDefinitions(ResourceClient resourceClient, ResourceRepository resourceRepository, ObjectMapper objectMapper) {
        this.resourceClient = resourceClient;
        this.resourceRepository = resourceRepository;
        this.objectMapper = objectMapper;
    }

    @When("user makes POST request to upload file {string}")
    public void userUploadsFile(String file) {
        uploadResourceResponse = uploadFile(file);
    }

    @And("resource uploaded response is")
    public void resourceUploadedResponseIs(String jsonResponse) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(jsonResponse, new TypeReference<UploadResourceResponse>() {
        });
        assertThat(uploadResourceResponse.id()).isEqualTo(expectedResponse.id());
    }

    @Then("the following resources are saved")
    public void theFollowingResourcesAreSaved(List<ResourceDto> resources) {
        resources.forEach(expectedResource -> {
                var actualResource = resourceRepository.findById(expectedResource.id()).orElseThrow();
                assertThat(actualResource.getId()).isEqualTo(expectedResource.id());
                assertThat(actualResource.getKey()).isNotNull();
            }
        );
    }

    @When("user gets resource by id={long}")
    public void userGetsResourceWithId(long id) {
        response = resourceClient.getResource(id);
    }

    @When("user deletes the resource by id={long}")
    public void userDeletesResourceWithId(long id) {
        response = resourceClient.deleteResource(id);

        deleteResourceResponse = response.as(new TypeRef<>() {
        });
        assertThat(deleteResourceResponse.ids().size()).isEqualTo(1);
        assertThat(deleteResourceResponse.ids().iterator().next()).isEqualTo(id);
    }

    @Then("response code is {int}")
    public void responseCodeIs(int responseStatus) {
        assertThat(response.getStatusCode()).isEqualTo(responseStatus);
    }

    @And("response content type is {string}")
    public void responseContentTypeIs(String contentType) {
        assertThat(response.getContentType()).isEqualTo(contentType);
    }

    @And("resources deleted response is")
    public void resourcesDeletedResponseIs(String jsonResponse) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(jsonResponse, new TypeReference<DeleteResourcesResponse>() {
        });
        assertThat(deleteResourceResponse.ids()).isEqualTo(expectedResponse.ids());
    }

    @And("response body has size {long}")
    public void responseBodyHasSize(long fileSize) {
        assertThat(response.asByteArray().length).isEqualTo(fileSize);
    }

    public UploadResourceResponse uploadFile(String file) {
        try (var is = new ClassPathResource(FILES_PATH + file).getInputStream()) {
            response = resourceClient.uploadResource(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response.as(new TypeRef<>() {
        });
    }
}
