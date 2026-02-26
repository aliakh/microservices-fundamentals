package com.example.resourceservice.service;

import com.example.resourceservice.dto.KafkaProperties;
import com.example.resourceservice.entity.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import java.nio.charset.StandardCharsets;

@Service
public class ResourceParsingProducer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceParsingProducer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;
    @Autowired
    private KafkaProperties kafkaProperties;
    @Value("${app.tracing.header:X-Trace-Id}")
    private String traceHeader;

    public void parseResource(Resource resource) {
        /*
        String payload = "processed:" + filename + ":" + size;
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, filename, payload);
        String toSendTrace = (traceId != null && !traceId.isBlank()) ? traceId : currentTraceId();
        if (toSendTrace != null) {
            record.headers().add(new RecordHeader(traceHeader, toSendTrace.getBytes(StandardCharsets.UTF_8)));
        }

         */

        kafkaTemplate.send(kafkaProperties.parsingResourcesTopic(), resource.getId(), toJson(resource))
            .whenComplete((result, throwable) -> {
                    if (throwable == null) {
                        logger.info("Resource parsing message with key {} and value {} was published to topic {} at offset {}",
                            result.getProducerRecord().key(),
                            result.getProducerRecord().value(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()
                        );
                    } else {
                        logger.error("Failed to publish resource parsing message {}", resource, throwable);
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
