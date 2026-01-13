package com.example.resourceservice.service.validation;

import com.example.resourceservice.exception.InvalidIdException;
import org.springframework.stereotype.Component;

@Component
public class IdValidator {

    public void validate(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidIdException(String.format("Invalid value '%d' for ID. Must be a positive integer", id));
        }
    }
}
