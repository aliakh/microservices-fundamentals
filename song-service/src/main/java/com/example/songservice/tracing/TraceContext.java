package com.example.songservice.tracing;

import org.slf4j.MDC;

import java.util.UUID;

public final class TraceContext {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    private TraceContext() {
    }

    public static String getTraceIdOrThrow() {
        var traceId = TRACE_ID.get();
        if (traceId == null || traceId.isEmpty()) {
            throw new RuntimeException("The thread-local trace id is blank");
        }
        return traceId;
    }

    public static String getTraceIdOrCreate() {
        var traceId = TRACE_ID.get();
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
            setTraceId(traceId);
        }
        return traceId;
    }

    public static void setTraceId(String traceId) {
        if (traceId == null || traceId.isEmpty()) {
            throw new RuntimeException("The parameter trace id is blank");
        }

        TRACE_ID.set(traceId);
        MDC.put(TraceConstants.TRACE_ID_MDC, traceId);
    }

    public static void clear() {
        TRACE_ID.remove();
        MDC.remove(TraceConstants.TRACE_ID_MDC);
    }
}

