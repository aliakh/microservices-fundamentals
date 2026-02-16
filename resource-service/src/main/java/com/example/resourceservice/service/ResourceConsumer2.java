package com.example.resourceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceConsumer2 {

    private static final Logger logger = LoggerFactory.getLogger(ResourceConsumer2.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceService resourceService;
//    @Autowired
//    private ResourceServiceClient resourceServiceClient;
//    @Autowired
//    private MetadataService metadataService;
    @Autowired
    private SongServiceClient songServiceClient;

    @Transactional
    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group-id}")
    public void consumeResource(Long resourceId) {
        try {
            logger.info("Message received: {}", resourceId);

            var xxx = resourceService.completeResourceUpload(resourceId);
            logger.info("Resource deserialized: {}", xxx);

//            var resourceDto = objectMapper.readValue(message, ResourceDto.class);
//            logger.info("Resource deserialized: {}", resourceDto);
//
//            var audio = resourceServiceClient.getResource(resourceDto.id());
//            logger.info("Get resource response: {} byte(s)", audio.length);
//
//            var songDto = metadataService.extractSongMetadata(audio, resourceDto.id());
//            logger.info("Song metadata extracted: {}", songDto);
//
//            var songCreatedResponse = songServiceClient.createSong(songDto);
//            logger.info("Create song response: {}", songCreatedResponse);
//        } catch (JsonProcessingException e) {
//            logger.error("Error while deserializing resource {} from JSON", message, e);
        } catch (RuntimeException e) {
//            logger.error("Failed to consume message {}", message, e);
        }
    }
}
