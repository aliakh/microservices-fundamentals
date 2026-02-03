package com.example.e2etests.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StepsDefinitions {

    private static final String FILE_PATH = "/audio/audio2.mp3";

    private final ObjectMapper objectMapper;

    public StepsDefinitions(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    private Integer postResourceId;


    @When("upload the audio file {string} to the resource service")
    public void upload_file_to_the_resource_service(String fileName) throws IOException {
//        HttpEntity<MultiValueMap<String, Object>> entity = getMultipartEntity(fileName);
        String RESOURCES_URL = "http://localhost:8083/resources";
//        ResponseEntity<Map> response = restTemplate.postForEntity(RESOURCES_URL, entity, Map.class);

        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");

        var requestEntity = new HttpEntity<>(content, headers);

        var responseEntity = restTemplate.postForEntity(RESOURCES_URL, requestEntity, Map.class);

        Map responseBody = responseEntity.getBody();
//        assertEquals(200, responseBody.getStatusCodeValue());
        assertNotNull(responseBody);

        var uploadedId = (int) responseBody.get("id");
        postResourceId = uploadedId;
        assertNotNull(uploadedId);
    }

    @Then("wait for the resource processor to consume the resource")
    public void wait_for_processor_service_to_parse_data() throws InterruptedException {
//        TimeUnit.SECONDS.sleep(60);

        for (int i=0; i<10; i++) {
            try {
                String url = "http://localhost:8084/songs/" + postResourceId;
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
                int statusCodeValue = response.getStatusCodeValue();

                if (statusCodeValue == 200) {
                    break;
                }
            } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
                System.out.println("wait");
            }
            TimeUnit.SECONDS.sleep(5);
        }
    }

    @Then("I get the song metadata from the song service")
    public void check_data_is_saved_via_get_call_to_the_song_service(String json) throws JsonProcessingException {
        String url = "http://localhost:8084/songs/" + postResourceId;
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
        int statusCodeValue = response.getStatusCodeValue();

        var expectedSongDto = objectMapper.readValue(json, new TypeReference<Map>() {
        });
//        var actualSongDto = response.getBody();

        var actual = response.getBody();

        expectedSongDto.forEach((key,value) -> {
            assertEquals(value, actual.get(key));
        });
//        var expected = getSongDto();

//        assertEquals(200, statusCodeValue);
//        assertNotNull(actual);
//        assertEquals("Impact Moderato", actual.get("name"));
//        assertEquals(expected.getArtist(), actual.getArtist());
//        assertEquals(expected.getAlbum(), actual.getAlbum());
//        assertEquals(expected.getLength(), actual.getLength());
//        assertEquals(expected.getYear(), actual.getYear());
    }

//    private HttpEntity<MultiValueMap<String, Object>> getMultipartEntity(String fileName) {
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("Name", "sss");
//        body.add("data", new ClassPathResource(fileName));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        return new HttpEntity<>(body, headers);
//    }
//
//    private SongDto getSongDto() {
//        return new SongDto(
//            1L,
//            "Impact Moderato",
//            "John Doe",
//            "Songs",
//            "12:34",
//            "2020"
//        );
//    }
}
