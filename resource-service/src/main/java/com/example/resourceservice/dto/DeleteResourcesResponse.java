package com.example.resourceservice.dto;

import java.util.List;

public record DeleteResourcesResponse(List<Long> ids) {
}
