package com.example.songservice.contracts;

import com.example.songservice.controller.SongController;
import com.example.songservice.dto.CreateSongRequest;
import com.example.songservice.dto.SongDto;
import com.example.songservice.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class SongServiceApplicationBaseTestClass {

    @Autowired
    private SongController songController;
    @MockitoBean
    private SongService songService;

    @BeforeEach
    public void init() {
        RestAssuredMockMvc.standaloneSetup(songController);

        var id = 1L;
        var createSongRequest = buildCreateSongRequest();
        var songDto = buildSongDto();

        when(songService.createSong(createSongRequest)).thenReturn(id);
        when(songService.getSongById(id)).thenReturn(songDto);
        when(songService.deleteSongs(String.valueOf(id))).thenReturn(List.of(id));
    }

    private SongDto buildSongDto() {
        return new SongDto(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );
    }

    private CreateSongRequest buildCreateSongRequest() {
        return new CreateSongRequest(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );
    }
}
