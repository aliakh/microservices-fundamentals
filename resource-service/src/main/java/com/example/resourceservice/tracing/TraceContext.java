package com.example.resourceservice.tracing;

import org.slf4j.MDC;

import java.util.UUID;

public final class TraceContext {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    private TraceContext() {}

    public static String getTraceId() {
        String id = TRACE_ID.get();
//        if (id == null) {
//            id = MDC.get(TraceConstants.TRACE_ID_MDC);
//        }
        return id;
    }

    public static String getOrCreateTraceId() {
        String id = getTraceId();
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            setTraceId(id);
        }
        return id;
    }

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
//        if (traceId != null) {
            MDC.put(TraceConstants.TRACE_ID_MDC, traceId);
//        } else {
//            MDC.remove(TraceConstants.TRACE_ID_MDC);
//        }
    }

    public static void clear() {
        TRACE_ID.remove();
        MDC.remove(TraceConstants.TRACE_ID_MDC);
    }
}
