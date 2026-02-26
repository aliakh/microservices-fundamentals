package com.example.songservice.controller;

import com.example.songservice.dto.CreateSongRequest;
import com.example.songservice.dto.CreateSongResponse;
import com.example.songservice.dto.DeleteSongsResponse;
import com.example.songservice.dto.SongDto;
import com.example.songservice.service.SongService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
@Validated
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);

    @Autowired
    private SongService songService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateSongResponse> createSong(@RequestBody @Valid CreateSongRequest createSongRequest) {
        logger.info("Create song: {}", createSongRequest);
        var createdId = songService.createSong(createSongRequest);
        return ResponseEntity.ok(new CreateSongResponse(createdId));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<SongDto> getSongById(@PathVariable Long id) {
        logger.info("Get song by id: {}", id);
        var songDto = songService.getSongById(id);
        return ResponseEntity.ok(songDto);
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<DeleteSongsResponse> deleteSongs(@RequestParam("id") String csvIds) {
        logger.info("Delete songs by ids: {}", csvIds);
        var deletedIds = songService.deleteSongs(csvIds);
        return ResponseEntity.ok(new DeleteSongsResponse(deletedIds));
    }
}
