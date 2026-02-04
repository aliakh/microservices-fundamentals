package com.example.resourceservice.component;

import com.example.resourceservice.dto.DeleteResourcesResponse;
import com.example.resourceservice.dto.UploadResourceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.resourceservice.repository.ResourceRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class StepsDefinitions {

    private static final String FILES_PATH = "/audio/";

    private final ResourceClient2 resourceClient;
    private final ResourceRepository resourceRepository;
    private final ObjectMapper objectMapper;

    private MockMvcResponse response;
    private UploadResourceResponse resourceUploadedResponse;
    private DeleteResourcesResponse resourcesDeletedResponse;

    public StepsDefinitions(ResourceClient2 resourceClient, ResourceRepository resourceRepository, ObjectMapper objectMapper) {
        this.resourceClient = resourceClient;
        this.resourceRepository = resourceRepository;
        this.objectMapper = objectMapper;
    }

    @When("user makes POST request to upload file {string}")
    public void userUploadsFile(String file) {
        resourceUploadedResponse = uploadFile(file);
    }

    @And("resource uploaded response is")
    public void resourceUploadedResponseIs(String jsonResponse) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(jsonResponse, new TypeReference<UploadResourceResponse>() {
        });
        assertThat(resourceUploadedResponse.id()).isEqualTo(expectedResponse.id());
    }

    @Then("the following resources are saved")
    public void theFollowingResourcesAreSaved(List<Resource> resources) {
        resources.forEach(resource -> {
                Optional<com.example.resourceservice.entity.Resource> foundResource = resourceRepository.findById(resource.id());
                assertThat(foundResource).isPresent();

                com.example.resourceservice.entity.Resource actualResource = foundResource.get();
                assertThat(actualResource.getId().equals(resource.id())).isTrue();
//                assertThat(actualResource.getBucket().equals(resource.bucket())).isTrue();
                assertThat(actualResource.getKey()).isNotNull();
//                assertThat(actualResource.getName().equals(resource.name())).isTrue();
//                assertThat(actualResource.getSize().equals(resource.size())).isTrue();
            }
        );
    }

//    @Given("the following resources uploaded")
//    public void theFollowingResourcesUploaded(List<Resource> resources) {
//        resources.forEach(resource -> {
//                var resourceUploadedResponse = uploadFile(resource.key());
//                assertThat(resourceUploadedResponse.id()).isEqualTo(resource.id());
//            }
//        );
//    }

    @When("user gets resource with id={long}")
    public void userGetsResourceWithId(long id) {
        response = resourceClient.getResource(id);
    }

    @When("user deletes resource with id={long}")
    public void userDeletesResourceWithId(long id) {
        response = resourceClient.deleteResource(id);

        resourcesDeletedResponse = response.as(new TypeRef<>() {
        });
        assertThat(resourcesDeletedResponse.ids().size()).isEqualTo(1);
        assertThat(resourcesDeletedResponse.ids().iterator().next()).isEqualTo(id);
    }

    @And("resources deleted response is")
    public void resourcesDeletedResponseIs(String jsonResponse) throws JsonProcessingException {
        var expectedResponse = objectMapper.readValue(jsonResponse, new TypeReference<DeleteResourcesResponse>() {
        });
        assertThat(resourcesDeletedResponse.ids()).isEqualTo(expectedResponse.ids());
    }

    @Then("response code is {int}")
    public void responseCodeIs(int responseStatus) {
        assertThat(response.getStatusCode()).isEqualTo(responseStatus);
    }

    @And("response content type is {string}")
    public void responseContentTypeIs(String contentType) {
        assertThat(response.getContentType()).isEqualTo(contentType);
    }

    @And("response body has size {long}")
    public void responseBodyHasSize(long fileSize) {
        assertThat(response.asByteArray().length).isEqualTo(fileSize);
    }

    public UploadResourceResponse uploadFile(String file) {
        try (InputStream inputStream = new ClassPathResource(FILES_PATH + file).getInputStream()) {
            response = resourceClient.uploadResource(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response.as(new TypeRef<>() {
        });
    }
}
