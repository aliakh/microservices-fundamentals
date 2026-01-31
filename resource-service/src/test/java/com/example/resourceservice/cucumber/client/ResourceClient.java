package com.example.resourceservice.cucumber.client;

import com.example.resourceservice.controller.ResourceController;
import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.validation.IdValidator;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.io.InputStream;

//import static com.example.resourceservice.service.Constants.CONTENT_TYPE_AUDIO_MPEG;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@Component
public class ResourceClient {

    private static final String URL_PATH = "/resources";

    @Autowired
    private ResourceService resourceService;

    @PostConstruct
    void init() {
        var resourceController = new ResourceController();
        ReflectionTestUtils.setField(resourceController, "resourceService", resourceService);
        RestAssuredMockMvc.standaloneSetup(resourceController);
    }

    public MockMvcResponse uploadResource(InputStream inputStream, String fileName) {
        return given()
            .multiPart("file", fileName, inputStream, "audio/mpeg")
            .post(URL_PATH);
    }

    public MockMvcResponse downloadResource(long id) {
        return given().get(URL_PATH + "/{id}/download", id);
    }

    public MockMvcResponse deleteResource(long id) {
        return given().delete(URL_PATH + "?ids=" + id);
    }
}
