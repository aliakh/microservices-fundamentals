package com.example.songservice.contracts;

import com.example.songservice.controller.SongController;
import com.example.songservice.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.example.songservice.Builders.buildCreateSongRequest;
import static com.example.songservice.Builders.buildSongDto;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:application-test.properties")
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
}
