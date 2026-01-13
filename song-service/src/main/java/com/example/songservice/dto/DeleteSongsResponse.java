package com.example.songservice.dto;

import java.util.List;

public record DeleteSongsResponse(List<Long> ids) {
}
