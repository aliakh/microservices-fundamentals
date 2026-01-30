package com.epam.songservice.contracts;

import com.epam.songservice.controller.SongController;

import com.epam.songservice.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class SongServiceContractTestBaseClass {
    @Autowired
    private SongController songController;

    @MockBean
    private SongService songService;

    @BeforeEach
    public void init() {
        RestAssuredMockMvc.standaloneSetup(this.songController);

        when(songService.addSong(any())).thenReturn(1L);
    }
}