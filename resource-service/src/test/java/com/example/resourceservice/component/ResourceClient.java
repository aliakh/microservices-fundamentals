package com.example.resourceservice.component;

import com.example.resourceservice.controller.ResourceController;
import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.SongServiceClient;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@Component
public class ResourceClient {

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
}
