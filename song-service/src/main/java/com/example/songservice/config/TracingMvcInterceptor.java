package com.example.songservice.config;

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
                if (requestTraceId != null && !requestTraceId.isBlank()) {
                    MDC.put("traceId", requestTraceId);
                } else if (traceId == null) {
                    Span s = tracer.nextSpan().name("song-service:http:request").start();
                    tracer.withSpan(s);
                    traceId = s.context().traceId();
                }
                response.setHeader(traceHeader, (requestTraceId != null && !requestTraceId.isBlank()) ? requestTraceId : (traceId != null ? traceId : UUID.randomUUID().toString()));
                return true;
            }
        });
    }

    private String currentTraceId() {
        Span span = tracer.currentSpan();
        return span != null ? span.context().traceId() : MDC.get("traceId");
    }
}
