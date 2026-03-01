package com.example.resourceprocessor.tracing;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignTraceRequestInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor(/*Tracer tracer*/) {
        return (RequestTemplate template) -> {
            var traceId = TraceContext.getOrCreateTraceId();
            template.header(TraceConstants.TRACE_ID_HEADER, traceId);
        };
    }
}
