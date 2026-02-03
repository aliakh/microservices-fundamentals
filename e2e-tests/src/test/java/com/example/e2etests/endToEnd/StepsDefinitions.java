package com.example.e2etests.endToEnd;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StepsDefinitions {

    private static final String FILE_PATH = "/audio/Kevin MacLeod - Impact Moderato.mp3";

    private final RestTemplate restTemplate = new RestTemplate();

    private Long postResourceId;

    @When("upload file with name {string} to the resource service")
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

        var uploadedId = (long) responseBody.get("id");
        postResourceId = uploadedId;
        assertNotNull(uploadedId);
    }

    @Then("wait for processor service to parse data")
    public void wait_for_processor_service_to_parse_data() throws InterruptedException {
        TimeUnit.SECONDS.sleep(20);
    }

    @Then("check data is saved via GET call to the song service")
    public void check_data_is_saved_via_get_call_to_the_song_service() {
        String url = "http://localhost:8084/songs/" + postResourceId;
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
        int statusCodeValue = response.getStatusCodeValue();
        var actual = response.getBody();
//        var expected = getSongDto();

        assertEquals(200, statusCodeValue);
        assertNotNull(actual);
        assertEquals("Impact Moderato", actual.get("name"));
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
