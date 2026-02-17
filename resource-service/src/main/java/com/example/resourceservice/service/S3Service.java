package com.example.resourceservice.service;

import com.example.resourceservice.dto.S3ResourceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private S3Client s3Client;

    public S3ResourceDto putObject(byte[] audio, String bucket, String key, String contentType) {
        logger.info("Put object into bucket {}", bucket);

        createBucketIfDoesNotExist(bucket);

        var putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .contentLength((long) audio.length)
            .build();

        var putObjectResponse = s3Client.putObject(
            putObjectRequest,
            RequestBody.fromBytes(audio)
        );
        logger.info("Put object response: {}", putObjectResponse);

        return new S3ResourceDto(
            bucket,
            key
        );
    }

    public byte[] getObject(String bucket, String key) {
        logger.info("Get object from bucket {} by key {}", bucket, key);

        var getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        var getObjectResponse = s3Client.getObjectAsBytes(getObjectRequest);
        logger.info("Get object response: {}", getObjectResponse);

        return getObjectResponse.asByteArray();
    }

    public void copyObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        logger.info("Copy object from bucket {} to bucket {} by key {}", sourceBucket, destinationBucket, key);

        createBucketIfDoesNotExist(destinationBucket);

        var copyObjectRequest = CopyObjectRequest.builder()
            .sourceBucket(sourceBucket)
            .sourceKey(sourceKey)
            .destinationBucket(destinationBucket)
            .destinationKey(destinationKey)
            .build();

        var copyObjectResponse = s3Client.copyObject(copyObjectRequest);
        logger.info("Copy object response: {}", copyObjectResponse);
    }

    public void deleteObject(String bucket, String key) {
        logger.info("Delete object from bucket {} by key {}", bucket, key);

        var deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        var deleteObjectResponse = s3Client.deleteObject(deleteObjectRequest);
        logger.info("Delete object response: {}", deleteObjectResponse);
    }

    void createBucketIfDoesNotExist(String bucket) {
        logger.info("Create bucket {} if doesn't exist", bucket);

        if (isBucketExist(bucket)) {
            logger.info("Bucket {} already exists", bucket);
            return;
        }

        var createBucketRequest = CreateBucketRequest.builder()
            .bucket(bucket)
            .build();

        var createBucketResponse = s3Client.createBucket(createBucketRequest);
        logger.info("Create bucket response: {}", createBucketResponse);
    }

    private boolean isBucketExist(String bucket) {
        var headBucketRequest = HeadBucketRequest.builder()
            .bucket(bucket)
            .build();
        try {
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }
}
