package com.example.resourceservice.service.validation;

import com.example.resourceservice.exception.InvalidIdException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsvIdsParser {

    public List<Long> parse(String csvIds) {
        return Arrays.stream(csvIds.split(","))
            .map(String::trim)
            .map(s -> {
                try {
                    long id = Long.parseLong(s);
                    if (id <= 0) {
                        throw new InvalidIdException(String.format("Invalid ID format: '%s'. Only positive integers are allowed", s));
                    }
                    return id;
                } catch (NumberFormatException e) {
                    throw new InvalidIdException(String.format("Invalid ID format: '%s'. Only positive integers are allowed", s));
                }
            })
            .collect(Collectors.toList());
    }
}
