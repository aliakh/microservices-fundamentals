package com.example.resourceservice.service.validation;

import com.example.resourceservice.exception.InvalidIdException;
import org.springframework.stereotype.Component;

@Component
public class CsvIdsValidator {

    public void validate(String csvIds) {
        if (csvIds == null || csvIds.trim().isEmpty()) {
            throw new InvalidIdException("CSV string cannot be empty");
        }

        if (csvIds.length() > 200) {
            throw new InvalidIdException(String.format("CSV string is too long: received %d characters, maximum allowed is 200", csvIds.length()));
        }
    }
}
