package com.example.resourceservice.controller;

import com.example.resourceservice.dto.ErrorResponse;
import com.example.resourceservice.exception.InvalidIdException;
import com.example.resourceservice.exception.InvalidMp3FileException;
import com.example.resourceservice.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ResourceNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidMp3FileException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidMp3FileException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidIdException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e) {
        var value = e.getValue() != null ? e.getValue().toString() : "null";
        var message = String.format("Invalid value '%s' for ID. Must be a positive integer", value);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handle(HttpMediaTypeNotSupportedException e) {
        var value = e.getContentType() != null ? e.getContentType().toString() : "unknown";
        var message = String.format("Invalid file format: %s. Only MP3 files are allowed", value);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity
            .status(httpStatus)
            .body(new ErrorResponse(message, httpStatus.value()));
    }
}
