package com.example.resourceservice.controller;

import com.jayway.jsonpath.JsonPath;
import com.example.resourceservice.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:tc:postgresql:17.0://localhost:5433/resource_db",
    "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
    "spring.jpa.hibernate.ddl-auto=create",
    "spring.cloud.discovery.enabled=false"
})
public class ResourceControllerMvcTest extends AbstractIntegrationTest {

    private static final String URL_PATH = "/resources";
    private static final String FILE_PATH = "/audio/audio1.mp3";
    private static final String FILE_NAME = "audio1.mp3";

    @Autowired
    private MockMvc mockMvc;

//    @Test
//    @Transactional
//    void shouldUploadResource() throws Exception {
//        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
//        var multipartFile = new MockMultipartFile(
//            "file",
//            FILE_NAME,
//            CONTENT_TYPE_AUDIO_MPEG,
//            content
//        );
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart(URL_PATH).file(multipartFile))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath(".id").isNotEmpty());
//    }
//
    @Test
    @Transactional
    void shouldGetResource() throws Exception {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
//        var multipartFile = new MockMultipartFile(
//            "file",
//            FILE_NAME,
//            CONTENT_TYPE_AUDIO_MPEG,
//            content
//        );

        var uploadResult = mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
                .content(audio)
                .contentType("audio/mpeg"))
            .andReturn();
        var uploadResponseContent = uploadResult.getResponse().getContentAsString();
        var id = JsonPath.read(uploadResponseContent, "$.id");

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PATH + "/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType("audio/mpeg"))
            .andExpect(content().bytes(audio));
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.id").value(id))
//            .andExpect(jsonPath("$.bucket").value("resources"))
//            .andExpect(jsonPath("$.key").isNotEmpty())
//            .andExpect(jsonPath("$.name").value(FILE_NAME))
//            .andExpect(jsonPath("$.size").value(audio.length));
    }
//
//    @Test
//    @Transactional
//    void shouldDownloadResource() throws Exception {
//        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
//        var multipartFile = new MockMultipartFile(
//            "file",
//            FILE_NAME,
//            CONTENT_TYPE_AUDIO_MPEG,
//            content
//        );
//
//        var uploadResult = mockMvc.perform(MockMvcRequestBuilders.multipart(URL_PATH).file(multipartFile))
//            .andReturn();
//        var uploadResponseContent = uploadResult.getResponse().getContentAsString();
//        var id = JsonPath.read(uploadResponseContent, "$.id");
//
//        mockMvc.perform(MockMvcRequestBuilders.get(URL_PATH + "/{id}/download", id))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(CONTENT_TYPE_AUDIO_MPEG))
//            .andExpect(content().bytes(content));
//    }
//
//    @Test
//    @Transactional
//    void shouldDeleteResource() throws Exception {
//        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
//        var multipartFile = new MockMultipartFile(
//            "file",
//            FILE_NAME,
//            CONTENT_TYPE_AUDIO_MPEG,
//            content
//        );
//
//        var uploadResult = mockMvc.perform(MockMvcRequestBuilders.multipart(URL_PATH).file(multipartFile))
//            .andReturn();
//        var uploadResponseContent = uploadResult.getResponse().getContentAsString();
//        var id = JsonPath.read(uploadResponseContent, "$.id");
//
//        mockMvc.perform(MockMvcRequestBuilders.delete(URL_PATH).param("ids", id.toString()))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath(".ids[0]").value(id));
//    }
}
