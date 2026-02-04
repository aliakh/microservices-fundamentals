package com.example.songservice.controller;

import com.example.songservice.dto.CreateSongRequest;
import com.example.songservice.dto.SongDto;
import com.example.songservice.service.SongService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SongController.class)
public class SongControllerTest {

    private static final String URL_PATH = "/songs";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private SongService songService;

    @Test
    void shouldCreateSong() throws Exception {
        var createSongRequest = buildCreateSongRequest();
        var id = createSongRequest.id();

        when(songService.createSong(createSongRequest)).thenReturn(id);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSongRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(id.intValue()));

        verify(songService).createSong(createSongRequest);
        verifyNoMoreInteractions(songService);
    }

    @Test
    void shouldGetSong() throws Exception {
        var songDto = getSongDto();
        var id = songDto.id();

        when(songService.getSongById(id)).thenReturn(songDto);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PATH + "/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(songDto.id().intValue()))
            .andExpect(jsonPath("$.name").value(songDto.name()))
            .andExpect(jsonPath("$.artist").value(songDto.artist()))
            .andExpect(jsonPath("$.album").value(songDto.album()))
            .andExpect(jsonPath("$.duration").value(songDto.duration()))
            .andExpect(jsonPath("$.year").value(songDto.year()));

        verify(songService).getSongById(id);
        verifyNoMoreInteractions(songService);
    }

    @Test
    void shouldDeleteSongs() throws Exception {
        Long id = 1L;
        var ids = List.of(id);

        when(songService.deleteSongs(String.valueOf(id))).thenReturn(ids);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL_PATH).param("id", id.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.ids.length()").value(1))
            .andExpect(jsonPath("$.ids[0]").value(id));

        verify(songService).deleteSongs(String.valueOf(id));
        verifyNoMoreInteractions(songService);
    }

    private SongDto getSongDto() {
        return new SongDto(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "60",
            "2020"
        );
    }

    private CreateSongRequest buildCreateSongRequest() {
        return new CreateSongRequest(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "12:24",
            "2020"
        );
    }
}
