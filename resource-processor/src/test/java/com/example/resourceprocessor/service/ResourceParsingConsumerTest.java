package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.CreateSongResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.resourceprocessor.Builders.buildCreateSongDto;
import static com.example.resourceprocessor.Builders.buildResourceDto;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceParsingConsumerTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private ResourceServiceClient resourceServiceClient;
    @Mock
    private MetadataService metadataService;
    @Mock
    private SongServiceClient songServiceClient;
    @InjectMocks
    private ResourceParsingConsumer resourceParsingConsumer;

    @Test
    void shouldParseResource() throws Exception {
        var resourceDto = buildResourceDto();
        var id = resourceDto.id();
        var message = objectMapper.writeValueAsString(resourceDto);
        var audio = new byte[]{0};
        var createSongDto = buildCreateSongDto(id);

        when(resourceServiceClient.getResource(id)).thenReturn(audio);
        when(metadataService.extractSongMetadata(audio, id)).thenReturn(createSongDto);
        when(songServiceClient.createSong(createSongDto)).thenReturn(new CreateSongResponse(id));

        resourceParsingConsumer.parseResource(message);

        verify(resourceServiceClient).getResource(id);
        verify(metadataService).extractSongMetadata(audio, id);
        verify(songServiceClient).createSong(createSongDto);
    }
}
