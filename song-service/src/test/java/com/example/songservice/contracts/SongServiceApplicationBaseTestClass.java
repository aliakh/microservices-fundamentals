package com.example.songservice.contracts;

import com.example.songservice.controller.SongController;
import com.example.songservice.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.any;
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
        RestAssuredMockMvc.standaloneSetup(this.songController);
        when(songService.createSong(any())).thenReturn(1L);
    }
}