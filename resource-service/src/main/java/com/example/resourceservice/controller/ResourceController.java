package com.example.resourceservice.controller;

import com.example.resourceservice.dto.DeleteResourcesResponse;
import com.example.resourceservice.dto.UploadResourceResponse;
import com.example.resourceservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping(consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<UploadResourceResponse> uploadResource(@RequestBody byte[] audio) {
        var createdId = resourceService.uploadResource(audio);
        return ResponseEntity.ok(new UploadResourceResponse(createdId));
    }

    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getResource(@PathVariable Long id) {
        var resource = resourceService.getResource(id);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentLength(resource.getAudio().length);

        return ResponseEntity.ok()
            .headers(headers)
            .body(resource.getAudio());
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<DeleteResourcesResponse> deleteResources(@RequestParam("id") String csvIds) {
        var deletedIds = resourceService.deleteResources(csvIds);
        return ResponseEntity.ok(new DeleteResourcesResponse(deletedIds));
    }
}
