package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.KafkaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResourceFinalizingProducer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceFinalizingProducer.class);

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;
    @Autowired
    private KafkaProperties kafkaProperties;

    public void finalizeResource(Long resourceId) {
        kafkaTemplate.send(kafkaProperties.finalizingResourcesTopic(), resourceId, resourceId.toString())
            .whenComplete((result, throwable) -> {
                    if (throwable == null) {
                        logger.info("Resource finalizing message with key {} and value {} was published to topic {} at offset {}",
                            result.getProducerRecord().key(),
                            result.getProducerRecord().value(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()
                        );
                    } else {
                        logger.error("Failed to publish resource finalizing message {}", resourceId, throwable);
                    }
                }
            );
    }
}
