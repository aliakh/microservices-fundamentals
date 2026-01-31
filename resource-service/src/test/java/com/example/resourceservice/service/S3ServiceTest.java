package com.example.resourceservice.service;

import com.example.resourceservice.Uuid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    private static final String BUCKET = "resources";
    private static final String KEY = "45453da8-e24f-4eea-86bf-8ca651a54bc6";

    @InjectMocks
    private S3Service s3Service;
    @Mock
    private S3Client s3Client;

    @Test //TODO
    void shouldCreateBucket() {
        var headBucketRequest = HeadBucketRequest.builder()
            .bucket(BUCKET)
            .build();
        when(s3Client.headBucket(headBucketRequest)).thenThrow(NoSuchBucketException.builder().build());

        var createBucketRequest = CreateBucketRequest.builder()
            .bucket(BUCKET)
            .build();
        var createBucketResponse = CreateBucketResponse.builder().build();
        when(s3Client.createBucket(createBucketRequest)).thenReturn(createBucketResponse);

        s3Service.createBucketIfDoesNotExist(BUCKET);

        verify(s3Client).headBucket(headBucketRequest);
        verify(s3Client).createBucket(createBucketRequest);
        verifyNoMoreInteractions(s3Client);
    }

    @Test //TODO
    void shouldDoNotCreateBucket() {
        var headBucketRequest = HeadBucketRequest.builder()
            .bucket(BUCKET)
            .build();
        var headBucketResponse = HeadBucketResponse.builder().build();
        when(s3Client.headBucket(headBucketRequest)).thenReturn(headBucketResponse);

        s3Service.createBucketIfDoesNotExist(BUCKET);

        verify(s3Client).headBucket(headBucketRequest);
        verifyNoMoreInteractions(s3Client);
    }

    @Test //TODO
    void shouldUploadFileFolderDoesNotExist() throws IOException {
        var headBucketRequest = HeadBucketRequest.builder()
            .bucket(BUCKET)
            .build();
        when(s3Client.headBucket(headBucketRequest)).thenThrow(NoSuchBucketException.builder().build());

        var createBucketRequest = CreateBucketRequest.builder()
            .bucket(BUCKET)
            .build();
        var createBucketResponse = CreateBucketResponse.builder().build();
        when(s3Client.createBucket(createBucketRequest)).thenReturn(createBucketResponse);

        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        PutObjectResponse putObjectResponse = PutObjectResponse.builder().build();

        ArgumentCaptor<RequestBody> requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        when(s3Client.putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture())).thenReturn(putObjectResponse);

        var audio = new byte[]{0};

        var uploadedFileMetadata = s3Service.putObject(audio, BUCKET, "audio/mpeg");

        assertEquals(BUCKET, uploadedFileMetadata.bucket());
        assertTrue(Uuid.isValid(uploadedFileMetadata.key()));

        var putObjectRequest = putObjectRequestCaptor.getValue();
        assertEquals(BUCKET, putObjectRequest.bucket());
        assertTrue(Uuid.isValid(putObjectRequest.key()));
        assertEquals(uploadedFileMetadata.key(), putObjectRequest.key());

        RequestBody requestBody = requestBodyCaptor.getValue();
        assertArrayEquals(audio, requestBody.contentStreamProvider().newStream().readAllBytes());
        assertEquals(audio.length, requestBody.optionalContentLength().get());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, requestBody.contentType());

        verify(s3Client).headBucket(headBucketRequest);
        verify(s3Client).createBucket(createBucketRequest);
        verify(s3Client).putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture());
        verifyNoMoreInteractions(s3Client);
    }

    @Test //TODO
    void shouldUploadFileFolderExists() throws IOException {
        var headBucketRequest = HeadBucketRequest.builder()
            .bucket(BUCKET)
            .build();
        var headBucketResponse = HeadBucketResponse.builder().build();
        when(s3Client.headBucket(headBucketRequest)).thenReturn(headBucketResponse);

        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        PutObjectResponse putObjectResponse = PutObjectResponse.builder().build();

        ArgumentCaptor<RequestBody> requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        when(s3Client.putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture())).thenReturn(putObjectResponse);

        var audio = new byte[]{0};

        var uploadedFileMetadata = s3Service.putObject(audio, BUCKET, "audio/mpeg");

        assertEquals(BUCKET, uploadedFileMetadata.bucket());
        assertTrue(Uuid.isValid(uploadedFileMetadata.key()));

        var putObjectRequest = putObjectRequestCaptor.getValue();
        assertEquals(BUCKET, putObjectRequest.bucket());
        assertTrue(Uuid.isValid(putObjectRequest.key()));
        assertEquals(uploadedFileMetadata.key(), putObjectRequest.key());

        RequestBody requestBody = requestBodyCaptor.getValue();
        assertArrayEquals(audio, requestBody.contentStreamProvider().newStream().readAllBytes());
        assertEquals(audio.length, requestBody.optionalContentLength().get());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, requestBody.contentType());

        verify(s3Client).headBucket(headBucketRequest);
        verify(s3Client).putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture());
        verifyNoMoreInteractions(s3Client);
    }

    @Test //TODO
    void shouldDownloadFile() {
        var getObjectRequest = GetObjectRequest.builder()
            .bucket(BUCKET)
            .key(KEY)
            .build();
        var content = new byte[]{0};
        ResponseBytes<GetObjectResponse> getObjectResponse = ResponseBytes.fromByteArray(
            GetObjectResponse.builder().build(),
            content
        );
        when(s3Client.getObjectAsBytes(getObjectRequest)).thenReturn(getObjectResponse);

        assertArrayEquals(content, s3Service.getObject(BUCKET, KEY));

        verify(s3Client).getObjectAsBytes(getObjectRequest);
        verifyNoMoreInteractions(s3Client);
    }

    @Test //TODO
    void shouldDeleteFile() {
        var deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(BUCKET)
            .key(KEY)
            .build();
        var deleteObjectResponse = DeleteObjectResponse.builder().build();
        when(s3Client.deleteObject(deleteObjectRequest)).thenReturn(deleteObjectResponse);

        s3Service.deleteObject(BUCKET, KEY);

        verify(s3Client).deleteObject(deleteObjectRequest);
        verifyNoMoreInteractions(s3Client);
    }
}
