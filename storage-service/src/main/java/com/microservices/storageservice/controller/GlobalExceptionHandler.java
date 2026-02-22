package com.microservices.storageservice.controller;

import com.microservices.storageservice.dto.ErrorResponse;
import com.microservices.storageservice.exception.InvalidIdException;
import com.microservices.storageservice.exception.StorageTypeAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StorageTypeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(StorageTypeAlreadyExistsException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidIdException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        var details = new HashMap<String, String>();

        e.getBindingResult()
            .getAllErrors()
            .forEach((error) -> {
                var field = ((FieldError) error).getField();
                var message = error.getDefaultMessage();
                details.put(field, message);
            });

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("Validation error", 400, details));
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
