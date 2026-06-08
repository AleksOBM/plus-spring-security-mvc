package ru.practicum.security.dto;

import java.time.Instant;

public record TaskResponse(
        Long id,
        String ownerUsername,
        String title,
        String description,
        boolean completed,
        Instant createdAt,
        Instant completedAt
) {
}