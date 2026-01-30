package com.example.resourceservice.service;

//import com.example.resourceservice.config.properties.S3Properties;
//import com.example.resourceservice.dto.UploadedFileMetadata;
//import com.example.resourceservice.entity.Resource;
//import com.example.resourceservice.exception.BadRequestException;
import com.example.resourceservice.dto.S3Properties;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.validation.CsvIdsParser;
import com.example.resourceservice.service.validation.CsvIdsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
    private static final String KEY = "11111111-2222-3333-4444-555555555555";
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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(resourceService, "s3Properties", new S3Properties(null, null, BUCKET, null, null));
        ReflectionTestUtils.setField(resourceService, "csvIdsValidator", new CsvIdsValidator());
        ReflectionTestUtils.setField(resourceService, "csvIdsParser", new CsvIdsParser());
    }
/*
    @Test
    void shouldFailWhenUploadingNonAudioContentType() {
        when(multipartFile.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);

        assertThrows(BadRequestException.class, () -> resourceService.uploadResource(multipartFile));

        verify(multipartFile, times(2)).getContentType();
        verifyNoMoreInteractions(multipartFile);
        verifyNoInteractions(resourceRepository, s3Service, resourceProducer);
    }

    @Test
    void shouldUploadResource() {
        when(multipartFile.getContentType()).thenReturn(CONTENT_TYPE_AUDIO_MPEG);
        when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME);
        when(multipartFile.getSize()).thenReturn(FILE_SIZE);

        var uploadedFileMetadata = new UploadedFileMetadata(BUCKET, KEY);
        when(s3Service.uploadFile(multipartFile, BUCKET)).thenReturn(uploadedFileMetadata);

        var resourceEntity = new Resource();
        resourceEntity.setBucket(BUCKET);
        resourceEntity.setKey(KEY);
        resourceEntity.setName(FILE_NAME);
        resourceEntity.setSize(FILE_SIZE);

        var savedResourceEntry = new Resource();
        savedResourceEntry.setId(ID);
        savedResourceEntry.setBucket(BUCKET);
        savedResourceEntry.setKey(KEY);
        savedResourceEntry.setName(FILE_NAME);
        savedResourceEntry.setSize(FILE_SIZE);
        when(resourceRepository.save(resourceEntity)).thenReturn(savedResourceEntry);

        var resourceUploadedResponse = resourceService.uploadResource(multipartFile);

        assertEquals(savedResourceEntry.getId(), resourceUploadedResponse.id());

        verify(multipartFile).getContentType();
        verify(multipartFile).getOriginalFilename();
        verify(multipartFile).getSize();
        verify(s3Service).uploadFile(multipartFile, BUCKET);
        verify(resourceRepository).save(resourceEntity);
        verify(resourceProducer).publish(savedResourceEntry);
        verifyNoMoreInteractions(resourceRepository, s3Service, resourceProducer, multipartFile);
    }

    @Test
    void shouldGetResource() {
        Resource resourceEntity = getResourceEntity();
        var id = resourceEntity.getId();
        when(resourceRepository.findById(id)).thenReturn(Optional.of(resourceEntity));

        var actualResourceEntry = resourceService.getResource(id);

        assertEquals(id, actualResourceEntry.getId());
        assertEquals(resourceEntity.getBucket(), actualResourceEntry.getBucket());
        assertEquals(resourceEntity.getKey(), actualResourceEntry.getKey());
        assertEquals(resourceEntity.getName(), actualResourceEntry.getName());
        assertEquals(resourceEntity.getSize(), actualResourceEntry.getSize());

        verify(resourceRepository).findById(id);
        verifyNoMoreInteractions(resourceRepository);
        verifyNoInteractions(s3Service, resourceProducer, multipartFile);
    }

    @Test
    void shouldDownloadResource() {
        Resource resourceEntity = getResourceEntity();

        when(s3Service.downloadFile(resourceEntity.getBucket(), resourceEntity.getKey())).thenReturn(FILE_CONTENT);

        var actualContent = resourceService.downloadResource(resourceEntity);

        assertArrayEquals(FILE_CONTENT, actualContent);

        verify(s3Service).downloadFile(resourceEntity.getBucket(), resourceEntity.getKey());
        verifyNoMoreInteractions(s3Service);
        verifyNoInteractions(resourceRepository, resourceProducer, multipartFile);
    }
*/
    @Test
    void shouldDeleteResources() {
        var resource = buildResource();
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        doNothing().when(resourceRepository).deleteById(resource.getId());
        doNothing().when(songServiceClient).deleteSong(resource.getId());

        var deletedResourceIds = resourceService.deleteResources(String.valueOf(resource.getId()));
        assertEquals(List.of(resource.getId()), deletedResourceIds);

        verify(resourceRepository).findById(resource.getId());
        verify(s3Service).deleteObject(BUCKET, resource.getKey());
        verify(resourceRepository).deleteById(resource.getId());
        verify(songServiceClient).deleteSong(resource.getId());
        verifyNoMoreInteractions(resourceRepository,songServiceClient,s3Service);
        verifyNoInteractions(resourceProducer);
    }

    private Resource buildResource() {
        var resource = new Resource();
        resource.setId(ID);
        resource.setKey(KEY);
        return resource;
    }
}
