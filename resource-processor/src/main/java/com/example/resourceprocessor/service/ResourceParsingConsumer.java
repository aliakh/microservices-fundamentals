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
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

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
    private ResourceFinalizingProducer resourceFinalizingProducer;
    @Autowired
    private Tracer tracer;
    @Value("${app.tracing.header:X-Trace-Id}")
    private String traceHeader;

    @Transactional
    @KafkaListener(topics = "${kafka.parsing-resources-topic}", groupId = "${kafka.parsing-resources-consumer-group}")
    public void parseResource(String message,
                              @Header(name = "X-Trace-Id", required = false) String messageTraceId) {

        // Start a new span and bind MDC with the propagated trace id if present
        Span span = tracer.nextSpan().name("kafka:consume:files.process").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
            if (messageTraceId != null && !messageTraceId.isBlank()) {
                MDC.put("traceId", messageTraceId);
            }
            String traceId = messageTraceId != null ? messageTraceId : span.context().traceId();
            log.info("Consumed Kafka message, key={}, payload='{}', traceId={}", key, payload,
                traceId);
        } finally {
            span.end();
            MDC.remove("traceId");
        }

        try {
            logger.info("Resource parsing message received: {}, traceId={}", message, traceId);

            var resourceDto = objectMapper.readValue(message, ResourceDto.class);
            logger.info("Resource deserialized: {}", resourceDto);

            var audio = resourceServiceClient.getResource(resourceDto.id());
            logger.info("Get resource response: {} byte(s)", audio.length);

            var songDto = metadataService.extractSongMetadata(audio, resourceDto.id());
            logger.info("Song metadata extracted: {}", songDto);

            var songCreatedResponse = songServiceClient.createSong(songDto);
            logger.info("Create song response: {}", songCreatedResponse);

            resourceFinalizingProducer.finalizeResource(resourceDto.id());
            logger.info("Sent resource finalizing message");
        } catch (JsonProcessingException e) {
            logger.error("Error while deserializing resource {} from JSON", message, e);
        } catch (RuntimeException e) {
            logger.error("Failed to parse message {}", message, e);
        }
    }
}
