package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.ResourceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceParsingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceParsingConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceServiceClient resourceServiceClient;
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private SongServiceClient songServiceClient;
    @Autowired
    private ResourceFinalizationProducer resourceFinalizationProducer;

    @Transactional
    @KafkaListener(topics = "${kafka.parsing-resources-topic}", groupId = "${kafka.parsing-resources-consumer-group}")
    public void parseResource(String message) {
        try {
            logger.info("Resource parsing message received: {}", message);

            var resourceDto = objectMapper.readValue(message, ResourceDto.class);
            logger.info("Resource deserialized: {}", resourceDto);

            var audio = resourceServiceClient.getResource(resourceDto.id());
            logger.info("Get resource response: {} byte(s)", audio.length);

            var songDto = metadataService.extractSongMetadata(audio, resourceDto.id());
            logger.info("Song metadata extracted: {}", songDto);

            var songCreatedResponse = songServiceClient.createSong(songDto);
            logger.info("Create song response: {}", songCreatedResponse);

            resourceFinalizationProducer.finalizeResource(resourceDto.id());
            logger.info("Sent resource finalization message");
        } catch (JsonProcessingException e) {
            logger.error("Error while deserializing resource {} from JSON", message, e);
        } catch (RuntimeException e) {
            logger.error("Failed to parse message {}", message, e);
        }
    }
}
