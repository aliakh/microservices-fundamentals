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
public class ResourceConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceServiceClient resourceServiceClient;
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private SongServiceClient songServiceClient;
    @Autowired
    private ResourceProducer2 resourceProducer2;

    @Transactional
    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group-id}")
    public void consumeResource(String message) {
        try {
            logger.info("Message received: {}", message);

            var resourceDto = objectMapper.readValue(message, ResourceDto.class);
            logger.info("Resource deserialized: {}", resourceDto);

            var audio = resourceServiceClient.getResource(resourceDto.id());
            logger.info("Get resource response: {} byte(s)", audio.length);

            var songDto = metadataService.extractSongMetadata(audio, resourceDto.id());
            logger.info("Song metadata extracted: {}", songDto);

            var songCreatedResponse = songServiceClient.createSong(songDto);
            logger.info("Create song response: {}", songCreatedResponse);

            resourceProducer2.completeResource(resourceDto.id());
//            logger.info("Create song response: {}", songCreatedResponse);
        } catch (JsonProcessingException e) {
            logger.error("Error while deserializing resource {} from JSON", message, e);
        } catch (RuntimeException e) {
            logger.error("Failed to consume message {}", message, e);
        }
    }
}
