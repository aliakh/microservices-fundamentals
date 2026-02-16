package com.microservices.storageservice.dto;

import java.util.List;

public record StoragesDeletedResponse(
    List<Long> ids
) {
}
