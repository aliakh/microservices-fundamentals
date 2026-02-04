package com.example.resourceservice.controller;

import com.example.resourceservice.AbstractIntegrationTest;
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
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ResourceControllerApplicationTest extends AbstractIntegrationTest {

    private static final String URL_PATH = "/resources";
    private static final String FILE_PATH = "/audio/audio1.mp3";
    private static final String BUCKET = "resources";
    private static final String KEY = "11111111-2222-3333-4444-555555555555";
    private static final String FILE_NAME = "audio1.mp3";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private S3Service s3Service;
//    @MockitoBean
//    private SongServiceClient songServiceClient;

    @BeforeEach
    void init() {
        resourceRepository.deleteAll();
    }

    @Test
    void shouldUploadResource() throws Exception {
        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");

        var requestEntity = new HttpEntity<>(content, headers);

        var responseEntity = restTemplate.postForEntity(URL_PATH, requestEntity, UploadResourceResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var resourceUploadedResponse = responseEntity.getBody();
        assertNotNull(resourceUploadedResponse);
        assertNotNull(resourceUploadedResponse.id());

        var foundResourceEntity = resourceRepository.findById(resourceUploadedResponse.id());
        assertTrue(foundResourceEntity.isPresent());

        var actualResourceEntity = foundResourceEntity.get();
        assertEquals(resourceUploadedResponse.id(), actualResourceEntity.getId());
    }

    @Test
    void shouldGetResource() throws IOException {
        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");

        var requestEntity = new HttpEntity<>(content, headers);

        var responseEntity = restTemplate.postForEntity(URL_PATH, requestEntity, UploadResourceResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var resourceUploadedResponse = responseEntity.getBody();
        assertNotNull(resourceUploadedResponse);
        assertNotNull(resourceUploadedResponse.id());

        var responseEntity2 = restTemplate.getForEntity(URL_PATH + "/" + resourceUploadedResponse.id(), byte[].class);
        assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        assertEquals("audio/mpeg", responseEntity2.getHeaders().getContentType().toString());

        assertNotNull(responseEntity2.getBody());
        assertArrayEquals(content, responseEntity2.getBody());
    }

    @Test
    void shouldDeleteResource() throws IOException {


        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");

        var requestEntity = new HttpEntity<>(content, headers);

        var responseEntity = restTemplate.postForEntity(URL_PATH, requestEntity, UploadResourceResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var resourceUploadedResponse = responseEntity.getBody();
        assertNotNull(resourceUploadedResponse);
        assertNotNull(resourceUploadedResponse.id());

//
//        doNothing().when(songServiceClient).deleteSong(resourceUploadedResponse.id());

        var headers2 = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        var responseEntity2 = restTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_PATH).queryParam("id", resourceUploadedResponse.id()).build().toUri(),
            HttpMethod.DELETE,
            null/*new HttpEntity<>(headers2)*/,
            DeleteResourcesResponse.class
        );
        assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity2.getHeaders().getContentType());

        var resourceDeletedResponse = responseEntity2.getBody();
        assertNotNull(resourceDeletedResponse);
        assertNotNull(resourceDeletedResponse.ids());
        assertEquals(1, resourceDeletedResponse.ids().size());
//
//        verify(songServiceClient).deleteSong(resourceUploadedResponse.id());
//        verifyNoMoreInteractions(songServiceClient);

        var foundResourceEntities = resourceRepository.findAllById(resourceDeletedResponse.ids());
        assertNotNull(foundResourceEntities);
        assertTrue(foundResourceEntities.isEmpty());
    }
}
