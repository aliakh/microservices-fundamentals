package com.example.resourceservice.config;

import com.example.resourceservice.tracing.TraceConstants;
import com.example.resourceservice.tracing.TraceContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class FeignTraceRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FeignTraceRequestInterceptor.class);
//    private static final AtomicInteger idx = new AtomicInteger(0);
//    @Value("${app.tracing.header:X-Trace-Id}")
//    private String traceHeader;

    @Bean
    public RequestInterceptor requestInterceptor(/*Tracer tracer*/) {
        return (RequestTemplate template) -> {
            String traceId = TraceContext.getOrCreateTraceId();

            logger.info("Feign traceId2={}", traceId);

//            String traceId = currentTraceId(tracer);
//            if (traceId == null) {
//                traceId = "resource-service:feign:" + idx.getAndIncrement();
//            }
            template.header(TraceConstants.TRACE_ID_HEADER, traceId);
        };
    }

//    private String currentTraceId(Tracer tracer) {
//        Span span = tracer.currentSpan();
//        if (span != null) return span.context().traceId();
//        String mdc = MDC.get("traceId");
//        return (mdc != null && !mdc.isBlank()) ? mdc : null;
//    }
}
