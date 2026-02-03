package com.example.resourceprocessor.contracts;

import com.example.resourceprocessor.dto.CreateSongResponse;
import com.example.resourceprocessor.dto.SongDto;
import com.example.resourceprocessor.dto.DeleteSongsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

//    @Test
//    void pingStub() {
//        ResponseEntity<Void> response = restTemplate.getForEntity("http://localhost:8082/ping", Void.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void shouldCreateSong() {
        var songDto = buildSongDto();
        var id = songDto.id();

        var response = restTemplate.postForEntity(URL, songDto, CreateSongResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(id);
    }

    @Test
    void shouldGetSong() {
        var songDto = buildSongDto();
        var id = songDto.id();

        var response = restTemplate.getForEntity(URL + "/" + id, SongDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(songDto);
    }

    @Test
    void shouldDeleteSong() {
        var id = 1L;
        var deleteResponseDto = new DeleteSongsResponse(List.of(id));

        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        var response = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL).queryParam("id", id).build().toUri(),
            HttpMethod.DELETE,
            null/*new HttpEntity<>(headers)*/,
            DeleteSongsResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(deleteResponseDto);
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
