package com.example.resourceservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    String errorMessage,
    int errorCode,
    Map<String, String> details
) {
    public ErrorResponse(String errorMessage, int errorCode) {
        this(errorMessage, errorCode, null);
    }
}
