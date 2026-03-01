package com.example.resourceservice.tracing;

public final class TraceConstants {
    private TraceConstants() {}

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String MDC_TRACE_ID_KEY = "xTraceId";
}
