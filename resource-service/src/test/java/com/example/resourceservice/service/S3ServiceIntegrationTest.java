package com.example.resourceservice.service;

import com.example.resourceservice.AbstractIntegrationTest;
import com.example.resourceservice.Uuid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class S3ServiceIntegrationTest extends AbstractIntegrationTest {

    private static final String BUCKET = "resources";
    private static final String KEY = "45453da8-e24f-4eea-86bf-8ca651a54bc6";
    private static final String FILE_PATH = "/audio/audio1.mp3";
    private static final String FILE_NAME = "audio1.mp3";

    @Autowired
    private S3Service s3Service;
    @Autowired
    private S3Client s3Client;

    @Test //TODO
    void shouldCreateBucket() {
        s3Service.createBucketIfDoesNotExist(BUCKET);

        var headBucketRequest = HeadBucketRequest.builder()
            .bucket(BUCKET)
            .build();
        var headBucketResponse = s3Client.headBucket(headBucketRequest);

        assertEquals(200, headBucketResponse.sdkHttpResponse().statusCode());
    }

    @Test //TODO
    void shouldDoNotCreateBucket() {
        s3Service.createBucketIfDoesNotExist(BUCKET);

        var headBucketRequest = HeadBucketRequest.builder()
            .bucket(BUCKET)
            .build();
        var headBucketResponse = s3Client.headBucket(headBucketRequest);

        assertEquals(200, headBucketResponse.sdkHttpResponse().statusCode());

        s3Service.createBucketIfDoesNotExist(BUCKET);
    }

    @Test //TODO
    void shouldPutObject() throws IOException {
        var audio = new ClassPathResource(FILE_PATH).getInputStream().readAllBytes();

        var uploadedFileMetadata = s3Service.putObject(audio, BUCKET, "audio/mpeg");

        assertEquals(BUCKET, uploadedFileMetadata.bucket());
        assertTrue(Uuid.isValid(uploadedFileMetadata.key()));
    }

    @Test //TODO
    void shouldDeleteFile() throws IOException {
        var audio = new byte[]{0};

        var putObjectRequest = PutObjectRequest.builder()
            .bucket(BUCKET)
            .key(KEY)
            .contentType("audio/mpeg")
            .contentLength((long)audio.length)
            .build();

        var putObjectResponse = s3Client.putObject(
            putObjectRequest,
            RequestBody.fromBytes(audio)
        );
        assertEquals(200, putObjectResponse.sdkHttpResponse().statusCode());

        s3Service.deleteObject(BUCKET, KEY);
    }
}
