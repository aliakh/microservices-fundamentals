package com.epam.learning.microservices.e2e.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUrlResolver {

    private final DiscoveryClient discoveryClient;

    public String getResourceServiceUrl() {
        return getServiceUrl("resource-service");
    }

    public String getSongServiceUrl() {
        return getServiceUrl("song-service");
    }

    private String getServiceUrl(String serviceName) {
        // Wait for Eureka client to initialize and fetch registry
        waitForEurekaClientReady();

        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            throw new RuntimeException("No instances found for service: " + serviceName +
                ". Make sure the service is running and registered with Eureka.");
        }

        ServiceInstance instance = instances.get(0);
        String url = String.format("http://%s:%d", instance.getHost(), instance.getPort());
        log.info("Discovered {} at: {}", serviceName, url);
        return url;
    }

    private void waitForEurekaClientReady() {
        log.info("Waiting for Eureka client to initialize...");
        for (int i = 0; i < 10; i++) {
            try {
                // Try to access discovery client to see if it's ready
                discoveryClient.getServices();
                log.info("Eureka client is ready");
                return;
            } catch (Exception e) {
                log.debug("Eureka client not ready yet, attempt {}/10: {}", i + 1, e.getMessage());
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for Eureka client", ie);
                }
            }
        }
        throw new RuntimeException("Eureka client failed to initialize after 20 seconds");
    }

    public void logServiceConfiguration() {
        log.info("=== E2E Test Service Configuration ===");
        log.info("Resource Service URL: {}", getResourceServiceUrl());
        log.info("Song Service URL: {}", getSongServiceUrl());
        log.info("=====================================");
    }
}
