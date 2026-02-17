package com.example.resourceservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceFinalizationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceFinalizationConsumer.class);

    @Autowired
    private ResourceService resourceService;

    @Transactional
    @KafkaListener(topics = "${kafka.parsing-resources-topic}", groupId = "${kafka.parsing-resources-consumer-group-id}")
    public void finalizeResource(Long resourceId) {
        try {
            logger.info("Resource finalization message received: {}", resourceId);

            var resource = resourceService.moveResourceToPermanentStorage(resourceId);
            logger.info("Resource moved to permanent storage: {}", resource);
        } catch (RuntimeException e) {
            logger.error("Failed to finalize resource by id={}", resourceId, e);
        }
    }
}
