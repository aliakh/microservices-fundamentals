package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.KafkaProperties;
import com.example.resourceprocessor.tracing.TraceConstants;
import com.example.resourceprocessor.tracing.TraceContext;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ResourceFinalizingProducer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceFinalizingProducer.class);

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;
    @Autowired
    private KafkaProperties kafkaProperties;

    public void finalizeResource(Long resourceId) {
        var topic = kafkaProperties.finalizingResourcesTopic();
        var key = resourceId;
        var value = resourceId.toString();

        var producerRecord = new ProducerRecord<>(topic, key, value);
        var traceId = TraceContext.getOrCreateTraceId();
        producerRecord.headers().add(new RecordHeader(TraceConstants.TRACE_ID_HEADER, traceId.getBytes(StandardCharsets.UTF_8)));

        kafkaTemplate.send(producerRecord)
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
