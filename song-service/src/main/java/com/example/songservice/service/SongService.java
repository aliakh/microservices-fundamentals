package com.example.songservice.service;

import com.example.songservice.dto.CreateSongRequest;
import com.example.songservice.dto.SongDto;
import com.example.songservice.entity.Song;
import com.example.songservice.exception.SongAlreadyExistsException;
import com.example.songservice.exception.SongNotFoundException;
import com.example.songservice.repository.SongRepository;
import com.example.songservice.service.validation.CsvIdsParser;
import com.example.songservice.service.validation.CsvIdsValidator;
import com.example.songservice.service.validation.IdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private IdValidator idValidator;
    @Autowired
    private CsvIdsValidator csvIdsValidator;
    @Autowired
    private CsvIdsParser csvIdsParser;

    @Transactional
    public Long createSong(CreateSongRequest createSongRequest) {
        if (songRepository.existsById(createSongRequest.id())) {
            throw new SongAlreadyExistsException(createSongRequest.id());
        }

        var song = new Song(
            createSongRequest.id(),
            createSongRequest.name(),
            createSongRequest.artist(),
            createSongRequest.album(),
            createSongRequest.duration(),
            createSongRequest.year()
        );

        var createdSong = songRepository.save(song);
        return createdSong.getId();
    }

    @Transactional(readOnly = true)
    public SongDto getSongById(Long id) {
        idValidator.validate(id);

        var song = songRepository.findById(id)
            .orElseThrow(() -> new SongNotFoundException(id));

        return new SongDto(
            song.getId(),
            song.getName(),
            song.getArtist(),
            song.getAlbum(),
            song.getDuration(),
            song.getYear()
        );
    }

    @Transactional
    public List<Long> deleteSongs(String csvIds) {
        csvIdsValidator.validate(csvIds);

        return csvIdsParser.parse(csvIds)
            .stream()
            .filter(songRepository::existsById)
            .peek(songRepository::deleteById)
            .collect(Collectors.toList());
    }
}
