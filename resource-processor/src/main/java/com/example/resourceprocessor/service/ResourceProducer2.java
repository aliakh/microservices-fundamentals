package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.KafkaProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResourceProducer2 {

    private static final Logger logger = LoggerFactory.getLogger(ResourceProducer2.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;
    @Autowired
    private KafkaProperties kafkaProperties;

    public void completeResource(Long resourceId) {
        kafkaTemplate.send(kafkaProperties.topic(), resource.getId(), toJson(resource))
            .whenComplete((result, throwable) -> {
                    if (throwable == null) {
                        logger.info("Resource with key {} and value {} was published to topic {} at offset {}",
                            result.getProducerRecord().key(),
                            result.getProducerRecord().value(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()
                        );
                    } else {
                        logger.error("Failed to publish resource {}", resource, throwable);
                    }
                }
            );
    }

    private String toJson(Resource resource) {
        try {
            return objectMapper.writeValueAsString(resource);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing resource to JSON", e);
        }
    }
}
