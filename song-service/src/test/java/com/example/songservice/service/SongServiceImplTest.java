package com.example.songservice.service;


import com.example.songservice.model.Song;
import com.example.songservice.repository.SongRepository;
import com.example.songservice.service.impl.SongServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static com.example.songservice.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {

    @Mock
    private SongRepository repository;

    private SongService service;

    @BeforeEach
    public void initService() {
        MockitoAnnotations.initMocks(this);
        service = new SongServiceImpl(repository);
    }

    @Test
    void create_shouldSaveAndReturnId_whenCreateMetadata() {
        //given
        Long id = 10L;
        String resourceId = "1";
        Song metadata = getDefaultSongMetadata(resourceId);

        Song song = getDefaultSong(resourceId);

        doReturn(song.withId(id)).when(repository).save(any(Song.class));

        //when
        Long result = service.addSong(metadata);

        //then
        assertEquals(id, result);
    }

    @Test
    void getSongMetadataById_shouldReturnSong_whenSongExists() {
        //given
        Long id = 1L;
        String resourceId = "10";
        Song song = getDefaultSongWithId(id, resourceId);
        doReturn(Optional.of(song)).when(repository).findById(id);
        //when
        Song result = service.getSongById(id);

        //then
        assertNotNull(result);
        assertEquals(result.getId(), id);
        assertEquals(result.getResourceId(), resourceId);
    }
}
