package com.example.resourceprocessor.contracts;

import com.example.resourceprocessor.model.SongDataBuilder;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(repositoryRoot = "stubs://file://song-service/target/stubs",
        ids = "com.example:song-service:1.0-SNAPSHOT", stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class ResourceProcessorContractTestBaseClass {
    private static final String SONG_BASE_URI = "http://localhost:8081/songs";
    private static final int SONG_ID = 1;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void uploadSongMetadata() {
        // given
        var songMetadata = SongDataBuilder.builder()
                .name("We are the champions")
                .artist("Queen")
                .album("News of the world")
                .length("2:59")
                .resourceId(1L)
                .year(1977)
                .build();

        // when
        var response =
                restTemplate.postForEntity(SONG_BASE_URI, songMetadata, Map.class);

        // then
        Map<String, Integer> responseBody = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(responseBody);
    }

    @Test
    public void retrieveSongMetadata() {
        // given
        var songMetadata = SongDataBuilder.builder()
                .album("News of the world")
                .artist("Queen")
                .name("We are the champions")
                .length("2:59")
                .resourceId(1L)
                .year(1977)
                .build();

        // when
        var response =
                restTemplate.getForEntity(SONG_BASE_URI + "/" + SONG_ID, SongDataBuilder.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(songMetadata);
    }
}
