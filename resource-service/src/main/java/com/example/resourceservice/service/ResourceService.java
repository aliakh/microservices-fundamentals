package com.example.resourceservice.service;

import com.example.resourceservice.dto.ResourceResponse;
import com.example.resourceservice.dto.S3Properties;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceProducer resourceProducer;
    @Autowired
    private SongServiceClient songServiceClient;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private S3Properties s3Properties;
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

        var s3ResourceDto = s3Service.putObject(audio, s3Properties.bucket(), "audio/mpeg");

        var resource = new Resource();
        resource.setKey(s3ResourceDto.key());

        var createdResource = resourceRepository.save(resource);

        resourceProducer.produceResource(createdResource);

        return createdResource.getId();
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResource(Long id) {
        idValidator.validate(id);

        var resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));

        return new ResourceResponse(
            resource.getId(),
            s3Service.getObject(s3Properties.bucket(), resource.getKey())
        );
    }

    @Transactional
    public List<Long> deleteResources(String csvIds) {
        csvIdsValidator.validate(csvIds);

        return csvIdsParser.parse(csvIds)
            .stream()
            .map(resourceRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(resource -> {
                s3Service.deleteObject(s3Properties.bucket(), resource.getKey());
                return resource.getId();
            })
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
