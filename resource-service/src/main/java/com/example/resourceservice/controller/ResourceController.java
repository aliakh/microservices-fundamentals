package com.example.resourceservice.controller;

import com.example.resourceservice.dto.DeleteResourcesResponse;
import com.example.resourceservice.dto.UploadResourceResponse;
import com.example.resourceservice.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.tracing.Tracer;

import java.util.UUID;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private Tracer tracer;

    @PostMapping(consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<UploadResourceResponse> uploadResource(@RequestBody byte[] audio,
                                                                 @RequestHeader(value = "X-Trace-Id", required = false) String requestTraceId) {
        var span = tracer.currentSpan();
        var traceId = (span != null) ? span.context().traceId() : (requestTraceId != null ? requestTraceId : "resource-service:controller:upload-resource:" + UUID.randomUUID());
        logger.info("Upload resource: {}, traceId={}", audio, traceId);

        var createdId = resourceService.uploadResource(audio, traceId);
        return ResponseEntity.ok(new UploadResourceResponse(createdId));
    }

    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getResource(@PathVariable Long id) {
        var span = tracer.currentSpan();
        var traceId = (span != null) ? span.context().traceId() : "resource-service:controller:get-resource:" + UUID.randomUUID();

        logger.info("Get resource by id: {}, traceId={}", id, traceId);
        var resourceResponse = resourceService.getResource(id);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentLength(resourceResponse.audio().length);

        return ResponseEntity.ok()
            .headers(headers)
            .body(resourceResponse.audio());
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<DeleteResourcesResponse> deleteResources(@RequestParam("id") String csvIds) {
        var span = tracer.currentSpan();
        var traceId = (span != null) ? span.context().traceId() : "resource-service:controller:delete-resource:" + UUID.randomUUID();

        var deletedIds = resourceService.deleteResources(csvIds);
        logger.info("Delete resources by ids: {}, traceId={}", csvIds, traceId);
        return ResponseEntity.ok(new DeleteResourcesResponse(deletedIds));
    }
}
