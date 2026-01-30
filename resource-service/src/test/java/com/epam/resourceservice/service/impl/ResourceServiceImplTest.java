package com.epam.resourceservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.model.Resource;
import com.epam.resourceservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.apache.http.entity.ContentType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {


    private ResourceServiceImpl resourceService;

    @Mock
    private ResourceRepository repository;
    @Mock
    private KafkaTemplate<Long, String> kafkaTemplate;

    @Mock
    private PutObjectResult putObjectResult;

    @Mock
    private S3Object s3Object;

    @Mock
    private AmazonS3 s3Client;

    @BeforeEach
    public void initService() {
        MockitoAnnotations.initMocks(this);
        resourceService = new ResourceServiceImpl(repository, kafkaTemplate, s3Client);
    }

    @Test
    void createResource_shouldSendEventAndReturnId_whenCreateNewResource() throws IOException, NoSuchFieldException, IllegalAccessException {
        //given
        Long id = 1L;
        String topic = "resources";
        String originalName = "test-original-name";
        MultipartFile data = new MockMultipartFile("test", originalName,
                MULTIPART_FORM_DATA.getMimeType(), new byte[]{1});
        Resource resource = new Resource();
        resource.setData(data.getBytes());
        resource.setFileName("name");
        doReturn(resource.withId(id)).when(repository).save(any(Resource.class));
        doReturn(putObjectResult).when(s3Client).putObject(anyString(), anyString(), any(), any());
        Field field = ResourceServiceImpl.class.getDeclaredField("topic");
        field.setAccessible(true);
        field.set(resourceService, topic);
        //when
        Long result = resourceService.addResource(data);

        //then
        Mockito.verify(kafkaTemplate, Mockito.times(1))
                .send(topic, id.toString());
        assertEquals(id, result);
    }
}