package com.example.resourceservice.tracing;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignTraceRequestInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            var traceId = TraceContext.getTraceIdOrCreate();
            template.header(TraceConstants.TRACE_ID_HEADER, traceId);
        };
    }
}
