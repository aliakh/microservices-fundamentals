package com.example.resourceservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class FeignTraceRequestInterceptor {

    @Value("${app.tracing.header:X-Trace-Id}")
    private String traceHeader;

    @Bean
    public RequestInterceptor requestInterceptor(Tracer tracer) {
        return (RequestTemplate template) -> {
            String traceId = currentTraceId(tracer);
            if (traceId == null) {
                traceId = UUID.randomUUID().toString();
            }
            template.header(traceHeader, traceId);
        };
    }

    private String currentTraceId(Tracer tracer) {
        Span span = tracer.currentSpan();
        if (span != null) return span.context().traceId();
        String mdc = MDC.get("traceId");
        return (mdc != null && !mdc.isBlank()) ? mdc : null;
    }
}
