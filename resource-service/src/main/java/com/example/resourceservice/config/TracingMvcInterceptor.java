package com.example.resourceservice.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.UUID;

@Configuration
public class TracingMvcInterceptor implements WebMvcConfigurer {

    private final Tracer tracer;
    private final String traceHeader;

    public TracingMvcInterceptor(Tracer tracer,
                                 @Value("${app.tracing.header:X-Trace-Id}") String traceHeader) {
        this.tracer = tracer;
        this.traceHeader = traceHeader;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull Object handler) {
                String requestTraceId = request.getHeader(traceHeader);
                String traceId = currentTraceId();
                if (requestTraceId == null || requestTraceId.isBlank()) {
                    if (traceId == null) {
                        // create a span to ensure trace is established
                        Span span = tracer.nextSpan().name("resource-service:http:request").start();
                        tracer.withSpan(span);
                        traceId = span.context().traceId();
                    }
                } else {
                    // if a custom trace header was provided, ensure it's in MDC too
                    MDC.put("traceId", requestTraceId);
                }
                // Always expose the header back to client
                response.setHeader(traceHeader, requestTraceId != null ? requestTraceId : (traceId != null ? traceId : UUID.randomUUID().toString()));
                return true;
            }
        });
    }

    private String currentTraceId() {
        Span span = tracer.currentSpan();
        return span != null ? span.context().traceId() : MDC.get("traceId");
    }
}
