package com.example.resourceservice.service;

import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.exception.InvalidMp3FileException;
import com.example.resourceservice.exception.ResourceNotFoundException;
import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.validation.CsvIdsParser;
import com.example.resourceservice.service.validation.CsvIdsValidator;
import com.example.resourceservice.service.validation.IdValidator;
import com.example.resourceservice.service.validation.Mp3Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private SongServiceClient songServiceClient;
    @Autowired
    private Mp3Validator mp3Validator;
    @Autowired
    private IdValidator idValidator;
    @Autowired
    private CsvIdsValidator csvIdsValidator;
    @Autowired
    private CsvIdsParser csvIdsParser;

    @Transactional
    public Long uploadResource(byte[] audio) {
        if (!mp3Validator.valid(audio)) {
            throw new InvalidMp3FileException("The request body is invalid MP3");
        }

        var resource = new Resource();
        resource.setAudio(audio);

        var createdResource = resourceRepository.save(resource);
        var createdId = createdResource.getId();

        try {
            var createSongDto = metadataService.extractSongMetadata(audio, createdId);
            songServiceClient.createSong(createSongDto);
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("Failed to save song metadata for ID=%d", createdId));
        }

        return createdId;
    }

    @Transactional(readOnly = true)
    public Resource getResource(Long id) {
        idValidator.validate(id);

        return resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public List<Long> deleteResources(String csvIds) {
        csvIdsValidator.validate(csvIds);

        return csvIdsParser.parse(csvIds)
            .stream()
            .filter(resourceRepository::existsById)
            .peek(id -> resourceRepository.deleteById(id))
            .peek(id -> {
                try {
                    songServiceClient.deleteSong(id);
                } catch (RuntimeException e) {
                    throw new RuntimeException(String.format("Failed to delete song metadata for ID=%d", id));
                }
            })
            .collect(Collectors.toList());
    }
}
