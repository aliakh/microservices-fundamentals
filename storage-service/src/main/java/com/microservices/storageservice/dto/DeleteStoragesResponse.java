package com.microservices.storageservice.dto;

import java.util.List;

public record DeleteStoragesResponse(List<Long> ids) {
}
