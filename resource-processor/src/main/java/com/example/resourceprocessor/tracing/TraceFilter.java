package com.example.resourceprocessor.tracing;

import com.example.resourceprocessor.tracing.TraceConstants;
import com.example.resourceprocessor.tracing.TraceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter extends OncePerRequestFilter {

//    private static final Logger log = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = request.getHeader(TraceConstants.TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = TraceContext.getOrCreateTraceId();
        } else {
            TraceContext.setTraceId(traceId);
        }

        logger.info("Filter traceId2="+ traceId);

        // Add to response as well
        response.setHeader(TraceConstants.TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContext.clear();
        }
    }
}
