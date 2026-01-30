package com.example.resourceservice.controller;

import com.example.resourceservice.model.Resource;
import com.example.resourceservice.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResourceControllerTest {

    private static final byte[] TEST_CONTENT = {1, 2, 3, 4};

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService service;

    @Test
    void givenMP3MultipartFile_whenPostWithRequestPart_thenReturnsOK() throws Exception {
        MockMultipartFile data = new MockMultipartFile("data", "song.mp3", "audio/mpeg", TEST_CONTENT);
        when(service.addResource(data)).thenReturn(1L);

        mockMvc.perform(multipart("/resources").file(data))
                .andExpect(content().json("{\"id\":1}"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource({
            "song.wav, audio/mpeg",
            "song.mp3, application/json",
            "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                    "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                    "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                    "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong, audio/mpeg"
    })

    @Test
    void givenResourceIdAndRange_whenGetWithId_thenReturnsPartialContent() throws Exception {
        Resource data = new Resource(1L,  new byte[]{1, 2, 3, 4}, "filename-test");
        when(service.getResourceById(1L)).thenReturn(data);

        mockMvc.perform(get("/resources/1")
                        .header(HttpHeaders.RANGE, "bytes=0-1"))
                .andExpect(content().bytes(new byte[]{1, 2}))
                .andExpect(status().isPartialContent());
    }

    @Test
    void givenResourceId_whenGetWithId_thenReturnsOK() throws Exception {
        Resource data = new Resource(1L,  new byte[]{1, 2, 3, 4}, "filename-test");
        when(service.getResourceById(1L)).thenReturn(data);

        mockMvc.perform(get("/resources/1"))
                .andExpect(content().bytes(new byte[]{1, 2, 3, 4}))
                .andExpect(status().isOk());
    }


    @Test
    void givenResourceIds_whenDelete_thenReturnOK() throws Exception {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(service.deleteResourcesByIds(ids)).thenReturn(ids);

        mockMvc.perform(delete("/resources").param("ids", "1,2,3"))
                .andExpect(content().json("[1,2,3]"))
                .andExpect(status().isOk());
    }
}