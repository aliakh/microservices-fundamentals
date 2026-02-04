package com.example.resourceprocessor.contracts;

import com.example.resourceprocessor.dto.CreateSongResponse;
import com.example.resourceprocessor.dto.DeleteSongsResponse;
import com.example.resourceprocessor.dto.SongDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@AutoConfigureStubRunner(
    ids = "com.example:song-service:+:stubs:8082",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class SongServiceApplicationContractTest {

    private static final String URL = "http://localhost:8082/songs";

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void shouldCreateSong() {
        var songDto = buildSongDto();
        var id = songDto.id();

        var responseEntity = restTemplate.postForEntity(URL, songDto, CreateSongResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(id);
    }

    @Test
    void shouldGetSong() {
        var songDto = buildSongDto();
        var id = songDto.id();

        var responseEntity = restTemplate.getForEntity(URL + "/" + id, SongDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isEqualTo(songDto);
    }

    @Test
    void shouldDeleteSong() {
        var id = 1L;
        var deleteResponseDto = new DeleteSongsResponse(List.of(id));

        var responseEntity = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL).queryParam("id", id).build().toUri(),
            HttpMethod.DELETE,
            null,
            DeleteSongsResponse.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isEqualTo(deleteResponseDto);
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
}
