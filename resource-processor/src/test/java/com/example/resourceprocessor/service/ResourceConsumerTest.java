package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.CreateSongDto;
import com.example.resourceprocessor.dto.CreateSongResponse;
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
    void shouldConsumeResource() throws Exception {
        var message = "{\"id\": 1, \"key\": \"74bcaf90-df4f-4e55-bb63-5d84961c2f5a\"}";
        var resourceDto = new ResourceDto(1L,"74bcaf90-df4f-4e55-bb63-5d84961c2f5a");
        var audio = new byte[]{0};
//        var songDto = mock(SongDto.class); // Replace with your actual SongDto
        var createSongDto = new CreateSongDto(
            1L,
            "The song",
            "John Doe",
            "Songs",
            "12:34",
            "2020"
        );

        when(objectMapper.readValue(message, ResourceDto.class)).thenReturn(resourceDto);
        when(resourceServiceClient.getResource(1L)).thenReturn(audio);
        when(metadataService.extractSongMetadata(audio, 1L)).thenReturn(createSongDto);
        when(songServiceClient.createSong(createSongDto)).thenReturn(new CreateSongResponse(1L));

        resourceConsumer.consumeResource(message);

        verify(objectMapper).readValue(message, ResourceDto.class);
        verify(resourceServiceClient).getResource(1L);
        verify(metadataService).extractSongMetadata(audio, 1L);
        verify(songServiceClient).createSong(createSongDto);
    }
}
