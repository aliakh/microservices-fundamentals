package com.example.resourceservice.service;

import com.example.resourceservice.dto.S3Properties;
import com.example.resourceservice.dto.S3ResourceDto;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.exception.InvalidMp3FileException;
import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.validation.CsvIdsParser;
import com.example.resourceservice.service.validation.CsvIdsValidator;
import com.example.resourceservice.service.validation.IdValidator;
import com.example.resourceservice.service.validation.Mp3Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    private static final long ID = 1L;
    private static final String BUCKET = "resources";
    private static final String KEY = "45453da8-e24f-4eea-86bf-8ca651a54bc6";
    private static final String FILE_NAME = "audio.mp3";
    private static final byte[] FILE_CONTENT = new byte[]{0};
    private static final long FILE_SIZE = FILE_CONTENT.length;

    @InjectMocks
    private ResourceService resourceService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceProducer resourceProducer;
    @Mock
    private SongServiceClient songServiceClient;
    @Mock
    private S3Service s3Service;
    @Mock
    private Mp3Validator mp3Validator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(resourceService, "s3Properties", new S3Properties(null, null, BUCKET, null, null));
        ReflectionTestUtils.setField(resourceService, "idValidator", new IdValidator());
        ReflectionTestUtils.setField(resourceService, "csvIdsValidator", new CsvIdsValidator());
        ReflectionTestUtils.setField(resourceService, "csvIdsParser", new CsvIdsParser());
    }

    @Test //TODO
    void shouldFailWhenUploadingWrongContentType() {
        var audio = new byte[]{0};
        when(mp3Validator.valid(audio)).thenReturn(false);

        assertThrows(InvalidMp3FileException.class, () -> resourceService.uploadResource(audio));

        verifyNoMoreInteractions(mp3Validator);
        verifyNoInteractions(resourceRepository, resourceProducer, songServiceClient, s3Service);
    }

    @Test //TODO
    void shouldUploadResource() {
        var audio = new byte[]{0};
        when(mp3Validator.valid(audio)).thenReturn(true);
        var s3ResourceDto = new S3ResourceDto(BUCKET, KEY);
        when(s3Service.putObject(audio, BUCKET, "audio/mpeg")).thenReturn(s3ResourceDto);

        var resource = new Resource();
        resource.setKey(KEY);

        var savedResource = new Resource();
        savedResource.setId(ID);
        savedResource.setKey(KEY);
        when(resourceRepository.save(resource)).thenReturn(savedResource);

        var uploadedResponseId = resourceService.uploadResource(audio);
        assertEquals(savedResource.getId(), uploadedResponseId);

        verify(mp3Validator).valid(audio);
        verify(s3Service).putObject(audio, BUCKET, "audio/mpeg");
        verify(resourceRepository).save(resource);
        verify(resourceProducer).produceResource(savedResource);
        verifyNoMoreInteractions(resourceRepository, resourceProducer,  s3Service, mp3Validator);
        verifyNoInteractions(songServiceClient);
    }

    @Test //TODO
    void shouldGetResource() {
        var resource1 = new Resource();
        resource1.setId(ID);
        resource1.setKey(KEY);
        var resource = resource1;
        var id = resource.getId();
        when(resourceRepository.findById(id)).thenReturn(Optional.of(resource));
        var audio = new byte[]{0};
        when(s3Service.getObject(BUCKET, resource.getKey())).thenReturn(audio);

        var resourceResponse = resourceService.getResource(id);

        assertEquals(id, resourceResponse.id());
        assertEquals(audio, resourceResponse.audio());
        verify(resourceRepository).findById(id);
        verify(s3Service).getObject(BUCKET, resource.getKey());
        verifyNoMoreInteractions(resourceRepository, s3Service);
        verifyNoInteractions(resourceProducer, songServiceClient,mp3Validator);
    }

    @Test //TODO
    void shouldDeleteResources() {
        var resource1 = new Resource();
        resource1.setId(ID);
        resource1.setKey(KEY);
        var resource = resource1;
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        doNothing().when(s3Service).deleteObject(BUCKET, resource.getKey());
        doNothing().when(resourceRepository).deleteById(resource.getId());
        doNothing().when(songServiceClient).deleteSong(resource.getId());

        var deletedResourceIds = resourceService.deleteResources(String.valueOf(resource.getId()));
        assertEquals(List.of(resource.getId()), deletedResourceIds);

        verify(resourceRepository).findById(resource.getId());
        verify(s3Service).deleteObject(BUCKET, resource.getKey());
        verify(resourceRepository).deleteById(resource.getId());
        verify(songServiceClient).deleteSong(resource.getId());
        verifyNoMoreInteractions(resourceRepository, songServiceClient, s3Service);
        verifyNoInteractions(resourceProducer,mp3Validator);
    }

}
