package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.ResourceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceConsumerTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ResourceServiceClient resourceServiceClient;
    @Mock
    private MetadataService metadataService;
    @Mock
    private SongServiceClient songServiceClient;

    @InjectMocks
    private ResourceConsumer resourceConsumer;

    @Test
    void consumeResource_Success() throws Exception {
        // Arrange
        String message = "{\"id\": 1}";
        ResourceDto dto = new ResourceDto(1L);
        byte[] audioData = new byte[]{1, 2, 3};
        var songDto = mock(SongDto.class); // Replace with your actual SongDto

        when(objectMapper.readValue(message, ResourceDto.class)).thenReturn(dto);
        when(resourceServiceClient.getResource(1L)).thenReturn(audioData);
        when(metadataService.extractSongMetadata(audioData, 1L)).thenReturn(songDto);

        // Act
        resourceConsumer.consumeResource(message);

        // Assert
        verify(songServiceClient, times(1)).createSong(songDto);
    }
}
