package com.example.resourceprocessor.service;

import com.example.resourceprocessor.exception.ExtractMetadataException;
import com.example.resourceprocessor.model.SongDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

import static com.example.resourceprocessor.TestUtil.getFileAsByteArrayResource;
import static com.example.resourceprocessor.TestUtil.readValueFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SongMetadataServiceTest {
    private static final String TEST_FILE = "test.mp3";
    private static final String EXPECTED_METADATA_FILE = "expected_metadata.json";
    private static final String BAD_TEST_FILE = "bad.txt";

    private SongMetadataService metadataService;

    @BeforeEach
    public void initMetadataService() {
        metadataService = new SongMetadataService();
    }

    @Test
    void extractMetadata_shouldReturnExpectedMetadata_whenValidResource() throws IOException {
        //given
        ByteArrayResource resource = getFileAsByteArrayResource(TEST_FILE);
        SongDataBuilder expectedMetadata = readValueFromFile(EXPECTED_METADATA_FILE, SongDataBuilder.class);

        //when
        SongDataBuilder result = metadataService.extractMetadata(resource);

        //then
        assertNotNull(result);
        assertEquals(expectedMetadata, result);
    }

    @Test
    void extractMetadata_shouldThrowException_whenResourceIsNull() {
        //given
        //when
        //then
        assertThrows(ExtractMetadataException.class, () ->
                metadataService.extractMetadata(null));
    }

    @Test
    void extractMetadata_shouldThrowException_whenResourceIsEmpty() {
        //given
        ByteArrayResource resource = new ByteArrayResource(new byte[]{});

        //when
        //then
        assertThrows(ExtractMetadataException.class, () ->
                metadataService.extractMetadata(resource));
    }

    @Test
    void extractMetadata_shouldThrowException_whenResourceIsBad() throws IOException {
        //given
        ByteArrayResource resource = getFileAsByteArrayResource(BAD_TEST_FILE);

        //when
        //then
        assertThrows(ExtractMetadataException.class, () ->
                metadataService.extractMetadata(resource));
    }
}
