package com.epam.resourceprocessor.endToEnd;


import com.epam.resourceprocessor.model.SongDataBuilder;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StepsDefinitions {
    private final RestTemplate restTemplate = new RestTemplate();

    private String postResourceId;

    @When("upload file with name {string} to the resource service")
    public void upload_file_to_the_resource_service(String fileName) {
        HttpEntity<MultiValueMap<String, Object>> entity = getMultipartEntity(fileName);
        String RESOURCES_URL = "http://localhost:8082/resources";
        ResponseEntity<Map> response = restTemplate.postForEntity(RESOURCES_URL, entity, Map.class);

        Map<String, String> responseBody = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(responseBody);

        String uploadedId = responseBody.get("id");
        postResourceId = uploadedId;
        assertNotNull(uploadedId);
    }

    @Then("wait for processor service to parse data")
    public void wait_for_processor_service_to_parse_data() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
    }

    @Then("check data is saved via GET call to the song service")
    public void check_data_is_saved_via_get_call_to_the_song_service() {
        String url = "http://localhost:8081/songs/" + postResourceId;
        ResponseEntity<SongDataBuilder> response = restTemplate.exchange(url, HttpMethod.GET, null, SongDataBuilder.class);
        int statusCodeValue = response.getStatusCodeValue();
        SongDataBuilder actual = response.getBody();
        SongDataBuilder expected = createExpectedDTO();

        assertEquals(200, statusCodeValue);
        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getArtist(), actual.getArtist());
        assertEquals(expected.getAlbum(), actual.getAlbum());
        assertEquals(expected.getLength(), actual.getLength());
        assertEquals(expected.getYear(), actual.getYear());
    }

    private HttpEntity<MultiValueMap<String, Object>> getMultipartEntity(String fileName) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("Name", "sss");
        body.add("data", new ClassPathResource(fileName));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }

    private SongDataBuilder createExpectedDTO() {
        SongDataBuilder songDTO = new SongDataBuilder();
        songDTO.setName("We are the champions");
        songDTO.setArtist("Queen");
        songDTO.setAlbum("News of the world");
        songDTO.setLength("2:59");
        songDTO.setYear(1977);
        return songDTO;
    }
}

