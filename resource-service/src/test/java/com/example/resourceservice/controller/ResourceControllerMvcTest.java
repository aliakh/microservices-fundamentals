package com.example.resourceservice.controller;

import com.example.resourceservice.AbstractTestcontainersTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class ResourceControllerMvcTest extends AbstractTestcontainersTest {

    private static final String URL_PATH = "/resources";
    private static final String FILE_PATH = "/audio/audio1.mp3";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void shouldUploadResource() throws Exception {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
                .content(audio)
                .contentType("audio/mpeg"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(".id").isNotEmpty());
    }

    @Test
    @Transactional
    void shouldGetResource() throws Exception {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();

        var uploadResourceActions = mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
                .content(audio)
                .contentType("audio/mpeg"))
            .andReturn();
        var uploadResourceResponse = uploadResourceActions.getResponse().getContentAsString();
        var id = JsonPath.read(uploadResourceResponse, "$.id");

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PATH + "/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType("audio/mpeg"))
            .andExpect(content().bytes(audio));
    }

    @Test
    @Transactional
    void shouldDeleteResource() throws Exception {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();

        var uploadResourceActions = mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
                .content(audio)
                .contentType("audio/mpeg"))
            .andReturn();
        var uploadResourceResponse = uploadResourceActions.getResponse().getContentAsString();
        var id = JsonPath.read(uploadResourceResponse, "$.id");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL_PATH).param("id", id.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.ids.length()").value(1))
            .andExpect(jsonPath("$.ids[0]").value(id));
    }
}
