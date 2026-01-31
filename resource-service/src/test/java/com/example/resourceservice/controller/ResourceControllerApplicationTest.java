package com.example.resourceservice.controller;

import com.example.resourceservice.AbstractIntegrationTest;
import com.example.resourceservice.dto.DeleteResourcesResponse;
import com.example.resourceservice.dto.UploadResourceResponse;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.S3Service;
import com.example.resourceservice.service.SongServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
//@TestPropertySource(properties = {
//    "spring.datasource.url=jdbc:tc:postgresql:17.0://localhost:5433/resource_db",
//    "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
//    "spring.jpa.hibernate.ddl-auto=create",
//    "spring.cloud.discovery.enabled=false"
//})
public class ResourceControllerApplicationTest extends AbstractIntegrationTest {

    private static final String URL_PATH = "/resources";
    private static final String FILE_PATH = "/audio/audio1.mp3";
    private static final String BUCKET = "resources";
    private static final String KEY = "11111111-2222-3333-4444-555555555555";
    private static final String FILE_NAME = "audio1.mp3";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private S3Service s3Service;
    @MockitoBean
    private SongServiceClient songServiceClient;

    @BeforeEach
    void init() {
        resourceRepository.deleteAll();
    }

    @Test
    void shouldUploadResource() throws Exception {
        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
// 2. Set headers for a binary stream
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

// 3. Create entity with byte[] directly
        var requestEntity = new HttpEntity<>(content, headers);

// 4. Execute the POST
        var responseEntity = testRestTemplate.postForEntity(URL_PATH, requestEntity, UploadResourceResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var resourceUploadedResponse = responseEntity.getBody();
        assertNotNull(resourceUploadedResponse);
        assertNotNull(resourceUploadedResponse.id());

        var foundResourceEntity = resourceRepository.findById(resourceUploadedResponse.id());
        assertTrue(foundResourceEntity.isPresent());

        var actualResourceEntity = foundResourceEntity.get();
        assertEquals(resourceUploadedResponse.id(), actualResourceEntity.getId());
//        assertEquals(BUCKET, actualResourceEntity.getBucket());
//        assertTrue(Pattern.matches(UUID_REGEXP, actualResourceEntity.getKey()));
//        assertEquals(FILE_NAME, actualResourceEntity.getName());
//        assertEquals(content.length, actualResourceEntity.getSize());
    }

//    private MultiValueMap<String, Object> getMultipartBody(byte[] content) {
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//
//        ContentDisposition contentDisposition = ContentDisposition
//            .builder("form-data")
//            .name("file")
//            .filename(FILE_NAME)
//            .build();
//
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
//        headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_AUDIO_MPEG);
//
//        HttpEntity<byte[]> fileEntity = new HttpEntity<>(content, headers);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", fileEntity);
//        return body;
//    }
//
//    private HttpHeaders getMultipartHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        return headers;
//    }
//
    @Test
    void shouldDownloadResource() throws IOException {
        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
// 2. Set headers for a binary stream
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

// 3. Create entity with byte[] directly
        var requestEntity = new HttpEntity<>(content, headers);

// 4. Execute the POST
        var responseEntity = testRestTemplate.postForEntity(URL_PATH, requestEntity, UploadResourceResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var resourceUploadedResponse = responseEntity.getBody();
        assertNotNull(resourceUploadedResponse);
        assertNotNull(resourceUploadedResponse.id());
//        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
//        var multipartFile = new MockMultipartFile(
//            "file",
//            FILE_NAME,
//            CONTENT_TYPE_AUDIO_MPEG,
//            content
//        );
//
//        var uploadedFileMetadata = s3Service.uploadFile(multipartFile, BUCKET);
//        assertEquals(BUCKET, uploadedFileMetadata.bucket());
//        assertTrue(Pattern.matches(UUID_REGEXP, uploadedFileMetadata.key()));
//
//        var resourceEntity = new Resource();
//        resourceEntity.setBucket(BUCKET);
//        resourceEntity.setKey(uploadedFileMetadata.key());
//        resourceEntity.setName(FILE_NAME);
//        resourceEntity.setSize((long) content.length);
//        var savedResourceEntity = resourceRepository.save(resourceEntity);

        var responseEntity2 = testRestTemplate.getForEntity(URL_PATH + "/" + resourceUploadedResponse.id(), byte[].class);
        assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        assertEquals("audio/mpeg", responseEntity2.getHeaders().getContentType().toString());

        assertNotNull(responseEntity2.getBody());
        assertArrayEquals(content, responseEntity2.getBody());
    }

    @Test
    void shouldDeleteResource() throws IOException {
//        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
//
//        var resourceEntity = new Resource();
////        resourceEntity.setBucket(BUCKET);
//        resourceEntity.setKey(KEY);
////        resourceEntity.setName(FILE_NAME);
////        resourceEntity.setSize((long) content.length);
//
//        var savedResourceEntity = resourceRepository.save(resourceEntity);


        var content = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();
// 2. Set headers for a binary stream
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

// 3. Create entity with byte[] directly
        var requestEntity = new HttpEntity<>(content, headers);

// 4. Execute the POST
        var responseEntity = testRestTemplate.postForEntity(URL_PATH, requestEntity, UploadResourceResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        var resourceUploadedResponse = responseEntity.getBody();
        assertNotNull(resourceUploadedResponse);
        assertNotNull(resourceUploadedResponse.id());

//        var foundResourceEntity = resourceRepository.findById(resourceUploadedResponse.id());

        doNothing().when(songServiceClient).deleteSong(resourceUploadedResponse.id());

        var headers2 = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        var responseEntity2 = testRestTemplate.exchange(
            UriComponentsBuilder.fromUriString(URL_PATH).queryParam("id", resourceUploadedResponse.id()).build().toUri(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers2),
            DeleteResourcesResponse.class
        );
        assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity2.getHeaders().getContentType());

        var resourceDeletedResponse = responseEntity2.getBody();
        assertNotNull(resourceDeletedResponse);
        assertNotNull(resourceDeletedResponse.ids());
        assertEquals(1, resourceDeletedResponse.ids().size());

        verify(songServiceClient).deleteSong(resourceUploadedResponse.id());
        verifyNoMoreInteractions(songServiceClient);

        var foundResourceEntities = resourceRepository.findAllById(resourceDeletedResponse.ids());
        assertNotNull(foundResourceEntities);
        assertTrue(foundResourceEntities.isEmpty());
    }
}
