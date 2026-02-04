package com.example.resourceservice.component;

import com.example.resourceservice.integration.ResourceController;
import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.SongServiceClient;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@Component
public class ResourceClient {

    private static final String URL_PATH = "/resources";

    @Autowired
    private ResourceService resourceService;

    @PostConstruct
    void init() {
        var resourceController = new ResourceController();
        ReflectionTestUtils.setField(resourceController, "resourceService", resourceService);
        var songServiceClient = mock(SongServiceClient.class);
        doNothing().when(songServiceClient).deleteSong(anyLong());
        ReflectionTestUtils.setField(resourceService, "songServiceClient", songServiceClient);
        RestAssuredMockMvc.standaloneSetup(resourceController);
    }

    public MockMvcResponse uploadResource(InputStream inputStream) throws IOException {
        return given()
            .contentType("audio/mpeg")
            .body(inputStream.readAllBytes())
            .when()
            .post(URL_PATH);
    }

    public MockMvcResponse getResource(long id) {
        return given().get(URL_PATH + "/{id}", id);
    }

    public MockMvcResponse deleteResource(long id) {
        return given().delete(URL_PATH + "?id=" + id);
    }
}
