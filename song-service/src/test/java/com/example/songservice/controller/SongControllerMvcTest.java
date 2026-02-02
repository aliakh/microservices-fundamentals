package com.example.songservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.example.songservice.dto.SongDto;
import com.example.songservice.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
public class SongControllerMvcTest {

    private static final String URL_PATH = "/songs";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SongRepository songRepository;

    @Test
    @Transactional
    void shouldCreateSong() throws Exception {
        var songDto = getSongDto();

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(songDto.id()));
    }

    @Test
    @Transactional
    void shouldGetSong() throws Exception {
        var songDto = getSongDto();

        var resultActions = mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songDto))).andReturn();
        String CreateSongResponse = resultActions.getResponse().getContentAsString();
        var id = JsonPath.read(CreateSongResponse, "$.id");

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PATH + "/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(songDto.id().intValue()))
            .andExpect(jsonPath("$.name").value(songDto.name()))
            .andExpect(jsonPath("$.artist").value(songDto.artist()))
            .andExpect(jsonPath("$.album").value(songDto.album()))
            .andExpect(jsonPath("$.duration").value(songDto.duration()))
            .andExpect(jsonPath("$.year").value(songDto.year()));
    }

    @Test
    @Transactional
    void shouldDeleteSongs() throws Exception {
        var songDto = getSongDto();

        var resultActions = mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(songDto))).andReturn();
        String DeleteSongsResponse = resultActions.getResponse().getContentAsString();
        var id = JsonPath.read(DeleteSongsResponse, "$.id");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL_PATH).param("id", id.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.ids.length()").value(1))
            .andExpect(jsonPath("$.ids[0]").value(id));
    }

    private SongDto getSongDto() {
        return new SongDto(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );
    }
}
