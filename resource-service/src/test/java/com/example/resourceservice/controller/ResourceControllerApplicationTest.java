package com.example.resourceservice.controller;

import com.example.resourceservice.AbstractTestcontainersTest;
import com.example.resourceservice.dto.DeleteResourcesResponse;
import com.example.resourceservice.dto.UploadResourceResponse;
import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ResourceControllerApplicationTest extends AbstractTestcontainersTest {

    private static final String URL = "/resources";
    private static final String FILE_PATH = "/audio/audio1.mp3";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private S3Service s3Service;

    @BeforeEach
    void init() {
        resourceRepository.deleteAll();
    }

    @Test
    void shouldUploadResource() throws Exception {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        var requestEntity = new HttpEntity<>(audio, headers);

        var responseEntity = restTemplate.postForEntity(URL, requestEntity, UploadResourceResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var uploadResourceResponse = responseEntity.getBody();
        assertNotNull(uploadResourceResponse);
        assertNotNull(uploadResourceResponse.id());

        var actualResource = resourceRepository.findById(uploadResourceResponse.id()).orElseThrow();
        assertEquals(uploadResourceResponse.id(), actualResource.getId());
    }

    @Test
    void shouldGetResource() throws IOException {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
        var id = uploadResource(audio);

        var responseEntity = restTemplate.getForEntity(URL + "/" + id, byte[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("audio/mpeg", responseEntity.getHeaders().getContentType().toString());

        assertNotNull(responseEntity.getBody());
        assertArrayEquals(audio, responseEntity.getBody());
    }

    @Test
    void shouldDeleteResource() throws IOException {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
        var id = uploadResource(audio);

        var responseEntity = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL).queryParam("id", id).build().toUri(),
            HttpMethod.DELETE,
            null,
            DeleteResourcesResponse.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var deleteResourceResponse = responseEntity.getBody();
        assertNotNull(deleteResourceResponse);
        assertNotNull(deleteResourceResponse.ids());
        assertEquals(1, deleteResourceResponse.ids().size());

        assertTrue(resourceRepository.findAllById(deleteResourceResponse.ids()).isEmpty());
    }

    private long uploadResource(byte[] audio) {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        var requestEntity = new HttpEntity<>(audio, headers);

        var responseEntity = restTemplate.postForEntity(URL, requestEntity, UploadResourceResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var uploadResourceResponse = responseEntity.getBody();
        assertNotNull(uploadResourceResponse);
        assertNotNull(uploadResourceResponse.id());

        return uploadResourceResponse.id();
    }
}
