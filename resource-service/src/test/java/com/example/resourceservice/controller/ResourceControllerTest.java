package com.example.resourceservice.controller;

import com.example.resourceservice.dto.ResourceResponse;
import com.example.resourceservice.service.ResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.example.resourceservice.Builders.buildResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
public class ResourceControllerTest {

    private static final String URL_PATH = "/resources";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ResourceService resourceService;

    @Test
    void shouldCreateResource() throws Exception {
        var audio = new byte[]{0};
        var id = 1L;

        when(resourceService.uploadResource(audio)).thenReturn(id);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
                .content(audio)
                .contentType("audio/mpeg"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value((int) id));

        verify(resourceService).uploadResource(audio);
        verifyNoMoreInteractions(resourceService);
    }

    @Test
    void shouldGetResource() throws Exception {
        var resource = buildResource();
        var id = resource.getId();
        var audio = new byte[]{0};

        var resourceResponse = new ResourceResponse(id, audio);
        when(resourceService.getResource(id)).thenReturn(resourceResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PATH + "/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType("audio/mpeg"))
            .andExpect(content().bytes(audio));

        verify(resourceService).getResource(id);
        verifyNoMoreInteractions(resourceService);
    }

    @Test
    void shouldDeleteResources() throws Exception {
        var id = 1L;
        when(resourceService.deleteResources(String.valueOf(id))).thenReturn(List.of(id));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL_PATH).param("id", String.valueOf(id)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.ids.length()").value(1))
            .andExpect(jsonPath("$.ids[0]").value(id));

        verify(resourceService).deleteResources(String.valueOf(id));
        verifyNoMoreInteractions(resourceService);
    }
}
